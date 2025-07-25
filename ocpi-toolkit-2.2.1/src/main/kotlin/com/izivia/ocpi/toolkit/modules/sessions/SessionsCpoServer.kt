package com.izivia.ocpi.toolkit.modules.sessions

import com.izivia.ocpi.toolkit.common.*
import com.izivia.ocpi.toolkit.modules.sessions.domain.ChargingPreferences
import com.izivia.ocpi.toolkit.modules.versions.domain.InterfaceRole
import com.izivia.ocpi.toolkit.modules.versions.domain.ModuleID
import com.izivia.ocpi.toolkit.modules.versions.domain.VersionNumber
import com.izivia.ocpi.toolkit.modules.versions.repositories.MutableVersionsRepository
import com.izivia.ocpi.toolkit.transport.TransportServer
import com.izivia.ocpi.toolkit.transport.domain.FixedPathSegment
import com.izivia.ocpi.toolkit.transport.domain.HttpMethod
import com.izivia.ocpi.toolkit.transport.domain.VariablePathSegment

class SessionsCpoServer(
    private val service: SessionsCpoInterface,
    versionsRepository: MutableVersionsRepository? = null,
    basePathOverride: String? = null,
) : OcpiSelfRegisteringModuleServer(
    ocpiVersion = VersionNumber.V2_2_1,
    moduleID = ModuleID.sessions,
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
                service
                    .getSessions(
                        dateFrom = req.queryParamAsInstant("date_from"),
                        dateTo = req.optionalQueryParamAsInstant("date_to"),
                        offset = req.optionalQueryParamAsInt("offset") ?: 0,
                        limit = req.optionalQueryParamAsInt("limit"),
                    )
            }
        }

        transportServer.handle(
            method = HttpMethod.PUT,
            path = basePathSegments + listOf(
                VariablePathSegment("sessionId"),
                FixedPathSegment("charging_preferences"),
            ),
        ) { req ->
            req.httpResponse {
                service
                    .putChargingPreferences(
                        sessionId = req.pathParam("sessionId"),
                        chargingPreferences = mapper.readValue(req.body, ChargingPreferences::class.java),
                    )
            }
        }
    }
}
