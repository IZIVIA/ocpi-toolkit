package com.izivia.ocpi.toolkit.samples.credentials

import com.izivia.ocpi.toolkit.common.tokenFilter
import com.izivia.ocpi.toolkit.modules.credentials.domain.BusinessDetails
import com.izivia.ocpi.toolkit.modules.credentials.domain.CredentialRole
import com.izivia.ocpi.toolkit.modules.credentials.domain.Role
import com.izivia.ocpi.toolkit.modules.credentials.repositories.CredentialsRoleRepository
import com.izivia.ocpi.toolkit.modules.credentials.services.CredentialsClientService
import com.izivia.ocpi.toolkit.modules.versions.VersionDetailsServer
import com.izivia.ocpi.toolkit.modules.versions.VersionsServer
import com.izivia.ocpi.toolkit.modules.versions.validation.VersionDetailsValidationService
import com.izivia.ocpi.toolkit.modules.versions.validation.VersionsValidationService
import com.izivia.ocpi.toolkit.samples.common.*

const val senderPort = 8081
const val senderUrl = "http://localhost:$senderPort"
const val senderVersionsUrl = "http://localhost:$senderPort/versions"

fun main() {
    // Add token A associated with the sender
    val senderVersionsRepository = VersionsCacheRepository(baseUrl = senderUrl)
    val senderVersionDetailsRepository = VersionDetailsCacheRepository(baseUrl = senderUrl)
    val senderPlatformRepository = PlatformCacheRepository()
    senderPlatformRepository.platforms[senderVersionsUrl] = Platform(url = senderVersionsUrl, tokenA = tokenA)

    // Server
    val senderServer = Http4kTransportServer(
        baseUrl = senderUrl,
        port = senderPort,
        secureFilter = senderPlatformRepository::tokenFilter
    )

    VersionsServer(
        service = VersionsValidationService(
            repository = senderVersionsRepository
        )
    ).registerOn(senderServer)
    VersionDetailsServer(
        service = VersionDetailsValidationService(
            repository = senderVersionDetailsRepository
        )
    ).registerOn(senderServer)
    senderServer.start()

    // Client
    val transportTowardsReceiver = Http4kTransportClient(baseUrl = receiverUrl)

    val credentialsClientService = CredentialsClientService(
        clientVersionsEndpointUrl = senderVersionsUrl,
        clientPlatformRepository = senderPlatformRepository,
        clientVersionsRepository = senderVersionsRepository,
        clientCredentialsRoleRepository = object : CredentialsRoleRepository {
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
