package com.izivia.ocpi.toolkit.modules.tokens

import com.izivia.ocpi.toolkit.common.*
import com.izivia.ocpi.toolkit.modules.tokens.domain.LocationReferences
import com.izivia.ocpi.toolkit.modules.tokens.domain.TokenType
import com.izivia.ocpi.toolkit.modules.versions.domain.InterfaceRole
import com.izivia.ocpi.toolkit.modules.versions.domain.ModuleID
import com.izivia.ocpi.toolkit.modules.versions.domain.VersionNumber
import com.izivia.ocpi.toolkit.modules.versions.repositories.MutableVersionsRepository
import com.izivia.ocpi.toolkit.transport.TransportServer
import com.izivia.ocpi.toolkit.transport.domain.FixedPathSegment
import com.izivia.ocpi.toolkit.transport.domain.HttpMethod
import com.izivia.ocpi.toolkit.transport.domain.VariablePathSegment

class TokensEmspServer(
    private val service: TokensEmspInterface,
    versionsRepository: MutableVersionsRepository? = null,
    basePathOverride: String? = null,
) : OcpiSelfRegisteringModuleServer(
    ocpiVersion = VersionNumber.V2_2_1,
    moduleID = ModuleID.tokens,
    interfaceRole = InterfaceRole.SENDER,
    versionsRepository = versionsRepository,
    basePathOverride = basePathOverride,
) {

    override suspend fun doRegisterOn(transportServer: TransportServer) {
        // GET Token
        transportServer.handle(
            method = HttpMethod.GET,
            path = basePathSegments,
            queryParams = listOf("date_from", "date_to", "offset", "limit"),
        ) { req ->
            req.httpResponse {
                service
                    .getTokens(
                        dateFrom = req.optionalQueryParamAsInstant("date_from"),
                        dateTo = req.optionalQueryParamAsInstant("date_to"),
                        offset = req.optionalQueryParamAsInt("offset") ?: 0,
                        limit = req.optionalQueryParamAsInt("limit"),
                    )
            }
        }

        // POST token
        transportServer.handle(
            method = HttpMethod.POST,
            path = basePathSegments + listOf(
                VariablePathSegment("tokenUid"),
                FixedPathSegment("authorize"),
            ),
            queryParams = listOf("type"),
        ) { req ->
            req.httpResponse {
                service.postToken(
                    tokenUid = req.pathParam("tokenUid"),
                    type = req.optionalQueryParamAs("type", TokenType::valueOf) ?: TokenType.RFID,
                    locationReferences = req.body
                        ?.takeIf { it.isNotBlank() } // During Test if client sent body = null, this reiceve body=""
                        ?.let { mapper.readValue(it, LocationReferences::class.java) },
                )
            }
        }
    }
}
