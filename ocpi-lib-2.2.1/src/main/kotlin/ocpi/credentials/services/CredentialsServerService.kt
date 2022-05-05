package ocpi.credentials.services

import common.*
import ocpi.credentials.CredentialsInterface
import ocpi.credentials.domain.Credentials
import ocpi.credentials.repositories.CredentialsRoleRepository
import ocpi.credentials.repositories.PlatformRepository
import ocpi.versions.domain.Version
import ocpi.versions.domain.VersionDetails
import ocpi.versions.domain.VersionNumber
import transport.TransportClientBuilder
import transport.domain.HttpMethod
import transport.domain.HttpRequest

class CredentialsServerService(
    private val platformRepository: PlatformRepository,
    private val credentialsRoleRepository: CredentialsRoleRepository,
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

    override fun delete() {
        TODO("Not yet implemented")
    }

    private fun findLatestMutualVersionAndStoreInformation(platformUrl: String, credentials: Credentials) {
        val versions = transportClientBuilder
            .build(credentials.url)
            .send(
                HttpRequest(
                    method = HttpMethod.GET,
                    path = "/",
                    headers = mapOf(
                        "Authorization" to "Token ${credentials.token.encodeBase64()}"
                    )
                )
            )
            .parseBody<OcpiResponseBody<List<Version>>>()
            .data
            ?: throw OcpiServerGenericException("Could not get versions of sender")

        val matchingVersion = versions.firstOrNull { it.version == VersionNumber.V2_2_1 }
            ?: throw OcpiServerNoMatchingEndpointsException("Expected version 2.2.1 from $versions")

        val versionDetail = transportClientBuilder
            .build(matchingVersion.url)
            .send(
                HttpRequest(
                    method = HttpMethod.GET,
                    path = "",
                    headers = mapOf(
                        "Authorization" to "Token ${credentials.token.encodeBase64()}"
                    )
                )
            )
            .parseBody<OcpiResponseBody<VersionDetails>>()
            .data
            ?: throw OcpiServerGenericException("Could not get versions of sender")

        platformRepository.saveVersion(platformUrl = platformUrl, version = matchingVersion)
        platformRepository.saveEndpoints(platformUrl = platformUrl, endpoints = versionDetail.endpoints)
    }

    private fun getCredentials(token: String): Credentials = Credentials(
        token = token,
        url = serverUrl,
        roles = credentialsRoleRepository.getCredentialsRoles()
    )
}