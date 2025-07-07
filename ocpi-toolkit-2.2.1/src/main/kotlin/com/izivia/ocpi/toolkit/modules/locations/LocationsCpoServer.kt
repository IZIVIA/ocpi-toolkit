package com.izivia.ocpi.toolkit.modules.locations

import com.izivia.ocpi.toolkit.common.*
import com.izivia.ocpi.toolkit.modules.versions.domain.InterfaceRole
import com.izivia.ocpi.toolkit.modules.versions.domain.ModuleID
import com.izivia.ocpi.toolkit.modules.versions.domain.VersionNumber
import com.izivia.ocpi.toolkit.modules.versions.repositories.MutableVersionsRepository
import com.izivia.ocpi.toolkit.transport.TransportServer
import com.izivia.ocpi.toolkit.transport.domain.HttpMethod
import com.izivia.ocpi.toolkit.transport.domain.VariablePathSegment

class LocationsCpoServer(
    private val service: LocationsCpoInterface,
    versionsRepository: MutableVersionsRepository? = null,
    basePathOverride: String? = null,
) : OcpiSelfRegisteringModuleServer(
    ocpiVersion = VersionNumber.V2_2_1,
    moduleID = ModuleID.locations,
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
                    .getLocations(
                        dateFrom = req.optionalQueryParamAsInstant("date_from"),
                        dateTo = req.optionalQueryParamAsInstant("date_to"),
                        offset = req.optionalQueryParamAsInt("offset") ?: 0,
                        limit = req.optionalQueryParamAsInt("limit"),
                    )
            }
        }

        transportServer.handle(
            method = HttpMethod.GET,
            path = basePathSegments + listOf(
                VariablePathSegment("locationId"),
            ),
        ) { req ->
            req.httpResponse {
                service
                    .getLocation(
                        locationId = req.pathParam("locationId"),
                    )
            }
        }

        transportServer.handle(
            method = HttpMethod.GET,
            path = basePathSegments + listOf(
                VariablePathSegment("locationId"),
                VariablePathSegment("evseUid"),
            ),
        ) { req ->
            req.httpResponse {
                service
                    .getEvse(
                        locationId = req.pathParam("locationId"),
                        evseUid = req.pathParam("evseUid"),
                    )
            }
        }

        transportServer.handle(
            method = HttpMethod.GET,
            path = basePathSegments + listOf(
                VariablePathSegment("locationId"),
                VariablePathSegment("evseUid"),
                VariablePathSegment("connectorId"),
            ),
        ) { req ->
            req.httpResponse {
                service
                    .getConnector(
                        locationId = req.pathParam("locationId"),
                        evseUid = req.pathParam("evseUid"),
                        connectorId = req.pathParam("connectorId"),
                    )
            }
        }
    }
}
