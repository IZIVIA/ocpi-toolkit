package samples.credentials

import ocpi.credentials.CredentialsClient
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
        validationService = VersionsValidationService(
            repository = senderVersionsRepository,
            platformRepository = senderPlatformRepository
        )
    )
    senderServer.start()

    // Client
    val transportTowardsReceiver = Http4kTransportClient(baseUrl = receiverUrl)

    val credentialsClientService = CredentialsClientService(
        clientPlatformRepository = senderPlatformRepository,
        versionsRepository = senderVersionsRepository,
        credentialsClient = CredentialsClient(transportClient = transportTowardsReceiver),
        versionsClient = VersionsClient(transportClient = transportTowardsReceiver)
    )

    // Register!
    val credentials = credentialsClientService.register(
        clientVersionsEndpointUrl = senderUrl,
        platformUrl = receiverUrl
    )

    println(credentials)
}