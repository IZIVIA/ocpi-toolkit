package com.izivia.ocpi.toolkit.modules.locations

import com.izivia.ocpi.toolkit.common.OcpiModuleServer
import com.izivia.ocpi.toolkit.common.httpResponse
import com.izivia.ocpi.toolkit.transport.TransportServer
import com.izivia.ocpi.toolkit.transport.domain.HttpMethod
import com.izivia.ocpi.toolkit.transport.domain.VariablePathSegment
import java.time.Instant

/**
 * Receives calls from a CPO
 * @property transportServer
 */
class LocationsCpoServer(
    private val service: LocationsCpoInterface,
    basePath: String = "/2.2.1/locations"
) : OcpiModuleServer(basePath) {

    override fun registerOn(transportServer: TransportServer) {
        transportServer.handle(
            method = HttpMethod.GET,
            path = basePathSegments,
            queryParams = listOf("date_from", "date_to", "offset", "limit")
        ) { req ->
            req.httpResponse {
                val dateFrom = req.queryParams["date_from"]
                val dateTo = req.queryParams["date_to"]

                service
                    .getLocations(
                        dateFrom = dateFrom?.let { Instant.parse(it) },
                        dateTo = dateTo?.let { Instant.parse(it) },
                        offset = req.queryParams["offset"]?.toInt() ?: 0,
                        limit = req.queryParams["limit"]?.toInt()
                    )
            }
        }

        transportServer.handle(
            method = HttpMethod.GET,
            path = basePathSegments + listOf(
                VariablePathSegment("locationId")
            )
        ) { req ->
            req.httpResponse {
                service
                    .getLocation(
                        locationId = req.pathParams["locationId"]!!
                    )
            }
        }

        transportServer.handle(
            method = HttpMethod.GET,
            path = basePathSegments + listOf(
                VariablePathSegment("locationId"),
                VariablePathSegment("evseUid")
            )
        ) { req ->
            req.httpResponse {
                service
                    .getEvse(
                        locationId = req.pathParams["locationId"]!!,
                        evseUid = req.pathParams["evseUid"]!!
                    )
            }
        }

        transportServer.handle(
            method = HttpMethod.GET,
            path = basePathSegments + listOf(
                VariablePathSegment("locationId"),
                VariablePathSegment("evseUid"),
                VariablePathSegment("connectorId")
            )
        ) { req ->
            req.httpResponse {
                service
                    .getConnector(
                        locationId = req.pathParams["locationId"]!!,
                        evseUid = req.pathParams["evseUid"]!!,
                        connectorId = req.pathParams["connectorId"]!!
                    )
            }
        }
    }
}
