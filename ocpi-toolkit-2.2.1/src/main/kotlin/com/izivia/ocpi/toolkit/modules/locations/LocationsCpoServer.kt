package com.izivia.ocpi.toolkit.modules.locations

import com.izivia.ocpi.toolkit.common.httpResponse
import com.izivia.ocpi.toolkit.common.tokenFilter
import com.izivia.ocpi.toolkit.modules.credentials.repositories.PlatformRepository
import com.izivia.ocpi.toolkit.transport.TransportServer
import com.izivia.ocpi.toolkit.transport.domain.FixedPathSegment
import com.izivia.ocpi.toolkit.transport.domain.HttpMethod
import com.izivia.ocpi.toolkit.transport.domain.VariablePathSegment
import java.time.Instant

/**
 * Receives calls from a CPO
 * @property transportServer
 */
class LocationsCpoServer(
    private val transportServer: TransportServer,
    private val platformRepository: PlatformRepository,
    private val service: LocationsCpoInterface,
    basePath: List<FixedPathSegment> = listOf(
        FixedPathSegment("/2.2.1/locations")
    )
) {

    init {
        transportServer.handle(
            method = HttpMethod.GET,
            path = basePath,
            queryParams = listOf("date_from", "date_to", "offset", "limit"),
            filters = listOf(platformRepository::tokenFilter)
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
            path = basePath + listOf(
                VariablePathSegment("locationId")
            ),
            filters = listOf(platformRepository::tokenFilter)
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
            path = basePath + listOf(
                VariablePathSegment("locationId"),
                VariablePathSegment("evseUid")
            ),
            filters = listOf(platformRepository::tokenFilter)
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
            path = basePath + listOf(
                VariablePathSegment("locationId"),
                VariablePathSegment("evseUid"),
                VariablePathSegment("connectorId")
            ),
            filters = listOf(platformRepository::tokenFilter)
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
