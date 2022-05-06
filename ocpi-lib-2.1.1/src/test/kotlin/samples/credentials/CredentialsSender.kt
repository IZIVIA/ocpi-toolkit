package samples.credentials

import ocpi.credentials.CredentialsClient
import ocpi.credentials.services.CredentialsClientService
import ocpi.locations.domain.BusinessDetails
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
        validationService = VersionsValidationService(
            repository = senderVersionsRepository,
            platformRepository = senderPlatformRepository
        )
    )
    senderServer.start()

    // Client
    val transportTowardsReceiver = Http4kTransportClient(baseUrl = receiverUrl)

    val credentialsClientService = CredentialsClientService(
        clientVersionsEndpointUrl = senderUrl,
        clientPlatformRepository = senderPlatformRepository,
        clientVersionsRepository = senderVersionsRepository,
        clientBusinessDetails = BusinessDetails(name = "Sender", website = null, logo = null),
        clientPartyId = "ABC",
        clientCountryCode = "FR",
        serverUrl = receiverUrl,
        credentialsClient = CredentialsClient(transportClient = transportTowardsReceiver),
        versionsClient = VersionsClient(transportClient = transportTowardsReceiver)
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