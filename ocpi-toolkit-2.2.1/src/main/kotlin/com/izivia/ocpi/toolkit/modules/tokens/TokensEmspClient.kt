package com.izivia.ocpi.toolkit.modules.tokens

import com.izivia.ocpi.toolkit.common.*
import com.izivia.ocpi.toolkit.modules.credentials.repositories.PartnerRepository
import com.izivia.ocpi.toolkit.modules.tokens.domain.Token
import com.izivia.ocpi.toolkit.modules.tokens.domain.TokenPartial
import com.izivia.ocpi.toolkit.modules.tokens.domain.TokenType
import com.izivia.ocpi.toolkit.modules.versions.domain.ModuleID
import com.izivia.ocpi.toolkit.transport.TransportClient
import com.izivia.ocpi.toolkit.transport.TransportClientBuilder
import com.izivia.ocpi.toolkit.transport.domain.HttpMethod
import com.izivia.ocpi.toolkit.transport.domain.HttpRequest

/**
 * Sends calls to the CPO
 * @property transportClientBuilder used to build transport client
 * @property serverVersionsEndpointUrl used to know which partner to communicate with
 * @property partnerRepository used to get information about the partner (endpoint, token)
 */
class TokensEmspClient(
    private val transportClientBuilder: TransportClientBuilder,
    private val serverVersionsEndpointUrl: String,
    private val partnerRepository: PartnerRepository
) : TokensCpoInterface {

    private suspend fun buildTransport(): TransportClient = transportClientBuilder
        .buildFor(
            module = ModuleID.tokens,
            partnerUrl = serverVersionsEndpointUrl,
            partnerRepository = partnerRepository
        )

    override suspend fun getToken(
        countryCode: CiString,
        partyId: CiString,
        tokenUid: CiString,
        type: TokenType?
    ): OcpiResponseBody<Token> =
        with(buildTransport()) {
            send(
                HttpRequest(
                    method = HttpMethod.GET,
                    path = "/$countryCode/$partyId/$tokenUid",
                    queryParams = listOfNotNull(type?.let { "type" to type.toString() }).toMap()
                )
                    .withRequiredHeaders(
                        requestId = generateRequestId(),
                        correlationId = generateCorrelationId()
                    )
                    .authenticate(partnerRepository = partnerRepository, partnerUrl = serverVersionsEndpointUrl)
            ).parseBody()
        }

    override suspend fun putToken(
        token: Token,
        countryCode: CiString,
        partyId: CiString,
        tokenUid: CiString,
        type: TokenType?
    ): OcpiResponseBody<Token> =
        with(buildTransport()) {
            send(
                HttpRequest(
                    method = HttpMethod.PUT,
                    body = mapper.writeValueAsString(token),
                    queryParams = listOfNotNull(
                        "country_code" to countryCode,
                        "party_id" to partyId,
                        "token_uid" to tokenUid,
                        type?.let { "type" to type.toString() }
                    ).toMap()
                )
                    .withRequiredHeaders(
                        requestId = generateRequestId(),
                        correlationId = generateCorrelationId()
                    )
                    .authenticate(partnerRepository = partnerRepository, partnerUrl = serverVersionsEndpointUrl)
            ).parseBody()
        }

    override suspend fun patchToken(
        token: TokenPartial,
        countryCode: CiString,
        partyId: CiString,
        tokenUid: CiString,
        type: TokenType?
    ): OcpiResponseBody<Token?> =
        with(buildTransport()) {
            send(
                HttpRequest(
                    method = HttpMethod.PATCH,
                    body = mapper.writeValueAsString(token),
                    queryParams = listOfNotNull(
                        "country_code" to countryCode,
                        "party_id" to partyId,
                        "token_uid" to tokenUid,
                        type?.let { "type" to type.toString() }
                    ).toMap()
                )
                    .withRequiredHeaders(
                        requestId = generateRequestId(),
                        correlationId = generateCorrelationId()
                    )
                    .authenticate(partnerRepository = partnerRepository, partnerUrl = serverVersionsEndpointUrl)
            ).parseBody()
        }
}
