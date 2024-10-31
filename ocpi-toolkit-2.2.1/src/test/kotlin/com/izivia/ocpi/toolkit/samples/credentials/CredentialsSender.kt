package com.izivia.ocpi.toolkit.samples.credentials

import com.izivia.ocpi.toolkit.common.checkToken
import com.izivia.ocpi.toolkit.modules.credentials.domain.CredentialRole
import com.izivia.ocpi.toolkit.modules.credentials.domain.Role
import com.izivia.ocpi.toolkit.modules.credentials.repositories.CredentialsRoleRepository
import com.izivia.ocpi.toolkit.modules.credentials.services.CredentialsClientService
import com.izivia.ocpi.toolkit.modules.credentials.services.RequiredEndpoints
import com.izivia.ocpi.toolkit.modules.locations.domain.BusinessDetails
import com.izivia.ocpi.toolkit.modules.versions.VersionsServer
import com.izivia.ocpi.toolkit.modules.versions.domain.Endpoint
import com.izivia.ocpi.toolkit.modules.versions.domain.InterfaceRole
import com.izivia.ocpi.toolkit.modules.versions.domain.ModuleID
import com.izivia.ocpi.toolkit.modules.versions.domain.VersionNumber
import com.izivia.ocpi.toolkit.modules.versions.repositories.InMemoryVersionsRepository
import com.izivia.ocpi.toolkit.modules.versions.services.VersionsService
import com.izivia.ocpi.toolkit.samples.common.Http4kTransportClientBuilder
import com.izivia.ocpi.toolkit.samples.common.Http4kTransportServer
import com.izivia.ocpi.toolkit.samples.common.Partner
import com.izivia.ocpi.toolkit.samples.common.PartnerCacheRepository
import kotlinx.coroutines.runBlocking

const val senderPort = 8081
const val senderUrl = "http://localhost:$senderPort"
const val senderVersionsUrl = "http://localhost:$senderPort/versions"

fun main() {
    // Add token A associated with the sender
    val senderVersionsRepository = InMemoryVersionsRepository()
    val senderPlatformRepository = PartnerCacheRepository()
    senderPlatformRepository.partners.add(Partner(url = receiverVersionsUrl, tokenA = tokenA))

    // Server
    val senderServer = Http4kTransportServer(
        baseUrl = senderUrl,
        port = senderPort,
        secureFilter = senderPlatformRepository::checkToken,
    )

    runBlocking {
        VersionsServer(
            service = VersionsService(
                repository = senderVersionsRepository,
                baseUrlProvider = { senderUrl },
            ),
        ).registerOn(senderServer)

        senderVersionsRepository.addEndpoint(
            VersionNumber.V2_2_1,
            Endpoint(ModuleID.credentials, InterfaceRole.SENDER, "$senderUrl/2.2.1/credentials"),
        )
    }
    senderServer.start()

    // Client
    val credentialsClientService = CredentialsClientService(
        clientVersionsEndpointUrl = senderVersionsUrl,
        clientPartnerRepository = senderPlatformRepository,
        clientVersionsRepository = senderVersionsRepository,
        clientCredentialsRoleRepository = object : CredentialsRoleRepository {
            override suspend fun getCredentialsRoles(partnerId: String): List<CredentialRole> = listOf(
                CredentialRole(
                    role = Role.CPO,
                    businessDetails = BusinessDetails(name = "Sender", website = null, logo = null),
                    partyId = "ABC",
                    countryCode = "FR",
                ),
            )
        },
        partnerId = receiverVersionsUrl,
        transportClientBuilder = Http4kTransportClientBuilder(),
        requiredEndpoints = RequiredEndpoints(receiver = listOf(ModuleID.credentials)),
    )

    runBlocking {
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
}
