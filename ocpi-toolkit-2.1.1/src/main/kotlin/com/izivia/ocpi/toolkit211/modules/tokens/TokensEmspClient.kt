package com.izivia.ocpi.toolkit211.modules.tokens

import com.izivia.ocpi.toolkit.transport.TransportClient
import com.izivia.ocpi.toolkit.transport.domain.HttpMethod
import com.izivia.ocpi.toolkit.transport.domain.HttpRequest
import com.izivia.ocpi.toolkit211.common.CiString
import com.izivia.ocpi.toolkit211.common.TransportClientBuilder
import com.izivia.ocpi.toolkit211.common.parseOptionalResult
import com.izivia.ocpi.toolkit211.common.parseResultOrNull
import com.izivia.ocpi.toolkit211.modules.tokens.domain.Token
import com.izivia.ocpi.toolkit211.modules.tokens.domain.TokenPartial
import com.izivia.ocpi.toolkit211.modules.tokens.domain.TokenType
import com.izivia.ocpi.toolkit211.modules.versions.domain.ModuleID
import com.izivia.ocpi.toolkit211.serialization.mapper
import com.izivia.ocpi.toolkit211.serialization.serializeObject

/**
 * Sends calls to the CPO
 */
class TokensEmspClient(
    private val transportClientBuilder: TransportClientBuilder,
    private val partnerId: String,
) : TokensCpoInterface {

    private suspend fun buildTransport(): TransportClient = transportClientBuilder
        .buildFor(
            partnerId = partnerId,
            module = ModuleID.tokens,
        )

    override suspend fun getToken(
        countryCode: CiString,
        partyId: CiString,
        tokenUid: CiString,
        type: TokenType?,
    ): Token? = with(buildTransport()) {
        send(
            HttpRequest(
                method = HttpMethod.GET,
                path = "/$countryCode/$partyId/$tokenUid",
                queryParams = listOfNotNull(type?.let { "type" to type.toString() }).toMap(),
            ),
        ).parseOptionalResult()
    }

    override suspend fun putToken(
        countryCode: CiString,
        partyId: CiString,
        tokenUid: CiString,
        type: TokenType?,
        token: Token,
    ): TokenPartial = with(buildTransport()) {
        send(
            HttpRequest(
                method = HttpMethod.PUT,
                path = "/$countryCode/$partyId/$tokenUid",
                body = mapper.serializeObject(token),
                queryParams = listOfNotNull(type?.let { "type" to type.toString() }).toMap(),
            ),
        ).parseResultOrNull() ?: TokenPartial()
    }

    override suspend fun patchToken(
        countryCode: CiString,
        partyId: CiString,
        tokenUid: CiString,
        type: TokenType?,
        token: TokenPartial,
    ): TokenPartial? = with(buildTransport()) {
        send(
            HttpRequest(
                method = HttpMethod.PATCH,
                path = "/$countryCode/$partyId/$tokenUid",
                body = mapper.serializeObject(token),
                queryParams = listOfNotNull(type?.let { "type" to type.toString() }).toMap(),
            ),
        ).parseResultOrNull()
    }
}
