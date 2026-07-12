package com.izivia.ocpi.toolkit211.modules.credentials.services

import com.izivia.ocpi.toolkit211.common.*
import com.izivia.ocpi.toolkit211.modules.credentials.CredentialsClient
import com.izivia.ocpi.toolkit211.modules.credentials.domain.Credentials
import com.izivia.ocpi.toolkit211.modules.credentials.domain.CredentialsDetails
import com.izivia.ocpi.toolkit211.modules.credentials.domain.toDetails
import com.izivia.ocpi.toolkit211.modules.credentials.repositories.CredentialsDetailsRepository
import com.izivia.ocpi.toolkit211.modules.credentials.repositories.PartnerRepository
import com.izivia.ocpi.toolkit211.modules.versions.VersionDetailsClient
import com.izivia.ocpi.toolkit211.modules.versions.VersionsClient
import com.izivia.ocpi.toolkit211.modules.versions.domain.Endpoint
import com.izivia.ocpi.toolkit211.modules.versions.domain.ModuleID
import com.izivia.ocpi.toolkit211.modules.versions.domain.parseVersionNumber
import com.izivia.ocpi.toolkit211.modules.versions.repositories.VersionsRepository

open class CredentialsClientService(
    private val clientVersionsEndpointUrl: String,
    private val clientPartnerRepository: PartnerRepository,
    private val clientVersionsRepository: VersionsRepository,
    private val clientCredentialsDetailsRepository: CredentialsDetailsRepository,
    private val partnerId: String,
    private val transportClientBuilder: TransportClientBuilder,
    private val requiredEndpoints: List<ModuleID>?,
) {
    suspend fun get(): Credentials = clientPartnerRepository
        .getCredentialsClientToken(partnerId = partnerId)
        ?.let { clientToken ->
            buildCredentialClient()
                .get(token = clientToken)
                .validate()
        }
        ?: throw OcpiClientGenericException(
            "Could not find CREDENTIALS_TOKEN_C associated with partner $partnerId",
        )

    suspend fun register(): Credentials {
        val credentialsTokenA =
            clientPartnerRepository.getCredentialsClientToken(partnerId = partnerId)
                ?: clientPartnerRepository.getCredentialsTokenA(partnerId = partnerId)
                ?: throw OcpiClientInvalidParametersException(
                    "Could not find the TokenA or the ClientToken associated with partner $partnerId",
                )

        findLatestMutualVersionAndSaveInformation()

        val serverToken = clientPartnerRepository.saveCredentialsServerToken(
            partnerId = partnerId,
            credentialsServerToken = generateUUIDv4Token(),
        )

        val credentials = buildCredentialClient(allowTokenA = true).post(
            token = credentialsTokenA,
            credentials = buildCredentials(serverToken),
            debugHeaders = emptyMap(),
        ).validate()

        clientPartnerRepository.saveCredentialsDetails(
            partnerId = partnerId,
            credentialsDetails = credentials.toDetails(),
        )

        clientPartnerRepository.saveCredentialsClientToken(
            partnerId = partnerId,
            credentialsClientToken = credentials.token,
        )

        clientPartnerRepository.invalidateCredentialsTokenA(partnerId = partnerId)

        return credentials
    }

    suspend fun update(): Credentials {
        val credentialsClientToken =
            clientPartnerRepository.getCredentialsClientToken(partnerId = partnerId)
                ?: throw OcpiClientInvalidParametersException(
                    "Could not find the ClientToken associated with partner $partnerId",
                )

        findLatestMutualVersionAndSaveInformation()

        val credentialsServerToken = clientPartnerRepository.saveCredentialsServerToken(
            partnerId = partnerId,
            credentialsServerToken = generateUUIDv4Token(),
        )

        val credentials = buildCredentialClient().put(
            token = credentialsClientToken,
            credentials = buildCredentials(credentialsServerToken),
            debugHeaders = emptyMap(),
        ).validate()

        clientPartnerRepository.saveCredentialsDetails(
            partnerId = partnerId,
            credentialsDetails = credentials.toDetails(),
        )

        clientPartnerRepository.saveCredentialsClientToken(
            partnerId = partnerId,
            credentialsClientToken = credentials.token,
        )

        return credentials
    }

    suspend fun delete() = clientPartnerRepository
        .getCredentialsClientToken(partnerId = partnerId)
        ?.let { clientToken ->
            buildCredentialClient()
                .delete(token = clientToken)
                .also {
                    clientPartnerRepository.invalidateCredentialsServerToken(partnerId = partnerId)
                }
        }
        ?: throw OcpiClientGenericException(
            "Could not find client token associated with partner $partnerId",
        )

    private suspend fun findLatestMutualVersionAndSaveInformation(): List<Endpoint> {
        val availableServerVersions = VersionsClient(
            transportClientBuilder = transportClientBuilder,
            partnerId = partnerId,
            partnerRepository = clientPartnerRepository,
        )
            .getVersions()
        val availableClientVersionNumbers = clientVersionsRepository.getVersions()

        val latestMutualVersion = availableServerVersions
            .sortedByDescending { clientVersion -> parseVersionNumber(clientVersion.version)?.index ?: 0 }
            .firstOrNull { serverVersion ->
                availableClientVersionNumbers
                    .any { clientVersionNumber -> serverVersion.version == clientVersionNumber.value }
            }
            ?: throw OcpiServerUnsupportedVersionException(
                "Could not find mutual version with partner $partnerId",
            )

        clientPartnerRepository.saveVersion(partnerId = partnerId, version = latestMutualVersion)

        val versionDetails = VersionDetailsClient(
            transportClientBuilder = transportClientBuilder,
            partnerId = partnerId,
            partnerRepository = clientPartnerRepository,
        )
            .getVersionDetails()

        checkRequiredEndpoints(requiredEndpoints, versionDetails.endpoints)

        return clientPartnerRepository.saveEndpoints(
            partnerId = partnerId,
            endpoints = versionDetails.endpoints,
        )
    }

    private suspend fun getOrFindEndpoints(): List<Endpoint> = clientPartnerRepository
        .getEndpoints(partnerId = partnerId)
        .takeIf { it.isNotEmpty() }
        ?: findLatestMutualVersionAndSaveInformation()

    private suspend fun buildCredentials(token: String): Credentials {
        val details: CredentialsDetails = clientCredentialsDetailsRepository.getCredentialsDetails(partnerId)
        return Credentials(
            token = token,
            url = clientVersionsEndpointUrl,
            businessDetails = details.businessDetails,
            partyId = details.partyId,
            countryCode = details.countryCode,
        )
    }

    private suspend fun buildCredentialClient(allowTokenA: Boolean = false): CredentialsClient = CredentialsClient(
        transportClient = transportClientBuilder
            .buildFor(
                partnerId = partnerId,
                baseUrl = getOrFindEndpoints()
                    .find { it.identifier == ModuleID.credentials }
                    ?.url
                    ?: throw OcpiServerUnsupportedVersionException(
                        "No credentials endpoint for $partnerId",
                    ),
                allowTokenA = allowTokenA,
            ),
    )
}
