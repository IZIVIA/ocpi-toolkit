package com.izivia.ocpi.toolkit.modules.credentials.services

import com.izivia.ocpi.toolkit.common.*
import com.izivia.ocpi.toolkit.modules.credentials.CredentialsClient
import com.izivia.ocpi.toolkit.modules.credentials.domain.Credentials
import com.izivia.ocpi.toolkit.modules.credentials.repositories.CredentialsRoleRepository
import com.izivia.ocpi.toolkit.modules.credentials.repositories.PartnerRepository
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
 * Note: credentialsClient & versionsClient must target the same partner since the automated process will check the
 * versions of the receiver before performing registration.
 *
 * - Client: the one doing the registration process
 * - Server: the one receiving the registration process
 *
 * @property clientVersionsEndpointUrl the versions endpoints url of the client (for the server to use)
 * @property clientPartnerRepository client's repository to store and retrieve tokens and information about partners
 * @property clientVersionsRepository client's repository to retrieve available versions on the client
 * @property clientCredentialsRoleRepository client's repository to retrieve its role
 * @property serverVersionsEndpointUrl the versions endpoint url of the server (for the client to retrieve endpoints)
 * @property transportClientBuilder used to build a transport (will be used to create CredentialClient to make calls)
 * @property requiredEndpoints the endpoints this client expects from the other part to provide
 */
