package samples.credentials

import ocpi.credentials.CredentialsServer
import ocpi.credentials.services.CredentialsServerService
import ocpi.locations.domain.BusinessDetails
import ocpi.versions.VersionsServer
import ocpi.versions.validation.VersionsValidationService
import samples.common.*

const val receiverPort = 8080
const val receiverUrl = "http://localhost:$receiverPort"
const val tokenA = "06f7967e-65c3-4def-a966-701ffb362b3c"

fun main() {
    val receiverServer = Http4kTransportServer(baseUrl = receiverUrl, port = receiverPort)

    // Add token A associated with the sender
    val receiverPlatformRepository = PlatformCacheRepository()
    receiverPlatformRepository.platforms[senderUrl] = Platform(url = senderUrl, tokenA = tokenA)

    CredentialsServer(
        transportServer = receiverServer,
        service = CredentialsServerService(
            platformRepository = receiverPlatformRepository,
            serverBusinessDetails = BusinessDetails(name = "Receiver", website = null, logo = null),
            serverPartyId = "DEF",
            serverCountryCode = "FR",
            transportClientBuilder = Http4kTransportClientBuilder(),
            serverUrl = receiverUrl
        )
    )
    VersionsServer(
        transportServer = receiverServer,
        platformRepository = receiverPlatformRepository,
        validationService = VersionsValidationService(
            repository = VersionsCacheRepository(baseUrl = receiverServer.baseUrl)
        )
    )
    receiverServer.start()
}