package com.izivia.ocpi.toolkit.modules.tokens

import com.izivia.ocpi.toolkit.common.httpResponse
import com.izivia.ocpi.toolkit.common.mapper
import com.izivia.ocpi.toolkit.common.checkToken
import com.izivia.ocpi.toolkit.modules.credentials.repositories.PlatformRepository
import com.izivia.ocpi.toolkit.modules.tokens.domain.Token
import com.izivia.ocpi.toolkit.modules.tokens.domain.TokenPartial
import com.izivia.ocpi.toolkit.transport.TransportServer
import com.izivia.ocpi.toolkit.transport.domain.FixedPathSegment
import com.izivia.ocpi.toolkit.transport.domain.HttpMethod
import com.izivia.ocpi.toolkit.transport.domain.VariablePathSegment
import kotlinx.coroutines.runBlocking

class TokensCpoServer(
    private val transportServer: TransportServer,
    private val platformRepository: PlatformRepository,
    private val service: TokensCpoInterface,
    basePath: List<FixedPathSegment> = listOf(
        FixedPathSegment("/2.1.1/tokens")
    )
) {
    init {
        runBlocking {
            transportServer.handle(
                method = HttpMethod.GET,
                path = basePath + listOf(
                    VariablePathSegment("countryCode"),
                    VariablePathSegment("partyId"),
                    VariablePathSegment("tokenUid")
                ),
                filters = listOf(platformRepository::checkToken)
            ) { req ->
                req.httpResponse {
                    service
                        .getToken(
                            countryCode = req.pathParams["countryCode"]!!,
                            partyId = req.pathParams["partyId"]!!,
                            tokenUid = req.pathParams["tokenUid"]!!
                        )
                }
            }

            transportServer.handle(
                method = HttpMethod.PUT,
                path = basePath + listOf(
                    VariablePathSegment("countryCode"),
                    VariablePathSegment("partyId"),
                    VariablePathSegment("tokenUid")
                ),
                filters = listOf(platformRepository::checkToken)
            ) { req ->
                req.httpResponse {
                    service
                        .putToken(
                            countryCode = req.pathParams["countryCode"]!!,
                            partyId = req.pathParams["partyId"]!!,
                            tokenUid = req.pathParams["tokenUid"]!!,
                            token = mapper.readValue(req.body, Token::class.java)
                        )
                }
            }

            transportServer.handle(
                method = HttpMethod.PATCH,
                path = basePath + listOf(
                    VariablePathSegment("countryCode"),
                    VariablePathSegment("partyId"),
                    VariablePathSegment("tokenUid")
                ),
                filters = listOf(platformRepository::checkToken)
            ) { req ->
                req.httpResponse {
                    service
                        .patchToken(
                            countryCode = req.pathParams["countryCode"]!!,
                            partyId = req.pathParams["partyId"]!!,
                            tokenUid = req.pathParams["tokenUid"]!!,
                            token = mapper.readValue(req.body!!, TokenPartial::class.java)
                        )
                }
            }
        }
    }
}
