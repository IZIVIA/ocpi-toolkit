package samples.credentials

import ocpi.credentials.CredentialsClient
import ocpi.credentials.domain.BusinessDetails
import common.CiString
import ocpi.credentials.domain.CredentialRole
import ocpi.credentials.domain.Role
import ocpi.credentials.repositories.CredentialsRoleRepository
import ocpi.credentials.services.CredentialsClientService
import ocpi.versions.VersionsClient
import ocpi.versions.VersionsServer
import ocpi.versions.validation.VersionsValidationService
import samples.common.*

const val senderPort = 8081
const val senderUrl = "http://localhost:$senderPort"

fun main() {
    // Server
    val senderServer = Http4kTransportServer(baseUrl = senderUrl, port = senderPort)

    // Add token A associated with the sender
    val senderVersionsRepository = VersionsCacheRepository(baseUrl = senderUrl)
    val senderPlatformRepository = PlatformCacheRepository()
    senderPlatformRepository.platforms[receiverUrl] = Platform(url = receiverUrl, tokenA = tokenA)

    VersionsServer(
        transportServer = senderServer,
        platformRepository = senderPlatformRepository,
        validationService = VersionsValidationService(
            repository = senderVersionsRepository
        )
    )
    senderServer.start()

    // Client
    val transportTowardsReceiver = Http4kTransportClient(baseUrl = receiverUrl)

    val credentialsClientService = CredentialsClientService(
        clientVersionsEndpointUrl = senderUrl,
        clientPlatformRepository = senderPlatformRepository,
        clientVersionsRepository = senderVersionsRepository,
        clientCredentialsRoleRepository = object: CredentialsRoleRepository {
            override fun getCredentialsRoles(): List<CredentialRole> = listOf(
                CredentialRole(
                    role = Role.CPO,
                    business_details = BusinessDetails(name = "Sender", website = null, logo = null),
                    party_id = "ABC",
                    country_code = "FR"
                )
            )
        },
        serverUrl = receiverUrl,
        credentialsClient = CredentialsClient(transportClient = transportTowardsReceiver),
        versionsClient = VersionsClient(transportClient = transportTowardsReceiver, platformRepository = senderPlatformRepository)
    )

    println("Registering $senderUrl to $receiverUrl")
    var credentials = credentialsClientService.register()
    println("Success. Credentials after register : $credentials")

    println("Retrieving credentials from $receiverUrl...")
    credentials = credentialsClientService.get()
    println("Success. Credentials : $credentials")

    println("Looking for updates, and updating if needed $receiverUrl...")
    credentials = credentialsClientService.update()
    println("Success. Credentials : $credentials")

    println("Deleting credentials of $senderUrl on $receiverUrl...")
    credentialsClientService.delete()
    println("Success.")
}