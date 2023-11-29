package com.izivia.ocpi.toolkit.modules.credentials.services

import com.izivia.ocpi.toolkit.common.*
import com.izivia.ocpi.toolkit.modules.credentials.CredentialsInterface
import com.izivia.ocpi.toolkit.modules.credentials.domain.Credentials
import com.izivia.ocpi.toolkit.modules.credentials.repositories.CredentialsRoleRepository
import com.izivia.ocpi.toolkit.modules.credentials.repositories.PlatformRepository
import com.izivia.ocpi.toolkit.modules.versions.domain.Version
import com.izivia.ocpi.toolkit.modules.versions.domain.VersionDetails
import com.izivia.ocpi.toolkit.modules.versions.domain.VersionNumber
import com.izivia.ocpi.toolkit.transport.TransportClientBuilder
import com.izivia.ocpi.toolkit.transport.domain.HttpMethod
import com.izivia.ocpi.toolkit.transport.domain.HttpRequest

class CredentialsServerService(
    private val platformRepository: PlatformRepository,
    private val credentialsRoleRepository: CredentialsRoleRepository,
    private val transportClientBuilder: TransportClientBuilder,
    private val serverVersionsUrlProvider: suspend () -> String
) : CredentialsInterface {

    override suspend fun get(
        token: String
    ): OcpiResponseBody<Credentials> = OcpiResponseBody.of {
        getCredentials(
            serverToken = token
        )
    }

    override suspend fun post(
        tokenA: String,
        credentials: Credentials,
        debugHeaders: Map<String, String>
    ): OcpiResponseBody<Credentials> = OcpiResponseBody.of {
        val platformUrl = platformRepository.savePlatformUrlForTokenA(
            tokenA = tokenA,
            platformUrl = credentials.url
        ) ?: throw OcpiClientInvalidParametersException("Invalid token A ($tokenA)")

        // Save credentials roles of partner
        platformRepository.saveCredentialsRoles(
            platformUrl = credentials.url,
            credentialsRoles = credentials.roles
        )

        // Save token B, which is in our case the client token, because it's the one that we will use to communicate
        // with the sender
        platformRepository.saveCredentialsClientToken(
            platformUrl = credentials.url,
            credentialsClientToken = credentials.token
        )

        findLatestMutualVersionAndStoreInformation(credentials = credentials, debugHeaders = debugHeaders)

        // Remove token A because it is useless from now on
        platformRepository.invalidateCredentialsTokenA(platformUrl = platformUrl)

        // Return Credentials objet to sender with the token C inside (which is for us the server token)
        getCredentials(
            serverToken = platformRepository.saveCredentialsServerToken(
                platformUrl = platformUrl,
                credentialsServerToken = generateUUIDv4Token()
            )
        )
    }

    override suspend fun put(
        token: String,
        credentials: Credentials,
        debugHeaders: Map<String, String>
    ): OcpiResponseBody<Credentials> = OcpiResponseBody.of {
        val platformUrl = platformRepository.getPlatformUrlByCredentialsServerToken(token)
            ?: throw OcpiClientInvalidParametersException("Invalid server token ($token)")

        // Save credentials roles of partner
        platformRepository.saveCredentialsRoles(
            platformUrl = credentials.url,
            credentialsRoles = credentials.roles
        )

        // In the payload, there is the new token B (the client token for us) to use to communicate with the receiver,
        // so we save it
        platformRepository.saveCredentialsClientToken(
            platformUrl = credentials.url,
            credentialsClientToken = credentials.token
        )

        // Update versions
        findLatestMutualVersionAndStoreInformation(credentials = credentials, debugHeaders = debugHeaders)

        // Return Credentials objet to sender with the updated token C inside (which is for us the server token)
        getCredentials(
            serverToken = platformRepository.saveCredentialsServerToken(
                platformUrl = platformUrl,
                credentialsServerToken = generateUUIDv4Token()
            )
        )
    }

    override suspend fun delete(
        token: String
    ): OcpiResponseBody<Credentials?> = OcpiResponseBody.of {
        platformRepository
            .getPlatformUrlByCredentialsServerToken(token)
            ?.also { platformUrl ->
                platformRepository.unregisterPlatform(platformUrl = platformUrl)
            }
            ?: throw OcpiClientInvalidParametersException("Invalid server token ($token)")

        null
    }

    private suspend fun findLatestMutualVersionAndStoreInformation(
        credentials: Credentials,
        debugHeaders: Map<String, String>
    ) {
        val versions = transportClientBuilder
            .build(credentials.url)
            .run {
                send(
                    HttpRequest(method = HttpMethod.GET)
                        .withUpdatedRequiredHeaders(
                            headers = debugHeaders,
                            generatedRequestId = generateRequestId()
                        )
                        .authenticate(token = credentials.token)
                )
            }
            .parseBody<OcpiResponseBody<List<Version>>>()
            .let {
                it.data
                    ?: throw OcpiServerGenericException(
                        "Could not get versions of sender, there was an error during the call: '${it.status_message}'"
                    )
            }

        val matchingVersion = versions.firstOrNull { it.version == VersionNumber.V2_2_1.value }
            ?: throw OcpiServerNoMatchingEndpointsException("Expected version 2.2.1 from $versions")

        platformRepository.saveVersion(platformUrl = credentials.url, version = matchingVersion)

        val versionDetail = transportClientBuilder
            .build(matchingVersion.url)
            .run {
                send(
                    HttpRequest(method = HttpMethod.GET)
                        .withUpdatedRequiredHeaders(
                            headers = debugHeaders,
                            generatedRequestId = generateRequestId()
                        )
                        .authenticate(token = credentials.token)
                )
            }
            .parseBody<OcpiResponseBody<VersionDetails>>()
            .let {
                it.data
                    ?: throw OcpiServerGenericException(
                        "Could not get version of sender, there was an error during the call: '${it.status_message}'"
                    )
            }

        platformRepository.saveEndpoints(platformUrl = credentials.url, endpoints = versionDetail.endpoints)
    }

    private suspend fun getCredentials(serverToken: String): Credentials = Credentials(
        token = serverToken,
        url = serverVersionsUrlProvider(),
        roles = credentialsRoleRepository.getCredentialsRoles()
    )
}
