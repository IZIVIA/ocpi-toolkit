package com.izivia.ocpi.toolkit211.modules.tokens

import com.izivia.ocpi.toolkit.transport.TransportClient
import com.izivia.ocpi.toolkit.transport.domain.HttpMethod
import com.izivia.ocpi.toolkit.transport.domain.HttpRequest
import com.izivia.ocpi.toolkit211.common.*
import com.izivia.ocpi.toolkit211.modules.tokens.domain.*
import com.izivia.ocpi.toolkit211.modules.versions.domain.ModuleID
import com.izivia.ocpi.toolkit211.serialization.mapper
import com.izivia.ocpi.toolkit211.serialization.serializeObject
import java.time.Instant

/**
 * Sends calls to an eMSP server
 */
class TokensCpoClient(
    private val transportClientBuilder: TransportClientBuilder,
    private val partnerId: String,
    private val ignoreInvalidListEntry: Boolean = false,
) : TokensEmspInterface {

    private suspend fun buildTransport(): TransportClient = transportClientBuilder
        .buildFor(
            partnerId = partnerId,
            module = ModuleID.tokens,
        )

    override suspend fun getTokens(
        dateFrom: Instant?,
        dateTo: Instant?,
        offset: Int,
        limit: Int?,
    ): SearchResult<Token> = with(buildTransport()) {
        send(
            HttpRequest(
                method = HttpMethod.GET,
                queryParams = listOfNotNull(
                    dateFrom?.let { "date_from" to dateFrom.toString() },
                    dateTo?.let { "date_to" to dateTo.toString() },
                    "offset" to offset.toString(),
                    limit?.let { "limit" to limit.toString() },
                ).toMap(),
            ),
        ).let { res ->
            if (ignoreInvalidListEntry) {
                res.parseSearchResultIgnoringInvalid<Token, TokenPartial>(offset)
            } else {
                res.parseSearchResult<Token>(offset)
            }
        }
    }

    suspend fun getTokensNextPage(
        previousResponse: SearchResult<Token>,
    ): SearchResult<Token>? = getNextPage<Token, TokenPartial>(
        transportClientBuilder = transportClientBuilder,
        partnerId = partnerId,
        previousResponse = previousResponse,
        ignoreInvalidListEntry = ignoreInvalidListEntry,
    )

    override suspend fun postToken(
        tokenUid: CiString,
        type: TokenType?,
        locationReferences: LocationReferences?,
    ): AuthorizationInfo = with(buildTransport()) {
        send(
            HttpRequest(
                method = HttpMethod.POST,
                path = "/$tokenUid/authorize",
                queryParams = listOfNotNull(type?.let { "type" to type.toString() }).toMap(),
                body = locationReferences.run(mapper::serializeObject),
            ),
        ).parseResult()
    }
}
