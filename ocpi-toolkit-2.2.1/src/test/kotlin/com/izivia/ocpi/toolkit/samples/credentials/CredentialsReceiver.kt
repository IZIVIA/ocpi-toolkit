package com.izivia.ocpi.toolkit.samples.credentials

import com.izivia.ocpi.toolkit.common.checkToken
import com.izivia.ocpi.toolkit.modules.credentials.CredentialsServer
import com.izivia.ocpi.toolkit.modules.credentials.domain.CredentialRole
import com.izivia.ocpi.toolkit.modules.credentials.domain.Role
import com.izivia.ocpi.toolkit.modules.credentials.repositories.CredentialsRoleRepository
import com.izivia.ocpi.toolkit.modules.credentials.services.CredentialsServerService
import com.izivia.ocpi.toolkit.modules.credentials.services.RequiredEndpoints
import com.izivia.ocpi.toolkit.modules.locations.domain.BusinessDetails
import com.izivia.ocpi.toolkit.modules.versions.VersionsServer
import com.izivia.ocpi.toolkit.modules.versions.domain.ModuleID
import com.izivia.ocpi.toolkit.modules.versions.repositories.InMemoryVersionsRepository
import com.izivia.ocpi.toolkit.modules.versions.services.VersionsService
import com.izivia.ocpi.toolkit.samples.common.Http4kTransportClientBuilder
import com.izivia.ocpi.toolkit.samples.common.Http4kTransportServer
import com.izivia.ocpi.toolkit.samples.common.Partner
import com.izivia.ocpi.toolkit.samples.common.PartnerCacheRepository
import kotlinx.coroutines.runBlocking

const val receiverPort = 8080
const val receiverUrl = "http://localhost:$receiverPort"
const val receiverVersionsUrl = "http://localhost:$receiverPort/versions"
const val tokenA = "06f7967e-65c3-4def-a966-701ffb362b3c"

fun main() {
    // Add token A associated with the sender
    val receiverVersionsRepository = InMemoryVersionsRepository()
    val receiverPlatformRepository = PartnerCacheRepository()
    receiverPlatformRepository.partners.add(Partner(tokenA = tokenA))

    val receiverServer = Http4kTransportServer(
        baseUrl = receiverUrl,
        port = receiverPort,
        secureFilter = receiverPlatformRepository::checkToken,
    )

    val requiredOtherPartEndpoints = RequiredEndpoints(receiver = listOf(ModuleID.credentials))

    runBlocking {
        CredentialsServer(
            service = CredentialsServerService(
                partnerRepository = receiverPlatformRepository,
                credentialsRoleRepository = object : CredentialsRoleRepository {
                    override suspend fun getCredentialsRoles(partnerId: String): List<CredentialRole> = listOf(
                        CredentialRole(
                            role = Role.EMSP,
                            businessDetails = BusinessDetails(name = "Receiver", website = null, logo = null),
                            partyId = "DEF",
                            countryCode = "FR",
                        ),
                    )
                },
                transportClientBuilder = Http4kTransportClientBuilder(),
                serverVersionsUrlProvider = { receiverVersionsUrl },
                requiredEndpoints = requiredOtherPartEndpoints,
            ),
            versionsRepository = receiverVersionsRepository,
        ).registerOn(receiverServer)
        VersionsServer(
            service = VersionsService(
                repository = receiverVersionsRepository,
                baseUrlProvider = { receiverUrl },
            ),
        ).registerOn(receiverServer)
    }
    receiverServer.start()
}
