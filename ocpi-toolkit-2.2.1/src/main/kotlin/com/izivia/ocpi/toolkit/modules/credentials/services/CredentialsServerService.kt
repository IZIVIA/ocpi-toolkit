package com.izivia.ocpi.toolkit.modules.credentials.services

import com.izivia.ocpi.toolkit.common.*
import com.izivia.ocpi.toolkit.modules.credentials.CredentialsInterface
import com.izivia.ocpi.toolkit.modules.credentials.domain.Credentials
import com.izivia.ocpi.toolkit.modules.credentials.repositories.CredentialsRoleRepository
import com.izivia.ocpi.toolkit.modules.credentials.repositories.PartnerRepository
import com.izivia.ocpi.toolkit.modules.versions.domain.Version
import com.izivia.ocpi.toolkit.modules.versions.domain.VersionDetails
import com.izivia.ocpi.toolkit.modules.versions.domain.VersionNumber
import com.izivia.ocpi.toolkit.transport.TransportClientBuilder
import com.izivia.ocpi.toolkit.transport.domain.HttpMethod
import com.izivia.ocpi.toolkit.transport.domain.HttpRequest

open class CredentialsServerService(
    private val partnerRepository: PartnerRepository,
    private val credentialsRoleRepository: CredentialsRoleRepository,
    private val transportClientBuilder: TransportClientBuilder,
    private val serverVersionsUrlProvider: suspend () -> String,
    private val requiredEndpoints: RequiredEndpoints?,
) : CredentialsInterface {

    override suspend fun get(
        token: String,
    ): Credentials {
        return getCredentials(
            serverToken = token,
            partnerId = partnerRepository.getPartnerIdByCredentialsServerToken(token)
                ?: partnerRepository.getPartnerIdByCredentialsTokenA(credentialsTokenA = token)
                ?: throw OcpiClientInvalidParametersException(
                    "Invalid token ($token) - should be either a TokenA or a ServerToken",
                ),
        )
    }

    override suspend fun post(
        token: String,
        credentials: Credentials,
        debugHeaders: Map<String, String>,
    ): Credentials {
        // A partner can use a valid serverToken on registration.
        // It can happen when a partner unregister, then registers with its clientToken (which is the serverToken
        // for us)
        val partnerId = partnerRepository.getPartnerIdByCredentialsServerToken(token)
            // If we could not find a partner with this serverToken, then, it means that it's probably a tokenA
            ?: partnerRepository.getPartnerIdByCredentialsTokenA(credentialsTokenA = token)
            ?: throw OcpiClientInvalidParametersException(
                "Invalid token ($token) - should be either a TokenA or a ServerToken",
            )

        // Save credentials roles of partner
        partnerRepository.saveCredentialsRoles(
            partnerId = partnerId,
            credentialsRoles = credentials.roles,
        )

        // Save token B, which is in our case the client token, because it's the one that we will use to communicate
        // with the sender
        partnerRepository.saveCredentialsClientToken(
            partnerId = partnerId,
            credentialsClientToken = credentials.token,
        )

        findLatestMutualVersionAndStoreInformation(
            partnerId = partnerId,
            credentials = credentials,
            debugHeaders = debugHeaders,
        )

        // Remove token A because it is useless from now on
        partnerRepository.invalidateCredentialsTokenA(partnerId = partnerId)

        // Return Credentials object to sender with the token C inside (which is for us the server token)
        return getCredentials(
            serverToken = partnerRepository.saveCredentialsServerToken(
                partnerId = partnerId,
                credentialsServerToken = generateUUIDv4Token(),
            ),
            partnerId = partnerId,
        )
    }

    override suspend fun put(
        token: String,
        credentials: Credentials,
        debugHeaders: Map<String, String>,
    ): Credentials {
        val partnerId = partnerRepository.getPartnerIdByCredentialsServerToken(token)
            // This line should not be reached when using the HttpUtils.kt PartnerRepository.checkToken
            // implementation of the toolkit, but is included for custom implementations.
            ?: throw OcpiClientMethodNotAllowedException()

        // Save credentials roles of partner
        partnerRepository.saveCredentialsRoles(
            partnerId = partnerId,
            credentialsRoles = credentials.roles,
        )

        // In the payload, there is the new token B (the client token for us) to use to communicate with the receiver,
        // so we save it
        partnerRepository.saveCredentialsClientToken(
            partnerId = partnerId,
            credentialsClientToken = credentials.token,
        )

        // Update versions
        findLatestMutualVersionAndStoreInformation(
            partnerId = partnerId,
            credentials = credentials,
            debugHeaders = debugHeaders,
        )

        // Return Credentials objet to sender with the updated token C inside (which is for us the server token)
        return getCredentials(
            serverToken = partnerRepository.saveCredentialsServerToken(
                partnerId = partnerId,
                credentialsServerToken = generateUUIDv4Token(),
            ),
            partnerId = partnerId,
        )
    }

    override suspend fun delete(
        token: String,
    ) {
        partnerRepository
            .getPartnerIdByCredentialsServerToken(token)
            ?.also { partnerId ->
                // Only client token is invalidated. It means that the partner can still send authenticated requests
                // to the system.
                partnerRepository.invalidateCredentialsClientToken(partnerId = partnerId)
            }
            // This line should not be reached when using the HttpUtils.kt PartnerRepository.checkToken
            // implementation of the toolkit, but is included for custom implementations.
            ?: throw OcpiClientMethodNotAllowedException()
    }

    private suspend fun findLatestMutualVersionAndStoreInformation(
        partnerId: String,
        credentials: Credentials,
        debugHeaders: Map<String, String>,
    ) {
        val versions = transportClientBuilder
            .build(credentials.url)
            .runCatching {
                // we can not use VersionsClient::getVersions() without some refactoring
                // regular clients require an established connect and make too many assumptions around headers
                send(
                    HttpRequest(method = HttpMethod.GET)
                        .withUpdatedRequiredHeaders(
                            headers = debugHeaders,
                            generatedRequestId = generateRequestId(),
                        )
                        .authenticate(token = credentials.token),
                )
                    .parseResultList<Version>()
            }
            .onFailure {
                throw OcpiServerUnusableApiException(
                    "Could not get versions of sender, there was an error parsing the response: " +
                        "URL='${credentials.url}', error='${it.message}'",
                )
            }
            .getOrThrow()

        val matchingVersion = versions.firstOrNull { it.version == VersionNumber.V2_2_1.value }
            ?: throw OcpiServerUnsupportedVersionException("Expected version 2.2.1 from $versions")

        partnerRepository.saveVersion(partnerId = partnerId, version = matchingVersion)

        val versionDetail = transportClientBuilder
            .build(matchingVersion.url)
            .runCatching {
                send(
                    HttpRequest(method = HttpMethod.GET)
                        .withUpdatedRequiredHeaders(
                            headers = debugHeaders,
                            generatedRequestId = generateRequestId(),
                        )
                        .authenticate(token = credentials.token),
                )
                    .parseResult<VersionDetails>()
            }
            .onFailure {
                throw OcpiServerUnusableApiException(
                    "Could not get version of sender, there was an error parsing the response: " +
                        "URL='${matchingVersion.url}', error='${it.message}'",
                )
            }
            .getOrThrow()

        checkRequiredEndpoints(requiredEndpoints, versionDetail.endpoints)

        partnerRepository.saveEndpoints(partnerId = partnerId, endpoints = versionDetail.endpoints)
    }

    private suspend fun getCredentials(serverToken: String, partnerId: String): Credentials = Credentials(
        token = serverToken,
        url = serverVersionsUrlProvider(),
        roles = credentialsRoleRepository.getCredentialsRoles(partnerId),
    )
}
