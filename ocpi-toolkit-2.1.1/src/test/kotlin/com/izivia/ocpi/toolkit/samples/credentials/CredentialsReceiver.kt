package com.izivia.ocpi.toolkit.samples.credentials

import com.izivia.ocpi.toolkit.modules.credentials.CredentialsServer
import com.izivia.ocpi.toolkit.modules.credentials.services.CredentialsServerService
import com.izivia.ocpi.toolkit.modules.locations.domain.BusinessDetails
import com.izivia.ocpi.toolkit.modules.versions.VersionDetailsServer
import com.izivia.ocpi.toolkit.modules.versions.VersionsServer
import com.izivia.ocpi.toolkit.modules.versions.validation.VersionDetailsValidationService
import com.izivia.ocpi.toolkit.modules.versions.validation.VersionsValidationService
import com.izivia.ocpi.toolkit.samples.common.*
import java.util.*

const val receiverPort = 8080
const val receiverUrl = "http://localhost:$receiverPort"
const val receiverVersionsUrl = "http://localhost:$receiverPort/versions"
const val tokenA = "06f7967e-65c3-4def-a966-701ffb362b3c"

fun main() {
    val receiverServer = Http4kTransportServer(baseUrl = receiverUrl, port = receiverPort)

    // Add token A associated with the sender
    val receiverPlatformRepository = PlatformCacheRepository()
    val tempUrl = UUID.randomUUID().toString()
    receiverPlatformRepository.platforms[tempUrl] = Platform(url = tempUrl, tokenA = tokenA)

    CredentialsServer(
        transportServer = receiverServer,
        service = CredentialsServerService(
            platformRepository = receiverPlatformRepository,
            serverBusinessDetails = BusinessDetails(name = "Receiver", website = null, logo = null),
            serverPartyId = "DEF",
            serverCountryCode = "FR",
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
