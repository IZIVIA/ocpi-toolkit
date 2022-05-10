package ocpi.credentials.services

import common.*
import ocpi.credentials.CredentialsClient
import ocpi.credentials.domain.Credentials
import ocpi.credentials.repositories.CredentialsRoleRepository
import ocpi.credentials.repositories.PlatformRepository
import ocpi.versions.VersionsClient
import ocpi.versions.repositories.VersionsRepository

/**
 * Automates authentification process
 *
 * Note: credentialsClient & versionsClient must target the same platform since the automated process will check the
 * versions of the receiver before performing registration.
 *
 * - Client: the one doing the registration process
 * - Server: the one receiving the registration process
 *
 * @property clientVersionsEndpointUrl the versions endpoints url of the client (for the server to use)
 * @property clientPlatformRepository client's repository to store and retrieve tokens and information about platforms
 * @property clientVersionsRepository client's repository to retrieve available versions on the client
 * @property clientCredentialsRoleRepository client's repository to retrieve its role
 * @property serverUrl the url of the server
 * @property credentialsClient the credentials client the client will use to communicate with the server
 * @property versionsClient VersionsClient the versions client the client will use to retrieve versions of the server
 */
class CredentialsClientService(
    private val clientVersionsEndpointUrl: String,
    private val clientPlatformRepository: PlatformRepository,
    private val clientVersionsRepository: VersionsRepository,
    private val clientCredentialsRoleRepository: CredentialsRoleRepository,
    private val serverUrl: String,
    private val credentialsClient: CredentialsClient,
    private val versionsClient: VersionsClient,
) {
    fun get(): Credentials = clientPlatformRepository
        .getCredentialsTokenC(platformUrl = serverUrl)
        ?.let { tokenC ->
            credentialsClient
                .get(tokenC = tokenC)
                .let { it.data ?: throw OcpiResponseException(it.status_code, it.status_message ?: "unknown") }
        }
        ?: throw OcpiClientGenericException("Could not find CREDENTIALS_TOKEN_C associated with platform $serverUrl")

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
    fun register(): Credentials {
        // Get token provided by receiver outside the OCPI protocol (for example by an admin)
        val credentialsTokenA = clientPlatformRepository.getCredentialsTokenA(platformUrl = serverUrl)
            ?: throw OcpiClientInvalidParametersException("Could not find token A associated with platform $serverUrl")

        findLatestMutualVersionAndSaveInformation()

        // Generate token B
        val credentialsTokenB = clientPlatformRepository.saveCredentialsTokenB(
            platformUrl = serverUrl,
            credentialsTokenB = generateUUIDv4Token()
        )

        // Initiate registration process
        val credentials = credentialsClient.post(
            tokenA = credentialsTokenA,
            credentials = Credentials(
                token = credentialsTokenB,
                url = clientVersionsEndpointUrl,
                roles = clientCredentialsRoleRepository.getCredentialsRoles()
            ),
            debugHeaders = emptyMap()
        ).let {
            it.data ?: throw OcpiResponseException(it.status_code, it.status_message ?: "unknown")
        }

        // Store token C
        clientPlatformRepository.saveCredentialsTokenC(
            platformUrl = serverUrl,
            credentialsTokenC = credentials.token
        )

        // Remove token A because it is useless from now on
        clientPlatformRepository.removeCredentialsTokenA(platformUrl = serverUrl)

        return credentials
    }

    fun update(): Credentials {
        // Token to communicate with receiver
        val credentialsTokenC = clientPlatformRepository.getCredentialsTokenC(platformUrl = serverUrl)
            ?: throw OcpiClientInvalidParametersException("Could not find token C associated with platform $serverUrl")

        findLatestMutualVersionAndSaveInformation()

        // Generate token B
        val credentialsTokenB = clientPlatformRepository.saveCredentialsTokenB(
            platformUrl = serverUrl,
            credentialsTokenB = generateUUIDv4Token()
        )

        // Initiate registration process
        val credentials = credentialsClient.put(
            tokenC = credentialsTokenC,
            credentials = Credentials(
                token = credentialsTokenB,
                url = clientVersionsEndpointUrl,
                roles = clientCredentialsRoleRepository.getCredentialsRoles()
            ),
            debugHeaders = emptyMap()
        ).let {
            it.data ?: throw OcpiResponseException(it.status_code, it.status_message ?: "unknown")
        }

        // Store token C
        clientPlatformRepository.saveCredentialsTokenC(
            platformUrl = serverUrl,
            credentialsTokenC = credentials.token
        )

        return credentials
    }

    private fun findLatestMutualVersionAndSaveInformation() {
        val availableServerVersions = versionsClient
            .getVersions()
            .let {
                it.data ?: throw OcpiResponseException(it.status_code, it.status_message ?: "unknown")
            }
        val availableClientVersions = clientVersionsRepository.getVersions()

        // Get available versions and pick latest mutual
        val latestMutualVersion = availableClientVersions
            .sortedByDescending { clientVersion -> clientVersion.version.index }
            .firstOrNull { clientVersion ->
                availableServerVersions
                    .any { serverVersion -> serverVersion.version == clientVersion.version }
            }
            ?: throw OcpiServerUnsupportedVersionException("Could not find mutual version with platform $serverUrl")

        // Get available endpoints for a given version
        val versionDetails = versionsClient.getVersionDetails(
            versionNumber = latestMutualVersion.version.value
        ).let {
            it.data ?: throw OcpiResponseException(it.status_code, it.status_message ?: "unknown")
        }

        // Store version & endpoint
        clientPlatformRepository.saveVersion(platformUrl = serverUrl, version = latestMutualVersion)
        clientPlatformRepository.saveEndpoints(platformUrl = serverUrl, endpoints = versionDetails.endpoints)
    }

    fun delete() = clientPlatformRepository
        .getCredentialsTokenC(platformUrl = serverUrl)
        ?.let { tokenC ->
            credentialsClient
                .delete(tokenC = tokenC)
                .also {
                    if (it.status_code != OcpiStatus.SUCCESS.code)
                        throw OcpiResponseException(it.status_code, it.status_message ?: "unknown")
                }
        }
        ?: throw OcpiClientGenericException("Could not find CREDENTIALS_TOKEN_C associated with platform $serverUrl")
}