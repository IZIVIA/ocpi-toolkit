package com.izivia.ocpi.toolkit.modules.tokens

import com.izivia.ocpi.toolkit.common.OcpiSelfRegisteringModuleServer
import com.izivia.ocpi.toolkit.common.httpResponse
import com.izivia.ocpi.toolkit.common.mapper
import com.izivia.ocpi.toolkit.modules.tokens.domain.Token
import com.izivia.ocpi.toolkit.modules.tokens.domain.TokenPartial
import com.izivia.ocpi.toolkit.modules.tokens.domain.TokenType
import com.izivia.ocpi.toolkit.modules.versions.domain.InterfaceRole
import com.izivia.ocpi.toolkit.modules.versions.domain.ModuleID
import com.izivia.ocpi.toolkit.modules.versions.domain.VersionNumber
import com.izivia.ocpi.toolkit.modules.versions.repositories.MutableVersionsRepository
import com.izivia.ocpi.toolkit.transport.TransportServer
import com.izivia.ocpi.toolkit.transport.domain.HttpMethod
import com.izivia.ocpi.toolkit.transport.domain.VariablePathSegment

class TokensCpoServer(
    private val service: TokensCpoInterface,
    versionsRepository: MutableVersionsRepository? = null,
    basePathOverride: String? = null,
) : OcpiSelfRegisteringModuleServer(
    ocpiVersion = VersionNumber.V2_2_1,
    moduleID = ModuleID.tokens,
    interfaceRole = InterfaceRole.RECEIVER,
    versionsRepository = versionsRepository,
    basePathOverride = basePathOverride,
) {

    override suspend fun doRegisterOn(transportServer: TransportServer) {
        // Get Method
        transportServer.handle(
            method = HttpMethod.GET,
            path = basePathSegments + listOf(
                VariablePathSegment("countryCode"),
                VariablePathSegment("partyId"),
                VariablePathSegment("tokenUid"),
            ),
            queryParams = listOf("type"),
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

        // Put Method
        transportServer.handle(
            method = HttpMethod.PUT,
            path = basePathSegments + listOf(
                VariablePathSegment("countryCode"),
                VariablePathSegment("partyId"),
                VariablePathSegment("tokenUid"),
            ),
            queryParams = listOf("type"),
        ) { req ->
            req.httpResponse {
                service
                    .putToken(
                        countryCode = req.pathParams["countryCode"]!!,
                        partyId = req.pathParams["partyId"]!!,
                        tokenUid = req.pathParams["tokenUid"]!!,
                        type = req.queryParams["type"]?.run(TokenType::valueOf) ?: TokenType.RFID,
                        token = mapper.readValue(req.body, Token::class.java),
                    )
            }
        }
        // Patch Method
        transportServer.handle(
            method = HttpMethod.PATCH,
            path = basePathSegments + listOf(
                VariablePathSegment("countryCode"),
                VariablePathSegment("partyId"),
                VariablePathSegment("tokenUid"),
            ),
            queryParams = listOf("type"),
        ) { req ->
            req.httpResponse {
                service
                    .patchToken(
                        countryCode = req.pathParams["countryCode"]!!,
                        partyId = req.pathParams["partyId"]!!,
                        tokenUid = req.pathParams["tokenUid"]!!,
                        type = req.queryParams["type"]?.run(TokenType::valueOf) ?: TokenType.RFID,
                        token = mapper.readValue(req.body, TokenPartial::class.java),
                    )
            }
        }
    }
}
