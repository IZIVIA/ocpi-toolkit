package com.izivia.ocpi.toolkit.modules.cdr

import com.izivia.ocpi.toolkit.common.OcpiSelfRegisteringModuleServer
import com.izivia.ocpi.toolkit.common.httpResponse
import com.izivia.ocpi.toolkit.modules.versions.domain.InterfaceRole
import com.izivia.ocpi.toolkit.modules.versions.domain.ModuleID
import com.izivia.ocpi.toolkit.modules.versions.domain.VersionNumber
import com.izivia.ocpi.toolkit.modules.versions.repositories.MutableVersionsRepository
import com.izivia.ocpi.toolkit.transport.TransportServer
import com.izivia.ocpi.toolkit.transport.domain.HttpMethod
import java.time.Instant

class CdrsCpoServer(
    private val service: CdrsCpoInterface,
    versionsRepository: MutableVersionsRepository? = null,
    basePathOverride: String? = null,
) : OcpiSelfRegisteringModuleServer(
    ocpiVersion = VersionNumber.V2_2_1,
    moduleID = ModuleID.cdrs,
    interfaceRole = InterfaceRole.SENDER,
    versionsRepository = versionsRepository,
    basePathOverride = basePathOverride,
) {
    override suspend fun doRegisterOn(transportServer: TransportServer) {
        transportServer.handle(
            method = HttpMethod.GET,
            path = basePathSegments,
            queryParams = listOf("date_from", "date_to", "offset", "limit"),
        ) { req ->
            req.httpResponse {
                val dateFrom = req.queryParams["date_from"]
                val dateTo = req.queryParams["date_to"]

                service
                    .getCdrs(
                        dateFrom = dateFrom?.let { Instant.parse(it) },
                        dateTo = dateTo?.let { Instant.parse(it) },
                        offset = req.queryParams["offset"]?.toInt() ?: 0,
                        limit = req.queryParams["limit"]?.toInt(),
                    )
            }
        }
    }
}
