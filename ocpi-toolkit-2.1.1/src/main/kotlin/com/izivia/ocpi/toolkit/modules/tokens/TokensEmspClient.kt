package com.izivia.ocpi.toolkit.modules.tokens

import com.izivia.ocpi.toolkit.common.*
import com.izivia.ocpi.toolkit.modules.credentials.repositories.PlatformRepository
import com.izivia.ocpi.toolkit.modules.tokens.domain.Token
import com.izivia.ocpi.toolkit.modules.tokens.domain.TokenPartial
import com.izivia.ocpi.toolkit.modules.versions.domain.ModuleID
import com.izivia.ocpi.toolkit.transport.TransportClient
import com.izivia.ocpi.toolkit.transport.TransportClientBuilder
import com.izivia.ocpi.toolkit.transport.domain.HttpMethod
import com.izivia.ocpi.toolkit.transport.domain.HttpRequest

class TokensEmspClient(
    private val transportClientBuilder: TransportClientBuilder,
    private val serverVersionsEndpointUrl: String,
    private val platformRepository: PlatformRepository
) : TokensCpoInterface {

    private fun buildTransport(): TransportClient = transportClientBuilder
        .buildFor(
            module = ModuleID.tokens,
            platform = serverVersionsEndpointUrl,
            platformRepository = platformRepository
        )

    override fun getToken(
        countryCode: String,
        partyId: String,
        tokenUid: String
    ): OcpiResponseBody<Token?> =
        buildTransport()
            .send(
                HttpRequest(
                    method = HttpMethod.GET,
                    path = "/$countryCode/$partyId/$tokenUid"
                ).authenticate(platformRepository = platformRepository, baseUrl = serverVersionsEndpointUrl)
            )
            .parseBody()

    override fun putToken(
        countryCode: String,
        partyId: String,
        tokenUid: String,
        token: Token
    ): OcpiResponseBody<Token> =
        buildTransport()
            .send(
                HttpRequest(
                    method = HttpMethod.PUT,
                    path = "/$countryCode/$partyId/$tokenUid",
                    body = mapper.writeValueAsString(token)
                ).authenticate(platformRepository = platformRepository, baseUrl = serverVersionsEndpointUrl)
            )
            .parseBody()

    override fun patchToken(
        countryCode: String,
        partyId: String,
        tokenUid: String,
        token: TokenPartial
    ): OcpiResponseBody<Token> =
        buildTransport()
            .send(
                HttpRequest(
                    method = HttpMethod.PATCH,
                    path = "/$countryCode/$partyId/$tokenUid",
                    body = mapper.writeValueAsString(token)
                ).authenticate(platformRepository = platformRepository, baseUrl = serverVersionsEndpointUrl)
            )
            .parseBody()
}
