package com.izivia.ocpi.toolkit.modules.tokens

import com.izivia.ocpi.toolkit.common.*
import com.izivia.ocpi.toolkit.modules.credentials.repositories.PlatformRepository
import com.izivia.ocpi.toolkit.modules.tokens.domain.AuthorizationInfo
import com.izivia.ocpi.toolkit.modules.tokens.domain.LocationReferences
import com.izivia.ocpi.toolkit.modules.tokens.domain.Token
import com.izivia.ocpi.toolkit.modules.tokens.domain.TokenType
import com.izivia.ocpi.toolkit.modules.versions.domain.ModuleID
import com.izivia.ocpi.toolkit.transport.TransportClient
import com.izivia.ocpi.toolkit.transport.TransportClientBuilder
import com.izivia.ocpi.toolkit.transport.domain.HttpMethod
import com.izivia.ocpi.toolkit.transport.domain.HttpRequest
import java.time.Instant

/**
 * Sends calls to an eMSP server
 * @property transportClientBuilder used to build transport client
 * @property serverVersionsEndpointUrl used to know which platform to communicate with
 * @property platformRepository used to get information about the platform (endpoint, token)
 */
class TokensCpoClient(
    private val transportClientBuilder: TransportClientBuilder,
    private val serverVersionsEndpointUrl: String,
    private val platformRepository: PlatformRepository
) : TokensEmspInterface {
    private suspend fun buildTransport(): TransportClient = transportClientBuilder
        .buildFor(
            module = ModuleID.tokens,
            platform = serverVersionsEndpointUrl,
            platformRepository = platformRepository
        )

    override suspend fun getTokens(
        dateFrom: Instant?,
        dateTo: Instant?,
        offset: Int,
        limit: Int?
    ): OcpiResponseBody<SearchResult<Token>> =
        with(buildTransport()) {
            send(
                HttpRequest(
                    method = HttpMethod.GET,
                    queryParams = listOfNotNull(
                        dateFrom?.let { "date_from" to dateFrom.toString() },
                        dateTo?.let { "date_to" to dateTo.toString() },
                        "offset" to offset.toString(),
                        limit?.let { "limit" to limit.toString() }
                    ).toMap()
                )
                    .withRequiredHeaders(
                        requestId = generateRequestId(),
                        correlationId = generateCorrelationId()
                    )
                    .authenticate(platformRepository = platformRepository, baseUrl = serverVersionsEndpointUrl)
            )
                .parsePaginatedBody(offset)
        }

    override suspend fun postToken(
        tokenUid: CiString,
        type: TokenType?,
        locationReferences: LocationReferences?
    ): OcpiResponseBody<AuthorizationInfo> =
        with(buildTransport()) {
            send(
                HttpRequest(
                    method = HttpMethod.POST,
                    path = "/$tokenUid/authorize",
                    queryParams = listOf("type" to type.toString()).toMap(),
                    body = locationReferences.run(mapper::writeValueAsString)
                )
                    .withRequiredHeaders(
                        requestId = generateRequestId(),
                        correlationId = generateCorrelationId()
                    )
                    .authenticate(platformRepository = platformRepository, baseUrl = serverVersionsEndpointUrl)
            ).parseBody()
        }
}
