package ocpi.credentials.services

import common.OcpiClientInvalidParametersException
import common.OcpiResponseException
import common.OcpiServerUnsupportedVersionException
import common.generateUUIDv4Token
import ocpi.credentials.CredentialsClient
import ocpi.credentials.domain.Credentials
import ocpi.credentials.repositories.CredentialsRoleRepository
import ocpi.credentials.repositories.PlatformRepository
import ocpi.versions.VersionsClient
import ocpi.versions.repositories.VersionsRepository

/**
 * Automates authentification process
 *
 * Note: credentialsClient & versionsClient must target the same platform
 *
 * @property clientPlatformRepository the repository to store credential information about platforms with whom the client may
 * communicate
 * @property versionsRepository the repository to retrieve versions of the client
 * @property credentialsClient the client to make requests to the credentials endpoint of the server
 * @property versionsClient the client to check & synchronize client / server versions
 */
class CredentialsClientService(
    private val clientPlatformRepository: PlatformRepository,
    private val versionsRepository: VersionsRepository,
    private val credentialsRoleRepository: CredentialsRoleRepository,
    private val credentialsClient: CredentialsClient,
    private val versionsClient: VersionsClient
) {
    /**
     * To start using OCPI, the Platforms will need to exchange credentials tokens.
     *
     * To start the exchange of credentials tokens, one platform has to be selected as Sender for the Credentials
     * module. This has to be decided between the Platforms (outside of OCPI) before they first connect.
     *
     * To start the credentials exchange, the Receiver Platform must create a unique credentials token:
     * CREDENTIALS_TOKEN_A that has to be used to authorize the Sender until the credentials exchange is finished. This
     * credentials token along with the versions endpoint SHOULD be sent to the Sender in a secure way that is outside
     * the scope of this protocol.
     *
     * The Sender starts the registration process, retrieves the version information and details (using
     * CREDENTIALS_TOKEN_A in the HTTP Authorization header). The Sender generates a unique credentials token:
     * CREDENTIALS_TOKEN_B, sends it to the Receiver in a POST request to the credentials module of the Receiver. The
     * Receiver stores CREDENTIALS_TOKEN_B and uses it for any requests to the Sender Platform, including the version
     * information and details.
     *
     * The Receiver generates a unique credentials token: CREDENTIALS_TOKEN_C and returns it to the Sender in the
     * response to the POST request from the Sender.
     *
     * After the credentials exchange has finished, the Sender SHALL use CREDENTIALS_TOKEN_C in future OCPI request to
     * the Receiver Platform. The CREDENTIALS_TOKEN_A can then be thrown away, it MAY no longer be used.
     *
     * @return the credentials to use when communicating with the server (receiver)
     */
    fun register(clientVersionsEndpointUrl: String, platformUrl: String): Credentials {
        // Get token provided by receiver outside the OCPI protocol (for example by an admin)
        val credentialsTokenA = clientPlatformRepository.getCredentialsTokenA(platformUrl = platformUrl)
            ?: throw OcpiClientInvalidParametersException("Could not find token A associated with platform $platformUrl")

        findLatestMutualVersionAndSaveInformation(platformUrl = platformUrl, token = credentialsTokenA)

        // Generate token B
        val credentialsTokenB = clientPlatformRepository.saveCredentialsTokenB(
            platformUrl = platformUrl,
            credentialsTokenB = generateUUIDv4Token()
        )

        // Initiate registration process
        val credentials = credentialsClient.post(
            tokenA = credentialsTokenA,
            credentials = Credentials(
                token = credentialsTokenB,
                url = clientVersionsEndpointUrl,
                roles = credentialsRoleRepository.getCredentialsRoles()
            )
        ).let {
            it.data ?: throw OcpiResponseException(it.status_code, it.status_message ?: "unknown")
        }

        // Store token C
        clientPlatformRepository.saveCredentialsTokenC(
            platformUrl = platformUrl,
            credentialsTokenC = credentials.token
        )

        // Remove token A because it is useless from now on
        clientPlatformRepository.removeCredentialsTokenA(platformUrl = platformUrl)

        return credentials
    }

    fun update(clientVersionsEndpointUrl: String, platformUrl: String): Credentials {
        // Token to communicate with receiver
        val credentialsTokenC = clientPlatformRepository.getCredentialsTokenC(platformUrl = platformUrl)
            ?: throw OcpiClientInvalidParametersException("Could not find token C associated with platform $platformUrl")

        findLatestMutualVersionAndSaveInformation(platformUrl = platformUrl, token = credentialsTokenC)

        // Generate token B
        val credentialsTokenB = clientPlatformRepository.saveCredentialsTokenB(
            platformUrl = platformUrl,
            credentialsTokenB = generateUUIDv4Token()
        )

        // Initiate registration process
        val credentials = credentialsClient.put(
            tokenC = credentialsTokenC,
            credentials = Credentials(
                token = credentialsTokenB,
                url = clientVersionsEndpointUrl,
                roles = credentialsRoleRepository.getCredentialsRoles()
            )
        ).let {
            it.data ?: throw OcpiResponseException(it.status_code, it.status_message ?: "unknown")
        }

        // Store token C
        clientPlatformRepository.saveCredentialsTokenC(
            platformUrl = platformUrl,
            credentialsTokenC = credentials.token
        )

        return credentials
    }

    private fun findLatestMutualVersionAndSaveInformation(platformUrl: String, token: String) {
        val availableServerVersions = versionsClient
            .getVersions(token = token)
            .let {
                it.data ?: throw OcpiResponseException(it.status_code, it.status_message ?: "unknown")
            }
        val availableClientVersions = versionsRepository.getVersions()

        // Get available versions and pick latest mutual
        val latestMutualVersion = availableClientVersions
            .sortedByDescending { clientVersion -> clientVersion.version.index }
            .firstOrNull { clientVersion ->
                availableServerVersions
                    .any { serverVersion -> serverVersion.version == clientVersion.version }
            }
            ?: throw OcpiServerUnsupportedVersionException("Could not find mutual version with platform $platformUrl")

        // Get available endpoints for a given version
        val versionDetails = versionsClient.getVersionDetails(
            token = token,
            versionNumber = latestMutualVersion.version.value
        ).let {
            it.data ?: throw OcpiResponseException(it.status_code, it.status_message ?: "unknown")
        }

        // Store version & endpoint
        clientPlatformRepository.saveVersion(platformUrl = platformUrl, version = latestMutualVersion)
        clientPlatformRepository.saveEndpoints(platformUrl = platformUrl, endpoints = versionDetails.endpoints)
    }

}