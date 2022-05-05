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
): CredentialsInterface {

    override fun get() {
        TODO("Not yet implemented")
    }

    override fun post(
        tokenA: String,
        credentials: Credentials
    ): OcpiResponseBody<Credentials> = OcpiResponseBody.of {
        val platform = platformRepository.getPlatformByTokenA(tokenA)
            ?: throw OcpiClientInvalidParametersException("Invalid CREDENTIALS_TOKEN_A ($tokenA)")

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

        // TODO: Should we do something about roles ?

        val tokenC = platformRepository.saveCredentialsTokenC(platformUrl = platform, credentialsTokenC = generateUUIDv4Token())
        platformRepository.saveVersion(platformUrl = platform, version = matchingVersion)
        platformRepository.saveEndpoints(platformUrl = platform, endpoints = versionDetail.endpoints)
        platformRepository.removeCredentialsTokenA(platformUrl = platform)

        Credentials(
            token = tokenC,
            url = serverUrl,
            roles = credentialsRoleRepository.getCredentialsRoles()
        )
    }

    override fun put() {
        TODO("Not yet implemented")
    }

    override fun delete() {
        TODO("Not yet implemented")
    }
}