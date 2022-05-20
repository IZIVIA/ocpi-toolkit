package samples.credentials

import ocpi.credentials.domain.BusinessDetails
import ocpi.credentials.domain.CredentialRole
import ocpi.credentials.domain.Role
import ocpi.credentials.repositories.CredentialsRoleRepository
import ocpi.credentials.services.CredentialsClientService
import ocpi.versions.VersionDetailsServer
import ocpi.versions.VersionsServer
import ocpi.versions.validation.VersionDetailsValidationService
import ocpi.versions.validation.VersionsValidationService
import samples.common.*

const val senderPort = 8081
const val senderUrl = "http://localhost:$senderPort"
const val senderVersionsUrl = "http://localhost:$senderPort/versions"

fun main() {
    // Server
    val senderServer = Http4kTransportServer(baseUrl = senderUrl, port = senderPort)

    // Add token A associated with the sender
    val senderVersionsRepository = VersionsCacheRepository(baseUrl = senderUrl)
    val senderVersionDetailsRepository = VersionDetailsCacheRepository(baseUrl = senderUrl)
    val senderPlatformRepository = PlatformCacheRepository()
    senderPlatformRepository.platforms[senderVersionsUrl] = Platform(url = senderVersionsUrl, tokenA = tokenA)

    VersionsServer(
        transportServer = senderServer,
        platformRepository = senderPlatformRepository,
        validationService = VersionsValidationService(
            repository = senderVersionsRepository
        )
    )
    VersionDetailsServer(
        transportServer = senderServer,
        platformRepository = senderPlatformRepository,
        validationService = VersionDetailsValidationService(
            repository = senderVersionDetailsRepository
        )
    )
    senderServer.start()

    // Client
    val transportTowardsReceiver = Http4kTransportClient(baseUrl = receiverUrl)

    val credentialsClientService = CredentialsClientService(
        clientVersionsEndpointUrl = senderVersionsUrl,
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
        serverVersionsEndpointUrl = receiverVersionsUrl,
        transportClientBuilder = Http4kTransportClientBuilder()
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