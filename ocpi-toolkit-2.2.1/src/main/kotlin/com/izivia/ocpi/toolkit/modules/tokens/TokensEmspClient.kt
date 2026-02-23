package com.izivia.ocpi.toolkit.modules.tokens

import com.izivia.ocpi.toolkit.common.*
import com.izivia.ocpi.toolkit.modules.credentials.repositories.PartnerRepository
import com.izivia.ocpi.toolkit.modules.tokens.domain.Token
import com.izivia.ocpi.toolkit.modules.tokens.domain.TokenPartial
import com.izivia.ocpi.toolkit.modules.tokens.domain.TokenType
import com.izivia.ocpi.toolkit.modules.versions.domain.InterfaceRole
import com.izivia.ocpi.toolkit.modules.versions.domain.ModuleID
import com.izivia.ocpi.toolkit.serialization.mapper
import com.izivia.ocpi.toolkit.serialization.serializeObject
import com.izivia.ocpi.toolkit.transport.TransportClient
import com.izivia.ocpi.toolkit.transport.domain.HttpMethod
import com.izivia.ocpi.toolkit.transport.domain.HttpRequest

/**
 * Sends calls to the CPO
 * @property transportClientBuilder used to build transport client
 * @property partnerId used to know which partner to communicate with
 * @property partnerRepository used to get information about the partner (endpoint, token)
 */
class TokensEmspClient(
    private val transportClientBuilder: TransportClientBuilder,
    private val partnerId: String,
    private val partnerRepository: PartnerRepository,
) : TokensCpoInterface {

    private suspend fun buildTransport(): TransportClient = transportClientBuilder
        .buildFor(
            partnerId = partnerId,
            module = ModuleID.tokens,
            role = InterfaceRole.RECEIVER,
        )

    override suspend fun getToken(
        countryCode: CiString,
        partyId: CiString,
        tokenUid: CiString,
        type: TokenType?,
    ): Token? =
        with(buildTransport()) {
            send(
                HttpRequest(
                    method = HttpMethod.GET,
                    path = "/$countryCode/$partyId/$tokenUid",
                    queryParams = listOfNotNull(type?.let { "type" to type.toString() }).toMap(),
                )
                    .withRequiredHeaders(
                        requestId = generateRequestId(),
                        correlationId = generateCorrelationId(),
                    )
                    .authenticate(partnerRepository = partnerRepository, partnerId = partnerId),
            ).parseOptionalResult()
        }

    override suspend fun putToken(
        countryCode: CiString,
        partyId: CiString,
        tokenUid: CiString,
        type: TokenType?,
        token: Token,
    ): TokenPartial =
        with(buildTransport()) {
            send(
                HttpRequest(
                    method = HttpMethod.PUT,
                    path = "/$countryCode/$partyId/$tokenUid",
                    body = mapper.serializeObject(token),
                    queryParams = listOfNotNull(
                        type?.let { "type" to type.toString() },
                    ).toMap(),
                )
                    .withRequiredHeaders(
                        requestId = generateRequestId(),
                        correlationId = generateCorrelationId(),
                    )
                    .authenticate(partnerRepository = partnerRepository, partnerId = partnerId),
            ).parseResultOrNull() ?: TokenPartial()
        }

    override suspend fun patchToken(
        countryCode: CiString,
        partyId: CiString,
        tokenUid: CiString,
        type: TokenType?,
        token: TokenPartial,
    ): TokenPartial? =
        with(buildTransport()) {
            send(
                HttpRequest(
                    method = HttpMethod.PATCH,
                    path = "/$countryCode/$partyId/$tokenUid",
                    body = mapper.serializeObject(token),
                    queryParams = listOfNotNull(
                        type?.let { "type" to type.toString() },
                    ).toMap(),
                )
                    .withRequiredHeaders(
                        requestId = generateRequestId(),
                        correlationId = generateCorrelationId(),
                    )
                    .authenticate(partnerRepository = partnerRepository, partnerId = partnerId),
            ).parseResultOrNull()
        }
}
