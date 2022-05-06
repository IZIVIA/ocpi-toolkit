package ocpi.credentials.services

import common.*
import ocpi.credentials.CredentialsInterface
import ocpi.credentials.domain.Credentials
import ocpi.credentials.repositories.PlatformRepository
import ocpi.locations.domain.BusinessDetails
import ocpi.versions.domain.Version
import ocpi.versions.domain.VersionDetails
import ocpi.versions.domain.VersionNumber
import transport.TransportClientBuilder
import transport.domain.HttpMethod
import transport.domain.HttpRequest

class CredentialsServerService(
    private val platformRepository: PlatformRepository,
    private val serverBusinessDetails: BusinessDetails,
    private val serverPartyId: String,
    private val serverCountryCode: String,
    private val transportClientBuilder: TransportClientBuilder,
    private val serverUrl: String
) : CredentialsInterface {

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

        findLatestMutualVersionAndStoreInformation(platformUrl = platformUrl, credentials = credentials)

        platformRepository.removeCredentialsTokenA(platformUrl = platformUrl)

        getCredentials(
            token = platformRepository.saveCredentialsTokenC(
                platformUrl = platformUrl,
                credentialsTokenC = generateUUIDv4Token()
            )
        )
    }

    override fun put(tokenC: String, credentials: Credentials): OcpiResponseBody<Credentials> = OcpiResponseBody.of {
        val platformUrl = platformRepository.getPlatformByTokenC(tokenC)
            ?: throw OcpiClientInvalidParametersException("Invalid CREDENTIALS_TOKEN_C ($tokenC)")

        findLatestMutualVersionAndStoreInformation(platformUrl = platformUrl, credentials = credentials)

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

    private fun findLatestMutualVersionAndStoreInformation(platformUrl: String, credentials: Credentials) {
        val versions = transportClientBuilder
            .build(credentials.url)
            .send(
                HttpRequest(
                    method = HttpMethod.GET,
                    path = "/",
                    headers = mapOf(authorizationHeader(token = credentials.token))
                )
            )
            .parseBody<OcpiResponseBody<List<Version>>>()
            .let {
                it.data
                    ?: throw OcpiServerGenericException(
                        "Could not get versions of sender, there was an error during the call: '${it.status_message}'"
                    )
            }

        val matchingVersion = versions.firstOrNull { it.version == VersionNumber.V2_1_1 }
            ?: throw OcpiServerNoMatchingEndpointsException("Expected version 2.1.1 from $versions")

        val versionDetail = transportClientBuilder
            .build(matchingVersion.url)
            .send(
                HttpRequest(
                    method = HttpMethod.GET,
                    path = "",
                    headers = mapOf(authorizationHeader(token = credentials.token))
                )
            )
            .parseBody<OcpiResponseBody<VersionDetails>>()
            .let {
                it.data
                    ?: throw OcpiServerGenericException(
                        "Could not get version of sender, there was an error during the call: '${it.status_message}'"
                    )
            }

        platformRepository.saveVersion(platformUrl = platformUrl, version = matchingVersion)
        platformRepository.saveEndpoints(platformUrl = platformUrl, endpoints = versionDetail.endpoints)
    }

    private fun getCredentials(token: String): Credentials = Credentials(
        token = token,
        url = serverUrl,
        business_details = serverBusinessDetails,
        party_id = serverPartyId,
        country_code = serverCountryCode
    )
}