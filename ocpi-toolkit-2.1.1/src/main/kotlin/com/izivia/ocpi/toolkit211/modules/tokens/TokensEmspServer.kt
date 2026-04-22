package com.izivia.ocpi.toolkit211.modules.tokens

import com.izivia.ocpi.toolkit.transport.TransportServer
import com.izivia.ocpi.toolkit.transport.domain.FixedPathSegment
import com.izivia.ocpi.toolkit.transport.domain.HttpMethod
import com.izivia.ocpi.toolkit.transport.domain.VariablePathSegment
import com.izivia.ocpi.toolkit211.common.*
import com.izivia.ocpi.toolkit211.modules.tokens.domain.LocationReferences
import com.izivia.ocpi.toolkit211.modules.tokens.domain.Token
import com.izivia.ocpi.toolkit211.modules.tokens.domain.TokenType
import com.izivia.ocpi.toolkit211.modules.versions.domain.ModuleID
import com.izivia.ocpi.toolkit211.modules.versions.domain.VersionNumber
import com.izivia.ocpi.toolkit211.modules.versions.repositories.MutableVersionsRepository
import com.izivia.ocpi.toolkit211.serialization.deserializeObject
import com.izivia.ocpi.toolkit211.serialization.mapper
import java.time.Instant

class TokensEmspServer(
    private val service: TokensEmspInterface,
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
            path = basePathSegments,
            queryParams = listOf("date_from", "date_to", "offset", "limit"),
        ) { req ->
            req.respondSearchResult<Token>(timeProvider.now()) {
                service.getTokens(
                    dateFrom = req.optionalQueryParamAsInstant("date_from"),
                    dateTo = req.optionalQueryParamAsInstant("date_to"),
                    offset = req.optionalQueryParamAsInt("offset") ?: 0,
                    limit = req.optionalQueryParamAsInt("limit"),
                )
            }
        }

        transportServer.handle(
            method = HttpMethod.POST,
            path = basePathSegments + listOf(
                VariablePathSegment("tokenUid"),
                FixedPathSegment("authorize"),
            ),
            queryParams = listOf("type"),
        ) { req ->
            req.respondObject(timeProvider.now()) {
                service.postToken(
                    tokenUid = req.pathParam("tokenUid"),
                    type = req.optionalQueryParamAs("type", TokenType::valueOf) ?: TokenType.RFID,
                    locationReferences = req.body
                        ?.takeIf { it.isNotBlank() }
                        ?.let { mapper.deserializeObject<LocationReferences>(it) },
                )
            }
        }
    }
}
