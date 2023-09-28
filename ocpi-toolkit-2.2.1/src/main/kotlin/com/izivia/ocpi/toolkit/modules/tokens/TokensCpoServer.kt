package com.izivia.ocpi.toolkit.modules.tokens

import com.izivia.ocpi.toolkit.common.OcpiModuleServer
import com.izivia.ocpi.toolkit.common.httpResponse
import com.izivia.ocpi.toolkit.common.mapper
import com.izivia.ocpi.toolkit.modules.tokens.domain.Token
import com.izivia.ocpi.toolkit.modules.tokens.domain.TokenPartial
import com.izivia.ocpi.toolkit.modules.tokens.domain.TokenType
import com.izivia.ocpi.toolkit.transport.TransportServer
import com.izivia.ocpi.toolkit.transport.domain.HttpMethod
import com.izivia.ocpi.toolkit.transport.domain.VariablePathSegment
import java.time.Instant

/**
 * Receives calls from a CPO
 * @property transportServer
 */
class TokensCpoServer(
    private val service: TokensCpoInterface,
    basePath: String = "/2.2.1/tokens"
) : OcpiModuleServer(basePath) {
    override suspend fun registerOn(transportServer: TransportServer) {

        //Get Method
        transportServer.handle(
            method = HttpMethod.GET,
            path = basePathSegments + listOf(
                VariablePathSegment("countryCode"),
                VariablePathSegment("partyId"),
                VariablePathSegment("tokenUid")
            ),
            queryParams = listOf("type")
        ) { req ->
            req.httpResponse {
                service
                    .getToken(
                        countryCode = req.pathParams["countryCode"]!!,
                        partyId = req.pathParams["partyId"]!!,
                        tokenUid = req.pathParams["tokenUid"]!!,
                        type = req.queryParams["type"]?.run(TokenType::valueOf) ?: TokenType.RFID,
                    )
            }
        }

        //Put Method
        transportServer.handle(
            method = HttpMethod.PUT,
            path = basePathSegments + listOf(
                VariablePathSegment("countryCode"),
                VariablePathSegment("partyId"),
                VariablePathSegment("tokenUid")
            ),
            queryParams = listOf("type")
        ) { req ->
            req.httpResponse {
                service
                    .putToken(
                        countryCode = req.pathParams["countryCode"]!!,
                        partyId = req.pathParams["partyId"]!!,
                        tokenUid = req.pathParams["tokenUid"]!!,
                        type = req.queryParams["type"]?.run(TokenType::valueOf) ?: TokenType.RFID,
                        token = mapper.readValue(req.body, Token::class.java)
                    )
            }
        }
        //Patch Method
        transportServer.handle(
            method = HttpMethod.PATCH,
            path = basePathSegments + listOf(
                VariablePathSegment("countryCode"),
                VariablePathSegment("partyId"),
                VariablePathSegment("tokenUid")
            ),
            queryParams = listOf("type")
        ) { req ->
            req.httpResponse {
                service
                    .patchToken(
                        countryCode = req.pathParams["countryCode"]!!,
                        partyId = req.pathParams["partyId"]!!,
                        tokenUid = req.pathParams["tokenUid"]!!,
                        type = req.queryParams["type"]?.run(TokenType::valueOf) ?: TokenType.RFID,
                        token = mapper.readValue(req.body, TokenPartial::class.java)
                    )
            }
        }
    }
}
