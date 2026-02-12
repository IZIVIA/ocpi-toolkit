package com.izivia.ocpi.toolkit.modules.tokens

import com.izivia.ocpi.toolkit.common.*
import com.izivia.ocpi.toolkit.modules.credentials.repositories.PartnerRepository
import com.izivia.ocpi.toolkit.modules.tokens.domain.*
import com.izivia.ocpi.toolkit.modules.versions.domain.InterfaceRole
import com.izivia.ocpi.toolkit.modules.versions.domain.ModuleID
import com.izivia.ocpi.toolkit.serialization.mapper
import com.izivia.ocpi.toolkit.serialization.serializeObject
import com.izivia.ocpi.toolkit.transport.TransportClient
import com.izivia.ocpi.toolkit.transport.domain.HttpMethod
import com.izivia.ocpi.toolkit.transport.domain.HttpRequest
import java.time.Instant

/**
 * Sends calls to an eMSP server
 * @property transportClientBuilder used to build transport client
 * @property partnerId used to know which partner to communicate with
 * @property partnerRepository used to get information about the partner (endpoint, token)
 */
class TokensCpoClient(
    private val transportClientBuilder: TransportClientBuilder,
    private val partnerId: String,
    private val partnerRepository: PartnerRepository,
    private val ignoreInvalidListEntry: Boolean = false,
) : TokensEmspInterface {
    private suspend fun buildTransport(): TransportClient = transportClientBuilder
        .buildFor(
            partnerId = partnerId,
            module = ModuleID.tokens,
            role = InterfaceRole.SENDER,
        )

    override suspend fun getTokens(
        dateFrom: Instant?,
        dateTo: Instant?,
        offset: Int,
        limit: Int?,
    ): SearchResult<Token> =
        with(buildTransport()) {
            send(
                HttpRequest(
                    method = HttpMethod.GET,
                    queryParams = listOfNotNull(
                        dateFrom?.let { "date_from" to dateFrom.toString() },
                        dateTo?.let { "date_to" to dateTo.toString() },
                        "offset" to offset.toString(),
                        limit?.let { "limit" to limit.toString() },
                    ).toMap(),
                )
                    .withRequiredHeaders(
                        requestId = generateRequestId(),
                        correlationId = generateCorrelationId(),
                    )
                    .authenticate(partnerRepository = partnerRepository, partnerId = partnerId),
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
        partnerRepository = partnerRepository,
        previousResponse = previousResponse,
        ignoreInvalidListEntry = ignoreInvalidListEntry,
    )

    override suspend fun postToken(
        tokenUid: CiString,
        type: TokenType?,
        locationReferences: LocationReferences?,
    ): AuthorizationInfo =
        with(buildTransport()) {
            send(
                HttpRequest(
                    method = HttpMethod.POST,
                    path = "/$tokenUid/authorize",
                    queryParams = listOfNotNull(type?.let { "type" to type.toString() }).toMap(),
                    body = locationReferences.run(mapper::serializeObject),
                )
                    .withRequiredHeaders(
                        requestId = generateRequestId(),
                        correlationId = generateCorrelationId(),
                    )
                    .authenticate(partnerRepository = partnerRepository, partnerId = partnerId),
            ).parseResult()
        }
}
