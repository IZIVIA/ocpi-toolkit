package com.izivia.ocpi.toolkit.modules.tariff

import com.izivia.ocpi.toolkit.common.*
import com.izivia.ocpi.toolkit.modules.versions.domain.InterfaceRole
import com.izivia.ocpi.toolkit.modules.versions.domain.ModuleID
import com.izivia.ocpi.toolkit.modules.versions.domain.VersionNumber
import com.izivia.ocpi.toolkit.modules.versions.repositories.MutableVersionsRepository
import com.izivia.ocpi.toolkit.transport.TransportServer
import com.izivia.ocpi.toolkit.transport.domain.HttpMethod
import java.time.Instant

class TariffCpoServer(
    private val service: TariffCpoInterface,
    private val timeProvider: TimeProvider = TimeProvider { Instant.now() },
    versionsRepository: MutableVersionsRepository? = null,
    basePathOverride: String? = null,
) : OcpiSelfRegisteringModuleServer(
    ocpiVersion = VersionNumber.V2_2_1,
    moduleID = ModuleID.tariffs,
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
            req.respondSearchResult(timeProvider.now()) {
                service
                    .getTariffs(
                        dateFrom = req.optionalQueryParamAsInstant("date_from"),
                        dateTo = req.optionalQueryParamAsInstant("date_to"),
                        offset = req.optionalQueryParamAsInt("offset") ?: 0,
                        limit = req.optionalQueryParamAsInt("limit"),
                    )
            }
        }
    }
}
