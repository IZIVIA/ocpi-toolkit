package com.izivia.ocpi.toolkit.modules.credentials.services

import com.izivia.ocpi.toolkit.common.*
import com.izivia.ocpi.toolkit.modules.credentials.CredentialsClient
import com.izivia.ocpi.toolkit.modules.credentials.domain.Credentials
import com.izivia.ocpi.toolkit.modules.credentials.repositories.CredentialsRoleRepository
import com.izivia.ocpi.toolkit.modules.credentials.repositories.PlatformRepository
import com.izivia.ocpi.toolkit.modules.versions.VersionDetailsClient
import com.izivia.ocpi.toolkit.modules.versions.VersionsClient
import com.izivia.ocpi.toolkit.modules.versions.domain.Endpoint
import com.izivia.ocpi.toolkit.modules.versions.domain.ModuleID
import com.izivia.ocpi.toolkit.modules.versions.domain.parseVersionNumber
import com.izivia.ocpi.toolkit.modules.versions.repositories.VersionsRepository
import com.izivia.ocpi.toolkit.transport.TransportClientBuilder

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
 * @property serverVersionsEndpointUrl the versions endpoint url of the server (for the client to retrieve endpoints)
 * @property transportClientBuilder used to build a transport (will be used to create CredentialClient to make calls)
 */
