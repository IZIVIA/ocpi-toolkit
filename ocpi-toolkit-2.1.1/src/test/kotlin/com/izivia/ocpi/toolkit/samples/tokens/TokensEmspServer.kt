package com.izivia.ocpi.toolkit.samples.tokens

import com.izivia.ocpi.toolkit.common.SearchResult
import com.izivia.ocpi.toolkit.common.toSearchResult
import com.izivia.ocpi.toolkit.modules.credentials.CredentialsServer
import com.izivia.ocpi.toolkit.modules.credentials.services.CredentialsServerService
import com.izivia.ocpi.toolkit.modules.locations.domain.BusinessDetails
import com.izivia.ocpi.toolkit.modules.tokens.TokensEmspServer
import com.izivia.ocpi.toolkit.modules.tokens.domain.AuthorizationInfo
import com.izivia.ocpi.toolkit.modules.tokens.domain.LocationReferences
import com.izivia.ocpi.toolkit.modules.tokens.domain.Token
import com.izivia.ocpi.toolkit.modules.tokens.domain.TokenType
import com.izivia.ocpi.toolkit.modules.tokens.services.TokensEmspService
import com.izivia.ocpi.toolkit.modules.tokens.validation.TokensEmspValidationService
import com.izivia.ocpi.toolkit.modules.versions.VersionDetailsServer
import com.izivia.ocpi.toolkit.modules.versions.VersionsServer
import com.izivia.ocpi.toolkit.modules.versions.validation.VersionDetailsValidationService
import com.izivia.ocpi.toolkit.modules.versions.validation.VersionsValidationService
import com.izivia.ocpi.toolkit.samples.common.*
import java.time.Instant
import java.util.*
import kotlin.math.min

const val emspServerUrl = "http://localhost:8089"
const val emspServerPort = 8089

/**
 * Example on how to server an eMSP server
 */
fun main() {
    // We specify the transport to serve the eMSP server
    val transportServer = Http4kTransportServer(baseUrl = emspServerUrl, port = emspServerPort)

    // We specify service for the validation service
    val service = CacheLocationsEmspService()

    val platformCacheRepository = PlatformCacheRepository()

    // Use for testing purposes
    // When registering for the first time, set the token A
    // repo.platforms["http://localhost:8085"] = Platform(url = "http://localhost:8085", tokenA = "a1c26d92-4d14-4706-b931-b3c362e3508d")

    // Once registerer set the token C with the one actually computed (since it's stored in the cache, after restarting the server, everything is lost)
    platformCacheRepository.platforms["http://localhost:8085"] = Platform(url = "http://localhost:8085", tokenC = "cbe36f7d-59f7-4b86-b037-904d0d6f8590")

    // We implement callbacks for the server using the built-in service and our repository implementation
    VersionsServer(
        transportServer = transportServer,
        platformRepository = platformCacheRepository,
        validationService = VersionsValidationService(repository = VersionsCacheRepository(emspServerUrl))
    )
    VersionDetailsServer(
        transportServer = transportServer,
        platformRepository = platformCacheRepository,
        validationService = VersionDetailsValidationService(repository = VersionDetailsCacheRepository(emspServerUrl))
    )
    CredentialsServer(
        transportServer = transportServer,
        service = CredentialsServerService(
            platformRepository = platformCacheRepository,
            serverBusinessDetails = BusinessDetails(name = "emsp local", null , null),
            serverPartyId = "loc",
            serverCountryCode = "fr",
            transportClientBuilder = Http4kTransportClientBuilder(),
            serverVersionsUrl = "$emspServerUrl/versions"
        )
    )

    TokensEmspServer(
        transportServer = transportServer,
        platformRepository = platformCacheRepository,
        service = TokensEmspValidationService(service = service)
    )

    // It is recommended to start the server after setting up the routes to handle
    transportServer.start()
}

class CacheLocationsEmspService : TokensEmspService {
    override fun getTokens(dateFrom: Instant?, dateTo: Instant?, offset: Int, limit: Int?): SearchResult<Token> =
        listOf(
            validToken.copy(uid = UUID.randomUUID().toString()),
            validToken.copy(uid = UUID.randomUUID().toString(), type = TokenType.OTHER),
            validToken.copy(uid = UUID.randomUUID().toString(), type = TokenType.OTHER)
        )
            .let {
                it.subList(offset, min(offset + (limit ?: 10), it.size))
            }
            .toSearchResult(totalCount = 10, limit = limit ?: 10, offset = offset)

    override fun postToken(
        tokenUid: String,
        tokenType: TokenType,
        locationReferences: LocationReferences?
    ): AuthorizationInfo {
        TODO("Not yet implemented")
    }
}
