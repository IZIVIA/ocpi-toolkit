package samples.credentials

import ocpi.credentials.CredentialsServer
import ocpi.credentials.domain.BusinessDetails
import ocpi.credentials.domain.CredentialRole
import ocpi.credentials.domain.Role
import ocpi.credentials.repositories.CredentialsRoleRepository
import ocpi.credentials.services.CredentialsServerService
import ocpi.versions.VersionDetailsServer
import ocpi.versions.VersionsServer
import ocpi.versions.validation.VersionDetailsValidationService
import ocpi.versions.validation.VersionsValidationService
import samples.common.*

const val receiverPort = 8080
const val receiverUrl = "http://localhost:$receiverPort"
const val receiverVersionsUrl = "http://localhost:$receiverPort/versions"
const val tokenA = "06f7967e-65c3-4def-a966-701ffb362b3c"

fun main() {
    val receiverServer = Http4kTransportServer(baseUrl = receiverUrl, port = receiverPort)

    // Add token A associated with the sender
    val receiverPlatformRepository = PlatformCacheRepository()
    receiverPlatformRepository.platforms[receiverVersionsUrl] = Platform(url = receiverVersionsUrl, tokenA = tokenA)

    CredentialsServer(
        transportServer = receiverServer,
        service = CredentialsServerService(
            platformRepository = receiverPlatformRepository,
            credentialsRoleRepository = object: CredentialsRoleRepository {
                override fun getCredentialsRoles(): List<CredentialRole> = listOf(
                    CredentialRole(
                        role = Role.EMSP,
                        business_details = BusinessDetails(name = "Receiver", website = null, logo = null),
                        party_id = "DEF",
                        country_code = "FR"
                    )
                )
            },
            transportClientBuilder = Http4kTransportClientBuilder(),
            serverVersionsUrl = receiverVersionsUrl
        )
    )
    VersionsServer(
        transportServer = receiverServer,
        platformRepository = receiverPlatformRepository,
        validationService = VersionsValidationService(
            repository = VersionsCacheRepository(baseUrl = receiverUrl)
        )
    )
    VersionDetailsServer(
        transportServer = receiverServer,
        platformRepository = receiverPlatformRepository,
        validationService = VersionDetailsValidationService(
            repository = VersionDetailsCacheRepository(baseUrl = receiverUrl)
        )
    )
    receiverServer.start()
}