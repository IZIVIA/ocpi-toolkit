package com.izivia.ocpi.toolkit.modules.tokens

import com.izivia.ocpi.toolkit.common.*
import com.izivia.ocpi.toolkit.modules.tokens.domain.Token
import com.izivia.ocpi.toolkit.modules.tokens.domain.TokenPartial
import com.izivia.ocpi.toolkit.modules.tokens.domain.TokenType
import com.izivia.ocpi.toolkit.modules.versions.domain.InterfaceRole
import com.izivia.ocpi.toolkit.modules.versions.domain.ModuleID
import com.izivia.ocpi.toolkit.modules.versions.domain.VersionNumber
import com.izivia.ocpi.toolkit.modules.versions.repositories.MutableVersionsRepository
import com.izivia.ocpi.toolkit.serialization.deserializeObject
import com.izivia.ocpi.toolkit.serialization.mapper
import com.izivia.ocpi.toolkit.transport.TransportServer
import com.izivia.ocpi.toolkit.transport.domain.HttpMethod
import com.izivia.ocpi.toolkit.transport.domain.VariablePathSegment
import java.time.Instant

class TokensCpoServer(
    private val service: TokensCpoInterface,
    private val timeProvider: TimeProvider = TimeProvider { Instant.now() },
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
            req.respondObject(timeProvider.now()) {
                service
                    .getToken(
                        countryCode = req.pathParam("countryCode"),
                        partyId = req.pathParam("partyId"),
                        tokenUid = req.pathParam("tokenUid"),
                        type = req.optionalQueryParamAs("type", TokenType::valueOf) ?: TokenType.RFID,
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
            req.respondObject(timeProvider.now()) {
                service
                    .putToken(
                        countryCode = req.pathParam("countryCode"),
                        partyId = req.pathParam("partyId"),
                        tokenUid = req.pathParam("tokenUid"),
                        type = req.optionalQueryParamAs("type", TokenType::valueOf) ?: TokenType.RFID,
                        token = mapper.deserializeObject<Token>(req.body),
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
            req.respondObject(timeProvider.now()) {
                service
                    .patchToken(
                        countryCode = req.pathParam("countryCode"),
                        partyId = req.pathParam("partyId"),
                        tokenUid = req.pathParam("tokenUid"),
                        type = req.optionalQueryParamAs("type", TokenType::valueOf) ?: TokenType.RFID,
                        token = mapper.deserializeObject<TokenPartial>(req.body),
                    )
            }
        }
    }
}