class CredentialsClientService(
    private val clientVersionsEndpointUrl: String,
    private val clientPlatformRepository: PlatformRepository,
    private val clientVersionsRepository: VersionsRepository,
    private val clientCredentialsRoleRepository: CredentialsRoleRepository,
    private val serverVersionsEndpointUrl: String,
    private val transportClientBuilder: TransportClientBuilder
) {
    suspend fun get(): Credentials = clientPlatformRepository
        .getCredentialsClientToken(platformUrl = serverVersionsEndpointUrl)
        ?.let { clientToken ->
            buildCredentialClient()
                .get(token = clientToken)
                .let { it.data ?: throw OcpiResponseException(it.status_code, it.status_message ?: "unknown") }
        }
        ?: throw OcpiClientGenericException(
            "Could not find CREDENTIALS_TOKEN_C associated with platform $serverVersionsEndpointUrl"
        )

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
    suspend fun register(): Credentials {
        // Get token provided by receiver outside the OCPI protocol (for example by an admin)
        val credentialsTokenA = clientPlatformRepository.getCredentialsTokenA(platformUrl = serverVersionsEndpointUrl)
            ?: throw OcpiClientInvalidParametersException(
                "Could not find token A associated with platform $serverVersionsEndpointUrl"
            )

        findLatestMutualVersionAndSaveInformation()

        // Generate token B,  which is the server token in this case because we are the sender. It's this token
        // that the receiver will use to contact us.
        val serverToken = clientPlatformRepository.saveCredentialsServerToken(
            platformUrl = serverVersionsEndpointUrl,
            credentialsServerToken = generateUUIDv4Token()
        )

        // Initiate registration process
        val credentials = buildCredentialClient().post(
            tokenA = credentialsTokenA,
            credentials = Credentials(
                token = serverToken,
                url = clientVersionsEndpointUrl,
                roles = clientCredentialsRoleRepository.getCredentialsRoles()
            ),
            debugHeaders = emptyMap()
        ).let {
            it.data ?: throw OcpiResponseException(it.status_code, it.status_message ?: "unknown")
        }

        // Save credentials roles of partner
        clientPlatformRepository.saveCredentialsRoles(
            platformUrl = credentials.url,
            credentialsRoles = credentials.roles
        )

        // Store token C, which is the client token in this case because we are the sender. It's this one that
        // we will use to communicate with the receiver
        clientPlatformRepository.saveCredentialsClientToken(
            platformUrl = serverVersionsEndpointUrl,
            credentialsClientToken = credentials.token
        )

        // Remove token A because it is useless from now on
        clientPlatformRepository.invalidateCredentialsTokenA(platformUrl = serverVersionsEndpointUrl)

        return credentials
    }

    suspend fun update(): Credentials {
        // Token to communicate with receiver
        val credentialsClientToken =
            clientPlatformRepository.getCredentialsClientToken(platformUrl = serverVersionsEndpointUrl)
                ?: throw OcpiClientInvalidParametersException(
                    "Could not find credentialsClientToken associated with platform $serverVersionsEndpointUrl"
                )

        findLatestMutualVersionAndSaveInformation()

        // Generate token B, which is the server token, because it's the one that will be used by the partner (receiver)
        // to communicate with us
        val credentialsServerToken = clientPlatformRepository.saveCredentialsServerToken(
            platformUrl = serverVersionsEndpointUrl,
            credentialsServerToken = generateUUIDv4Token()
        )

        // Initiate registration process
        val credentials = buildCredentialClient().put(
            token = credentialsClientToken,
            credentials = Credentials(
                token = credentialsServerToken,
                url = clientVersionsEndpointUrl,
                roles = clientCredentialsRoleRepository.getCredentialsRoles()
            ),
            debugHeaders = emptyMap()
        ).let {
            it.data ?: throw OcpiResponseException(it.status_code, it.status_message ?: "unknown")
        }

        // Save credentials roles of partner
        clientPlatformRepository.saveCredentialsRoles(
            platformUrl = credentials.url,
            credentialsRoles = credentials.roles
        )

        // Store token C, which is the client token in this case because we are the sender. It's this one that
        // we will use to communicate with the receiver
        clientPlatformRepository.saveCredentialsClientToken(
            platformUrl = serverVersionsEndpointUrl,
            credentialsClientToken = credentials.token
        )

        return credentials
    }

    suspend fun delete() = clientPlatformRepository
        .getCredentialsClientToken(platformUrl = serverVersionsEndpointUrl)
        ?.let { clientToken ->
            buildCredentialClient()
                .delete(token = clientToken)
                .also {
                    clientPlatformRepository.unregisterPlatform(platformUrl = serverVersionsEndpointUrl)
                }
                .also {
                    if (it.status_code != OcpiStatus.SUCCESS.code) {
                        throw OcpiResponseException(it.status_code, it.status_message ?: "unknown")
                    }
                }
        }
        ?: throw OcpiClientGenericException(
            "Could not find client token associated with platform $serverVersionsEndpointUrl"
        )

    private suspend fun findLatestMutualVersionAndSaveInformation(): List<Endpoint> {
        val availableServerVersions = VersionsClient(
            transportClientBuilder = transportClientBuilder,
            serverVersionsEndpointUrl = serverVersionsEndpointUrl,
            platformRepository = clientPlatformRepository
        )
            .getVersions()
            .let {
                it.data ?: throw OcpiResponseException(it.status_code, it.status_message ?: "unknown")
            }
        val availableClientVersions = clientVersionsRepository.getVersions()

        // Get available versions and pick latest mutual
        val latestMutualVersion = availableServerVersions
            .sortedByDescending { clientVersion -> parseVersionNumber(clientVersion.version)!!.index }
            .firstOrNull { serverVersion ->
                availableClientVersions
                    .any { clientVersion -> serverVersion.version == clientVersion.version }
            }
            ?: throw OcpiServerUnsupportedVersionException(
                "Could not find mutual version with platform $serverVersionsEndpointUrl"
            )

        // Store version that will be used
        clientPlatformRepository.saveVersion(platformUrl = serverVersionsEndpointUrl, version = latestMutualVersion)

        // Get available endpoints for a given version
        val versionDetails = VersionDetailsClient(
            transportClientBuilder = transportClientBuilder,
            serverVersionsEndpointUrl = serverVersionsEndpointUrl,
            platformRepository = clientPlatformRepository
        )
            .getVersionDetails()
            .let {
                it.data ?: throw OcpiResponseException(it.status_code, it.status_message ?: "unknown")
            }

        // Store version & endpoint
        return clientPlatformRepository.saveEndpoints(
            platformUrl = serverVersionsEndpointUrl,
            endpoints = versionDetails.endpoints
        )
    }

    private suspend fun getOrFindEndpoints(): List<Endpoint> = clientPlatformRepository
        .getEndpoints(platformUrl = serverVersionsEndpointUrl)
        .takeIf { it.isNotEmpty() }
        ?: findLatestMutualVersionAndSaveInformation()

    private suspend fun buildCredentialClient(): CredentialsClient = CredentialsClient(
        transportClient = transportClientBuilder
            .build(
                baseUrl = getOrFindEndpoints()
                    .find { it.identifier == ModuleID.credentials }
                    ?.url
                    ?: throw OcpiServerUnsupportedVersionException(
                        "No credentials endpoint for $serverVersionsEndpointUrl"
                    )
            )
    )
}
