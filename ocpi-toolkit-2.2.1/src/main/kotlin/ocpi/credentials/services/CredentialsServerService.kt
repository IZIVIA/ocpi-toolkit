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
    private val serverVersionsUrl: String
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
        credentials: Credentials,
        debugHeaders: Map<String, String>
    ): OcpiResponseBody<Credentials> = OcpiResponseBody.of {
        val platformUrl = platformRepository.getPlatformByTokenA(tokenA)
            ?: throw OcpiClientInvalidParametersException("Invalid CREDENTIALS_TOKEN_A ($tokenA)")

        findLatestMutualVersionAndStoreInformation(
            credentials = credentials,
            debugHeaders = debugHeaders
        )

        platformRepository.removeCredentialsTokenA(platformUrl = platformUrl)

        getCredentials(
            token = platformRepository.saveCredentialsTokenC(
                platformUrl = platformUrl,
                credentialsTokenC = generateUUIDv4Token()
            )
        )
    }

    override fun put(
        tokenC: String,
        credentials: Credentials,
        debugHeaders: Map<String, String>
    ): OcpiResponseBody<Credentials> = OcpiResponseBody.of {
        val platformUrl = platformRepository.getPlatformByTokenC(tokenC)
            ?: throw OcpiClientInvalidParametersException("Invalid CREDENTIALS_TOKEN_C ($tokenC)")

        findLatestMutualVersionAndStoreInformation(
            credentials = credentials,
            debugHeaders = debugHeaders
        )

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

    private fun findLatestMutualVersionAndStoreInformation(
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

    private fun getCredentials(token: String): Credentials = Credentials(
        token = token,
        url = serverVersionsUrl,
        roles = credentialsRoleRepository.getCredentialsRoles()
    )
}