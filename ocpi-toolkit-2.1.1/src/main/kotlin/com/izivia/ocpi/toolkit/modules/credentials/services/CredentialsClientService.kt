package com.izivia.ocpi.toolkit.modules.credentials.services

import com.izivia.ocpi.toolkit.common.*
import com.izivia.ocpi.toolkit.modules.credentials.CredentialsClient
import com.izivia.ocpi.toolkit.modules.credentials.domain.Credentials
import com.izivia.ocpi.toolkit.modules.credentials.repositories.PlatformRepository
import com.izivia.ocpi.toolkit.modules.locations.domain.BusinessDetails
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
 * @property clientBusinessDetails Details of this party.
 * @property clientPartyId (max-length=3) CPO or eMSP ID of this party. (following the 15118 ISO standard).
 * @property clientCountryCode(max-length=2) Country code of the country this party is operating in.
 * @property serverVersionsEndpointUrl the versions endpoint url of the server (for the client to retrieve endpoints)
 * @property transportClientBuilder used to build a transport (will be used to create CredentialClient to make calls)
 */
class CredentialsClientService(
    private val clientVersionsEndpointUrl: String,
    private val clientPlatformRepository: PlatformRepository,
    private val clientVersionsRepository: VersionsRepository,
    private val clientBusinessDetails: BusinessDetails,
    private val clientPartyId: String,
    private val clientCountryCode: String,
    private val serverVersionsEndpointUrl: String,
    private val transportClientBuilder: TransportClientBuilder
) {
    fun get(): Credentials = clientPlatformRepository
        .getCredentialsTokenC(platformUrl = serverVersionsEndpointUrl)
        ?.let { tokenC ->
            buildCredentialClient()
                .get(tokenC = tokenC)
                .let { it.data ?: throw OcpiResponseException(it.status_code, it.status_message ?: "unknown") }
        }
        ?: throw OcpiClientGenericException("Could not find CREDENTIALS_TOKEN_C associated with platform $serverVersionsEndpointUrl")

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
        val credentialsTokenA = clientPlatformRepository.getCredentialsTokenA(platformUrl = serverVersionsEndpointUrl)
            ?: throw OcpiClientInvalidParametersException("Could not find token A associated with platform $serverVersionsEndpointUrl")

        findLatestMutualVersionAndSaveInformation()

        // Generate token B
        val credentialsTokenB = clientPlatformRepository.saveCredentialsTokenB(
            platformUrl = serverVersionsEndpointUrl,
            credentialsTokenB = generateUUIDv4Token()
        )

        // Initiate registration process
        val credentials = buildCredentialClient().post(
            tokenA = credentialsTokenA,
            credentials = Credentials(
                token = credentialsTokenB,
                url = clientVersionsEndpointUrl,
                business_details = clientBusinessDetails,
                party_id = clientPartyId,
                country_code = clientCountryCode
            )
        ).let {
            it.data ?: throw OcpiResponseException(it.status_code, it.status_message ?: "unknown")
        }

        // Store token C
        clientPlatformRepository.saveCredentialsTokenC(
            platformUrl = serverVersionsEndpointUrl,
            credentialsTokenC = credentials.token
        )

        // Remove token A because it is useless from now on
        clientPlatformRepository.removeCredentialsTokenA(platformUrl = serverVersionsEndpointUrl)

        return credentials
    }

    fun update(): Credentials {
        // Token to communicate with receiver
        val credentialsTokenC = clientPlatformRepository.getCredentialsTokenC(platformUrl = serverVersionsEndpointUrl)
            ?: throw OcpiClientInvalidParametersException("Could not find token C associated with platform $serverVersionsEndpointUrl")

        findLatestMutualVersionAndSaveInformation()

        // Generate token B
        val credentialsTokenB = clientPlatformRepository.saveCredentialsTokenB(
            platformUrl = serverVersionsEndpointUrl,
            credentialsTokenB = generateUUIDv4Token()
        )

        // Initiate registration process
        val credentials = buildCredentialClient().put(
            tokenC = credentialsTokenC,
            credentials = Credentials(
                token = credentialsTokenB,
                url = clientVersionsEndpointUrl,
                business_details = clientBusinessDetails,
                party_id = clientPartyId,
                country_code = clientCountryCode
            )
        ).let {
            it.data ?: throw OcpiResponseException(it.status_code, it.status_message ?: "unknown")
        }

        // Store token C
        clientPlatformRepository.saveCredentialsTokenC(
            platformUrl = serverVersionsEndpointUrl,
            credentialsTokenC = credentials.token
        )

        return credentials
    }

    fun delete() = clientPlatformRepository
        .getCredentialsTokenC(platformUrl = serverVersionsEndpointUrl)
        ?.let { tokenC ->
            buildCredentialClient()
                .delete(tokenC = tokenC)
                .also {
                    if (it.status_code != OcpiStatus.SUCCESS.code)
                        throw OcpiResponseException(it.status_code, it.status_message ?: "unknown")
                }
        }
        ?: throw OcpiClientGenericException("Could not find CREDENTIALS_TOKEN_C associated with platform $serverVersionsEndpointUrl")

    private fun findLatestMutualVersionAndSaveInformation(): List<Endpoint> {
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
            ?: throw OcpiServerUnsupportedVersionException("Could not find mutual version with platform $serverVersionsEndpointUrl")

        // Store version that will be used
        clientPlatformRepository.saveVersion(platformUrl = serverVersionsEndpointUrl, version = latestMutualVersion)

        // Get available endpoints for the used version
        val versionDetails = VersionDetailsClient(
            transportClientBuilder = transportClientBuilder,
            serverVersionsEndpointUrl = serverVersionsEndpointUrl,
            platformRepository = clientPlatformRepository
        )
            .getVersionDetails()
            .let {
                it.data ?: throw OcpiResponseException(it.status_code, it.status_message ?: "unknown")
            }

        return clientPlatformRepository.saveEndpoints(platformUrl = serverVersionsEndpointUrl, endpoints = versionDetails.endpoints)
    }

    private fun getOrFindEndpoints(): List<Endpoint> = clientPlatformRepository
        .getEndpoints(platformUrl = serverVersionsEndpointUrl)
        .takeIf { it.isNotEmpty() }
        ?: findLatestMutualVersionAndSaveInformation()

    private fun buildCredentialClient(): CredentialsClient = CredentialsClient(
        transportClient = transportClientBuilder
            .build(
                url = getOrFindEndpoints()
                    .find { it.identifier == ModuleID.credentials }
                    ?.url
                    ?: throw OcpiServerUnsupportedVersionException("No credentials endpoint for $serverVersionsEndpointUrl")
            )
    )
}