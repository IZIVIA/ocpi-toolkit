package com.izivia.ocpi.toolkit.modules.credentials.services

import com.izivia.ocpi.toolkit.common.*
import com.izivia.ocpi.toolkit.modules.credentials.domain.Credentials
import com.izivia.ocpi.toolkit.modules.credentials.repositories.PlatformRepository
import com.izivia.ocpi.toolkit.modules.locations.domain.BusinessDetails
import com.izivia.ocpi.toolkit.modules.versions.VersionDetailsClient
import com.izivia.ocpi.toolkit.modules.versions.VersionsClient
import com.izivia.ocpi.toolkit.modules.versions.domain.VersionNumber
import com.izivia.ocpi.toolkit.transport.TransportClientBuilder

class CredentialsServerService(
    private val platformRepository: PlatformRepository,
    private val serverBusinessDetails: BusinessDetails,
    private val serverPartyId: String,
    private val serverCountryCode: String,
    private val transportClientBuilder: TransportClientBuilder,
    private val serverVersionsUrl: String
) : com.izivia.ocpi.toolkit.modules.credentials.CredentialsInterface {

    override fun get(
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

    override fun post(
        tokenA: String,
        credentials: Credentials
    ): OcpiResponseBody<Credentials> = OcpiResponseBody.of {
        val platformUrl = platformRepository.getPlatformByTokenA(tokenA)
            ?: throw OcpiClientInvalidParametersException("Invalid CREDENTIALS_TOKEN_A ($tokenA)")

        findLatestMutualVersionAndStoreInformation(credentials = credentials)

        platformRepository.removeCredentialsTokenA(platformUrl = platformUrl)

        getCredentials(
            token = platformRepository.saveCredentialsTokenC(
                platformUrl = platformUrl,
                credentialsTokenC = generateUUIDv4Token()
            )
        )
    }

    override fun delete(
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

    private fun findLatestMutualVersionAndStoreInformation(credentials: Credentials) {
        val versions = VersionsClient(
            transportClientBuilder = transportClientBuilder,
            serverVersionsEndpointUrl = credentials.url,
            platformRepository = platformRepository
        )
            .getVersions()
            .let {
                it.data
                    ?: throw OcpiServerGenericException(
                        "Could not get versions of sender, there was an error during the call: '${it.status_message}'"
                    )
            }

        val matchingVersion = versions.firstOrNull { it.version == VersionNumber.V2_1_1.value }
            ?: throw OcpiServerNoMatchingEndpointsException("Expected version 2.1.1 from $versions")

        platformRepository.saveVersion(platformUrl = credentials.url, version = matchingVersion)

        val versionDetail = VersionDetailsClient(
            transportClientBuilder = transportClientBuilder,
            serverVersionsEndpointUrl = credentials.url,
            platformRepository = platformRepository
        )
            .getVersionDetails()
            .let {
                it.data
                    ?: throw OcpiServerGenericException(
                        "Could not get version of sender, there was an error during the call: '${it.status_message}'"
                    )
            }

        platformRepository.saveEndpoints(platformUrl = credentials.url, endpoints = versionDetail.endpoints)
    }

    private fun getCredentials(token: String): Credentials = Credentials(
        token = token,
        url = serverVersionsUrl,
        business_details = serverBusinessDetails,
        party_id = serverPartyId,
        country_code = serverCountryCode
    )
}