open class CredentialsClientService(
    private val clientVersionsEndpointUrl: String,
    private val clientPartnerRepository: PartnerRepository,
    private val clientVersionsRepository: VersionsRepository,
    private val clientCredentialsRoleRepository: CredentialsRoleRepository,
    private val serverVersionsEndpointUrl: String,
    private val transportClientBuilder: TransportClientBuilder,
    private val requiredEndpoints: RequiredEndpoints?
) {
    suspend fun get(): Credentials = clientPartnerRepository
        .getCredentialsClientToken(partnerUrl = serverVersionsEndpointUrl)
        ?.let { clientToken ->
            buildCredentialClient()
                .get(token = clientToken)
                .let { it.data ?: throw OcpiResponseException(it.status_code, it.status_message ?: "unknown") }
        }
        ?: throw OcpiClientGenericException(
            "Could not find CREDENTIALS_TOKEN_C associated with partner $serverVersionsEndpointUrl"
        )

    /**
     * To start using OCPI, the Platforms will need to exchange credentials tokens.
     *
     * To start the exchange of credentials tokens, one partner has to be selected as Sender for the Credentials
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
        val credentialsTokenA =
            clientPartnerRepository.getCredentialsClientToken(partnerUrl = serverVersionsEndpointUrl)
                ?: clientPartnerRepository.getCredentialsTokenA(partnerUrl = serverVersionsEndpointUrl)
                ?: throw OcpiClientInvalidParametersException(
                    "Could not find the TokenA or the ClientToken associated with partner $serverVersionsEndpointUrl"
                )

        findLatestMutualVersionAndSaveInformation()

        // Generate token B,  which is the server token in this case because we are the sender. It's this token
        // that the receiver will use to contact us.
        val serverToken = clientPartnerRepository.saveCredentialsServerToken(
            partnerUrl = serverVersionsEndpointUrl,
            credentialsServerToken = generateUUIDv4Token()
        )

        // Initiate registration process
        val credentials = buildCredentialClient().post(
            token = credentialsTokenA,
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
        clientPartnerRepository.saveCredentialsRoles(
            partnerUrl = credentials.url,
            credentialsRoles = credentials.roles
        )

        // Store token C, which is the client token in this case because we are the sender. It's this one that
        // we will use to communicate with the receiver
        clientPartnerRepository.saveCredentialsClientToken(
            partnerUrl = serverVersionsEndpointUrl,
            credentialsClientToken = credentials.token
        )

        // Remove token A because it is useless from now on
        clientPartnerRepository.invalidateCredentialsTokenA(partnerUrl = serverVersionsEndpointUrl)

        return credentials
    }

    suspend fun update(): Credentials {
        // Token to communicate with receiver
        val credentialsClientToken =
            clientPartnerRepository.getCredentialsClientToken(partnerUrl = serverVersionsEndpointUrl)
                ?: throw OcpiClientInvalidParametersException(
                    "Could not find the ClientToken associated with partner $serverVersionsEndpointUrl"
                )

        findLatestMutualVersionAndSaveInformation()

        // Generate token B, which is the server token, because it's the one that will be used by the partner (receiver)
        // to communicate with us
        val credentialsServerToken = clientPartnerRepository.saveCredentialsServerToken(
            partnerUrl = serverVersionsEndpointUrl,
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
        clientPartnerRepository.saveCredentialsRoles(
            partnerUrl = credentials.url,
            credentialsRoles = credentials.roles
        )

        // Store token C, which is the client token in this case because we are the sender. It's this one that
        // we will use to communicate with the receiver
        clientPartnerRepository.saveCredentialsClientToken(
            partnerUrl = serverVersionsEndpointUrl,
            credentialsClientToken = credentials.token
        )

        return credentials
    }

    suspend fun delete() = clientPartnerRepository
        .getCredentialsClientToken(partnerUrl = serverVersionsEndpointUrl)
        ?.let { clientToken ->
            buildCredentialClient()
                .delete(token = clientToken)
                .also {
                    // Only server token is invalidated. It means that the system can still send authenticated requests
                    // to the partner
                    clientPartnerRepository.invalidateCredentialsServerToken(partnerUrl = serverVersionsEndpointUrl)
                }
                .also {
                    if (it.status_code != OcpiStatus.SUCCESS.code) {
                        throw OcpiResponseException(it.status_code, it.status_message ?: "unknown")
                    }
                }
        }
        ?: throw OcpiClientGenericException(
            "Could not find client token associated with partner $serverVersionsEndpointUrl"
        )

    private suspend fun findLatestMutualVersionAndSaveInformation(): List<Endpoint> {
        val availableServerVersions = VersionsClient(
            transportClientBuilder = transportClientBuilder,
            serverVersionsEndpointUrl = serverVersionsEndpointUrl,
            partnerRepository = clientPartnerRepository
        )
            .getVersions()
            .let {
                it.data ?: throw OcpiResponseException(it.status_code, it.status_message ?: "unknown")
            }
        val availableClientVersionNumbers = clientVersionsRepository.getVersions()

        // Get available versions and pick latest mutual
        val latestMutualVersion = availableServerVersions
            .sortedByDescending { clientVersion -> parseVersionNumber(clientVersion.version)!!.index }
            .firstOrNull { serverVersion ->
                availableClientVersionNumbers
                    .any { clientVersionNumber -> serverVersion.version == clientVersionNumber.value }
            }
            ?: throw OcpiServerUnsupportedVersionException(
                "Could not find mutual version with partner $serverVersionsEndpointUrl"
            )

        // Store version that will be used
        clientPartnerRepository.saveVersion(partnerUrl = serverVersionsEndpointUrl, version = latestMutualVersion)

        // Get available endpoints for a given version
        val versionDetails = VersionDetailsClient(
            transportClientBuilder = transportClientBuilder,
            serverVersionsEndpointUrl = serverVersionsEndpointUrl,
            partnerRepository = clientPartnerRepository
        )
            .getVersionDetails()
            .let {
                it.data ?: throw OcpiResponseException(it.status_code, it.status_message ?: "unknown")
            }

        checkRequiredEndpoints(requiredEndpoints, versionDetails.endpoints)

        // Store version & endpoint
        return clientPartnerRepository.saveEndpoints(
            partnerUrl = serverVersionsEndpointUrl,
            endpoints = versionDetails.endpoints
        )
    }

    private suspend fun getOrFindEndpoints(): List<Endpoint> = clientPartnerRepository
        .getEndpoints(partnerUrl = serverVersionsEndpointUrl)
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
