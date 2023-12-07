package com.izivia.ocpi.toolkit.samples.credentials

import com.izivia.ocpi.toolkit.common.checkToken
import com.izivia.ocpi.toolkit.modules.credentials.domain.BusinessDetails
import com.izivia.ocpi.toolkit.modules.credentials.domain.CredentialRole
import com.izivia.ocpi.toolkit.modules.credentials.domain.Role
import com.izivia.ocpi.toolkit.modules.credentials.repositories.CredentialsRoleRepository
import com.izivia.ocpi.toolkit.modules.credentials.services.CredentialsClientService
import com.izivia.ocpi.toolkit.modules.versions.VersionDetailsServer
import com.izivia.ocpi.toolkit.modules.versions.VersionsServer
import com.izivia.ocpi.toolkit.modules.versions.services.VersionDetailsService
import com.izivia.ocpi.toolkit.modules.versions.services.VersionsService
import com.izivia.ocpi.toolkit.samples.common.*
import kotlinx.coroutines.runBlocking

const val senderPort = 8081
const val senderUrl = "http://localhost:$senderPort"
const val senderVersionsUrl = "http://localhost:$senderPort/versions"

fun main() {
    // Add token A associated with the sender
    val senderVersionsRepository = VersionsCacheRepository(baseUrl = senderUrl)
    val senderVersionDetailsRepository = VersionDetailsCacheRepository(baseUrl = senderUrl)
    val senderPlatformRepository = PlatformCacheRepository()
    senderPlatformRepository.platforms.add(Platform(url = receiverVersionsUrl, tokenA = tokenA))

    // Server
    val senderServer = Http4kTransportServer(
        baseUrl = senderUrl,
        port = senderPort,
        secureFilter = senderPlatformRepository::checkToken
    )

    runBlocking {
        VersionsServer(
            service = VersionsService(
                repository = senderVersionsRepository
            )
        ).registerOn(senderServer)
        VersionDetailsServer(
            service = VersionDetailsService(
                repository = senderVersionDetailsRepository
            )
        ).registerOn(senderServer)
    }
    senderServer.start()

    // Client
    val credentialsClientService = CredentialsClientService(
        clientVersionsEndpointUrl = senderVersionsUrl,
        clientPlatformRepository = senderPlatformRepository,
        clientVersionsRepository = senderVersionsRepository,
        clientCredentialsRoleRepository = object : CredentialsRoleRepository {
            override suspend fun getCredentialsRoles(): List<CredentialRole> = listOf(
                CredentialRole(
                    role = Role.CPO,
                    businessDetails = BusinessDetails(name = "Sender", website = null, logo = null),
                    partyId = "ABC",
                    countryCode = "FR"
                )
            )
        },
        serverVersionsEndpointUrl = receiverVersionsUrl,
        transportClientBuilder = Http4kTransportClientBuilder()
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
