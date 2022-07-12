package com.izivia.ocpi.toolkit.modules.tokens

import com.izivia.ocpi.toolkit.common.httpResponse
import com.izivia.ocpi.toolkit.common.mapper
import com.izivia.ocpi.toolkit.common.tokenFilter
import com.izivia.ocpi.toolkit.modules.credentials.repositories.PlatformRepository
import com.izivia.ocpi.toolkit.modules.tokens.domain.LocationReferences
import com.izivia.ocpi.toolkit.modules.tokens.domain.TokenType
import com.izivia.ocpi.toolkit.transport.TransportServer
import com.izivia.ocpi.toolkit.transport.domain.FixedPathSegment
import com.izivia.ocpi.toolkit.transport.domain.HttpMethod
import com.izivia.ocpi.toolkit.transport.domain.VariablePathSegment
import java.time.Instant

class TokensEmspServer(
    private val transportServer: TransportServer,
    private val platformRepository: PlatformRepository,
    private val service: TokensEmspInterface,
    basePath: List<FixedPathSegment> = listOf(
        FixedPathSegment("/2.1.1/tokens")
    )
) {
    init {
        transportServer.handle(
            method = HttpMethod.GET,
            path = basePath,
            queryParams = listOf("date_from", "date_to", "offset", "limit"),
            filters = listOf(platformRepository::tokenFilter)
        ) { req ->
            req.httpResponse {
                val dateFrom = req.queryParams["date_from"]
                val dateTo = req.queryParams["date_to"]

                service
                    .getTokens(
                        dateFrom = dateFrom?.let { Instant.parse(it) },
                        dateTo = dateTo?.let { Instant.parse(it) },
                        offset = req.queryParams["offset"]?.toInt() ?: 0,
                        limit = req.queryParams["limit"]?.toInt()
                    )
            }
        }

        transportServer.handle(
            method = HttpMethod.POST,
            path = basePath + listOf(
                VariablePathSegment("tokenUid"),
                FixedPathSegment("authorize")
            ),
            queryParams = listOf("type"),
            filters = listOf(platformRepository::tokenFilter)
        ) { req ->
            req.httpResponse {
                service
                    .postToken(
                        tokenUid = req.pathParams["tokenUid"]!!,
                        tokenType = req.queryParams["type"]?.run(TokenType::valueOf) ?: TokenType.RFID,
                        locationReferences = req.body?.run { mapper.readValue(this, LocationReferences::class.java) }
                    )
            }
        }
    }
}
