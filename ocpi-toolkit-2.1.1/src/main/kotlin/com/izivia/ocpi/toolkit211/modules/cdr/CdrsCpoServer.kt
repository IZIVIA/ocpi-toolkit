package com.izivia.ocpi.toolkit211.modules.cdr

import com.izivia.ocpi.toolkit.transport.TransportServer
import com.izivia.ocpi.toolkit.transport.domain.HttpMethod
import com.izivia.ocpi.toolkit211.common.*
import com.izivia.ocpi.toolkit211.modules.cdr.domain.Cdr
import com.izivia.ocpi.toolkit211.modules.versions.domain.ModuleID
import com.izivia.ocpi.toolkit211.modules.versions.domain.VersionNumber
import com.izivia.ocpi.toolkit211.modules.versions.repositories.MutableVersionsRepository
import java.time.Instant

class CdrsCpoServer(
    private val service: CdrsCpoInterface,
    private val timeProvider: TimeProvider = TimeProvider { Instant.now() },
    versionsRepository: MutableVersionsRepository? = null,
    basePathOverride: String? = null,
) : OcpiSelfRegisteringModuleServer(
    ocpiVersion = VersionNumber.V2_1_1,
    moduleID = ModuleID.cdrs,
    versionsRepository = versionsRepository,
    basePathOverride = basePathOverride,
) {

    override suspend fun doRegisterOn(transportServer: TransportServer) {
        transportServer.handle(
            method = HttpMethod.GET,
            path = basePathSegments,
            queryParams = listOf("date_from", "date_to", "offset", "limit"),
        ) { req ->
            req.respondSearchResult<Cdr>(timeProvider.now()) {
                service.getCdrs(
                    dateFrom = req.optionalQueryParamAsInstant("date_from"),
                    dateTo = req.optionalQueryParamAsInstant("date_to"),
                    offset = req.optionalQueryParamAsInt("offset") ?: 0,
                    limit = req.optionalQueryParamAsInt("limit"),
                )
            }
        }
    }
}
