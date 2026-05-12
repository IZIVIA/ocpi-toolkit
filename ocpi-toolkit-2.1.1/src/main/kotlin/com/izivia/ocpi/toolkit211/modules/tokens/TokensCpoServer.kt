package com.izivia.ocpi.toolkit211.modules.tokens

import com.izivia.ocpi.toolkit.transport.TransportServer
import com.izivia.ocpi.toolkit.transport.domain.HttpMethod
import com.izivia.ocpi.toolkit.transport.domain.VariablePathSegment
import com.izivia.ocpi.toolkit211.common.*
import com.izivia.ocpi.toolkit211.modules.tokens.domain.Token
import com.izivia.ocpi.toolkit211.modules.tokens.domain.TokenPartial
import com.izivia.ocpi.toolkit211.modules.tokens.domain.TokenType
import com.izivia.ocpi.toolkit211.modules.versions.domain.ModuleID
import com.izivia.ocpi.toolkit211.modules.versions.domain.VersionNumber
import com.izivia.ocpi.toolkit211.modules.versions.repositories.MutableVersionsRepository
import com.izivia.ocpi.toolkit211.serialization.deserializeObject
import com.izivia.ocpi.toolkit211.serialization.mapper
import java.time.Instant

class TokensCpoServer(
    private val service: TokensCpoInterface,
    private val timeProvider: TimeProvider = TimeProvider { Instant.now() },
    versionsRepository: MutableVersionsRepository? = null,
    basePathOverride: String? = null,
) : OcpiSelfRegisteringModuleServer(
    ocpiVersion = VersionNumber.V2_1_1,
    moduleID = ModuleID.tokens,
    versionsRepository = versionsRepository,
    basePathOverride = basePathOverride,
) {

    override suspend fun doRegisterOn(transportServer: TransportServer) {
        transportServer.handle(
            method = HttpMethod.GET,
            path = basePathSegments + listOf(
                VariablePathSegment("countryCode"),
                VariablePathSegment("partyId"),
                VariablePathSegment("tokenUid"),
            ),
            queryParams = listOf("type"),
        ) { req ->
            req.respondObject(timeProvider.now()) {
                service.getToken(
                    countryCode = req.pathParam("countryCode"),
                    partyId = req.pathParam("partyId"),
                    tokenUid = req.pathParam("tokenUid"),
                    type = req.optionalQueryParamAs("type", TokenType::valueOf) ?: TokenType.RFID,
                )
            }
        }

        transportServer.handle(
            method = HttpMethod.PUT,
            path = basePathSegments + listOf(
                VariablePathSegment("countryCode"),
                VariablePathSegment("partyId"),
                VariablePathSegment("tokenUid"),
            ),
            queryParams = listOf("type"),
        ) { req ->
            req.respondObject(timeProvider.now()) {
                service.putToken(
                    countryCode = req.pathParam("countryCode"),
                    partyId = req.pathParam("partyId"),
                    tokenUid = req.pathParam("tokenUid"),
                    type = req.optionalQueryParamAs("type", TokenType::valueOf) ?: TokenType.RFID,
                    token = mapper.deserializeObject<Token>(req.body!!),
                )
            }
        }

        transportServer.handle(
            method = HttpMethod.PATCH,
            path = basePathSegments + listOf(
                VariablePathSegment("countryCode"),
                VariablePathSegment("partyId"),
                VariablePathSegment("tokenUid"),
            ),
            queryParams = listOf("type"),
        ) { req ->
            req.respondObject(timeProvider.now()) {
                service.patchToken(
                    countryCode = req.pathParam("countryCode"),
                    partyId = req.pathParam("partyId"),
                    tokenUid = req.pathParam("tokenUid"),
                    type = req.optionalQueryParamAs("type", TokenType::valueOf) ?: TokenType.RFID,
                    token = mapper.deserializeObject<TokenPartial>(req.body!!),
                )
            }
        }
    }
}
