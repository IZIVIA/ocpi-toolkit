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
        tokenC: String
    ): OcpiResponseBody<Credentials> = OcpiResponseBody.of {
        platformRepository
            .getPlatformByTokenC(tokenC)
            ?.let { platformUrl ->
                getCredentials(
                    token = platformRepository.getCredentialsTokenC(platformUrl)
                        ?: throw OcpiClientInvalidParametersException(
                            "Could not find CREDENTIALS_TOKEN_C associated with platform $platformUrl"
                        )
                )
            }
            ?: throw OcpiClientInvalidParametersException("Invalid CREDENTIALS_TOKEN_C ($tokenC)")
    }

    override suspend fun post(
        tokenA: String,
        credentials: Credentials,
        debugHeaders: Map<String, String>
    ): OcpiResponseBody<Credentials> = OcpiResponseBody.of {
        val platformUrl = platformRepository.savePlatformUrlForTokenA(
            tokenA = tokenA,
            platformUrl = credentials.url
        ) ?: throw OcpiClientInvalidParametersException("Invalid CREDENTIALS_TOKEN_A ($tokenA)")

        platformRepository.saveCredentialsTokenB(platformUrl = credentials.url, credentialsTokenB = credentials.token)

        findLatestMutualVersionAndStoreInformation(credentials = credentials, debugHeaders = debugHeaders)

        platformRepository.removeCredentialsTokenA(platformUrl = platformUrl)
        platformRepository.removeCredentialsTokenB(platformUrl = platformUrl)

        getCredentials(
            token = platformRepository.saveCredentialsTokenC(
                platformUrl = platformUrl,
                credentialsTokenC = generateUUIDv4Token()
            )
        )
    }

    override suspend fun put(
        tokenC: String,
        credentials: Credentials,
        debugHeaders: Map<String, String>
    ): OcpiResponseBody<Credentials> = OcpiResponseBody.of {
        val platformUrl = platformRepository.getPlatformByTokenC(tokenC)
            ?: throw OcpiClientInvalidParametersException("Invalid CREDENTIALS_TOKEN_C ($tokenC)")

        platformRepository.saveCredentialsTokenB(platformUrl = credentials.url, credentialsTokenB = credentials.token)

        findLatestMutualVersionAndStoreInformation(credentials = credentials, debugHeaders = debugHeaders)

        platformRepository.removeCredentialsTokenA(platformUrl = platformUrl)
        platformRepository.removeCredentialsTokenB(platformUrl = platformUrl)

        getCredentials(
            token = platformRepository.saveCredentialsTokenC(
                platformUrl = platformUrl,
                credentialsTokenC = generateUUIDv4Token()
            )
        )
    }

    override suspend fun delete(
        tokenC: String
    ): OcpiResponseBody<Credentials?> = OcpiResponseBody.of {
        platformRepository
            .getPlatformByTokenC(tokenC)
            ?.also { platformUrl ->
                platformRepository.removeVersion(platformUrl = platformUrl)
                platformRepository.removeEndpoints(platformUrl = platformUrl)
                platformRepository.removeCredentialsTokenC(platformUrl = platformUrl)
            }
            ?: throw OcpiClientInvalidParametersException("Invalid CREDENTIALS_TOKEN_C ($tokenC)")

        null
    }

    private suspend fun findLatestMutualVersionAndStoreInformation(
        credentials: Credentials,
        debugHeaders: Map<String, String>
    ) {
        val versions = transportClientBuilder
            .build(credentials.url)
            .send(
                HttpRequest(method = HttpMethod.GET)
                    .withUpdatedDebugHeaders(headers = debugHeaders)
                    .authenticate(token = credentials.token)
            )
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
            .send(
                HttpRequest(method = HttpMethod.GET)
                    .withUpdatedDebugHeaders(headers = debugHeaders)
                    .authenticate(token = credentials.token)
            )
            .parseBody<OcpiResponseBody<VersionDetails>>()
            .let {
                it.data
                    ?: throw OcpiServerGenericException(
                        "Could not get version of sender, there was an error during the call: '${it.status_message}'"
                    )
            }

        platformRepository.saveEndpoints(platformUrl = credentials.url, endpoints = versionDetail.endpoints)
    }

    private suspend fun getCredentials(token: String): Credentials = Credentials(
        token = token,
        url = serverVersionsUrlProvider(),
        roles = credentialsRoleRepository.getCredentialsRoles()
    )
}
