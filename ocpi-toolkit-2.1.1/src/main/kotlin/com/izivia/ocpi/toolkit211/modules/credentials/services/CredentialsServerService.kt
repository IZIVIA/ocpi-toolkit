package com.izivia.ocpi.toolkit211.modules.credentials.services

import com.izivia.ocpi.toolkit.transport.domain.HttpMethod
import com.izivia.ocpi.toolkit.transport.domain.HttpRequest
import com.izivia.ocpi.toolkit211.common.*
import com.izivia.ocpi.toolkit211.modules.credentials.CredentialsInterface
import com.izivia.ocpi.toolkit211.modules.credentials.domain.Credentials
import com.izivia.ocpi.toolkit211.modules.credentials.repositories.CredentialsRoleRepository
import com.izivia.ocpi.toolkit211.modules.credentials.repositories.PartnerRepository
import com.izivia.ocpi.toolkit211.modules.versions.domain.ModuleID
import com.izivia.ocpi.toolkit211.modules.versions.domain.Version
import com.izivia.ocpi.toolkit211.modules.versions.domain.VersionDetails
import com.izivia.ocpi.toolkit211.modules.versions.domain.VersionNumber

open class CredentialsServerService(
    private val partnerRepository: PartnerRepository,
    private val credentialsRoleRepository: CredentialsRoleRepository,
    private val transportClientBuilder: TransportClientBuilder,
    private val serverVersionsUrlProvider: suspend () -> String,
    private val requiredEndpoints: List<ModuleID>?,
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
        val partnerId = partnerRepository.getPartnerIdByCredentialsServerToken(token)
            ?: partnerRepository.getPartnerIdByCredentialsTokenA(credentialsTokenA = token)
            ?: throw OcpiClientInvalidParametersException(
                "Invalid token ($token) - should be either a TokenA or a ServerToken",
            )

        partnerRepository.saveCredentialsRoles(
            partnerId = partnerId,
            credentialsRoles = credentials.roles,
        )

        partnerRepository.saveCredentialsClientToken(
            partnerId = partnerId,
            credentialsClientToken = credentials.token,
        )

        findLatestMutualVersionAndStoreInformation(
            partnerId = partnerId,
            credentials = credentials,
            debugHeaders = debugHeaders,
        )

        partnerRepository.invalidateCredentialsTokenA(partnerId = partnerId)

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
            ?: throw OcpiClientMethodNotAllowedException()

        partnerRepository.saveCredentialsRoles(
            partnerId = partnerId,
            credentialsRoles = credentials.roles,
        )

        partnerRepository.saveCredentialsClientToken(
            partnerId = partnerId,
            credentialsClientToken = credentials.token,
        )

        findLatestMutualVersionAndStoreInformation(
            partnerId = partnerId,
            credentials = credentials,
            debugHeaders = debugHeaders,
        )

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
                partnerRepository.invalidateCredentialsClientToken(partnerId = partnerId)
            }
            ?: throw OcpiClientMethodNotAllowedException()
    }

    private suspend fun findLatestMutualVersionAndStoreInformation(
        partnerId: String,
        credentials: Credentials,
        debugHeaders: Map<String, String>,
    ) {
        val versions = transportClientBuilder
            .build(credentials.url, credentials.token)
            .runCatching {
                send(
                    HttpRequest(method = HttpMethod.GET)
                        .withHeadersMixin(debugHeaders),
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

        val matchingVersion = versions.firstOrNull { it.version == VersionNumber.V2_1_1.value }
            ?: throw OcpiServerUnsupportedVersionException("Expected version 2.1.1 from $versions")

        partnerRepository.saveVersion(partnerId = partnerId, version = matchingVersion)

        val versionDetail = transportClientBuilder
            .build(matchingVersion.url, credentials.token)
            .runCatching {
                send(
                    HttpRequest(method = HttpMethod.GET)
                        .withHeadersMixin(debugHeaders),
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
