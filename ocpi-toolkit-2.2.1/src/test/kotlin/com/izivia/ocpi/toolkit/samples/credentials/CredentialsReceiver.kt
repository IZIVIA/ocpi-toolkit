package com.izivia.ocpi.toolkit.samples.credentials

import com.izivia.ocpi.toolkit.common.checkToken
import com.izivia.ocpi.toolkit.modules.credentials.CredentialsServer
import com.izivia.ocpi.toolkit.modules.credentials.domain.BusinessDetails
import com.izivia.ocpi.toolkit.modules.credentials.domain.CredentialRole
import com.izivia.ocpi.toolkit.modules.credentials.domain.Role
import com.izivia.ocpi.toolkit.modules.credentials.repositories.CredentialsRoleRepository
import com.izivia.ocpi.toolkit.modules.credentials.services.CredentialsServerService
import com.izivia.ocpi.toolkit.modules.versions.VersionDetailsServer
import com.izivia.ocpi.toolkit.modules.versions.VersionsServer
import com.izivia.ocpi.toolkit.modules.versions.services.VersionDetailsService
import com.izivia.ocpi.toolkit.modules.versions.services.VersionsService
import com.izivia.ocpi.toolkit.samples.common.*
import kotlinx.coroutines.runBlocking

const val receiverPort = 8080
const val receiverUrl = "http://localhost:$receiverPort"
const val receiverVersionsUrl = "http://localhost:$receiverPort/versions"
const val tokenA = "06f7967e-65c3-4def-a966-701ffb362b3c"

fun main() {
    // Add token A associated with the sender
    val receiverPlatformRepository = PlatformCacheRepository()
    receiverPlatformRepository.platforms.add(Platform(tokenA = tokenA))

    val receiverServer = Http4kTransportServer(
        baseUrl = receiverUrl,
        port = receiverPort,
        secureFilter = receiverPlatformRepository::checkToken
    )

    runBlocking {
        CredentialsServer(
            service = CredentialsServerService(
                platformRepository = receiverPlatformRepository,
                credentialsRoleRepository = object : CredentialsRoleRepository {
                    override suspend fun getCredentialsRoles(): List<CredentialRole> = listOf(
                        CredentialRole(
                            role = Role.EMSP,
                            businessDetails = BusinessDetails(name = "Receiver", website = null, logo = null),
                            partyId = "DEF",
                            countryCode = "FR"
                        )
                    )
                },
                transportClientBuilder = Http4kTransportClientBuilder(),
                serverVersionsUrlProvider = { receiverVersionsUrl }
            )
        ).registerOn(receiverServer)
        VersionsServer(
            service = VersionsService(
                repository = VersionsCacheRepository(baseUrl = receiverUrl)
            )
        ).registerOn(receiverServer)

        VersionDetailsServer(
            service = VersionDetailsService(
                repository = VersionDetailsCacheRepository(baseUrl = receiverUrl)
            )
        ).registerOn(receiverServer)
    }
    receiverServer.start()
}
