package ocpi.locations

import common.toHttpResponse
import common.toPaginatedHttpResponse
import transport.TransportServer
import transport.domain.FixedPathSegment
import transport.domain.HttpMethod
import transport.domain.VariablePathSegment
import java.time.Instant

/**
 * Receives calls from a CPO
 * @property transportServer
 */
class LocationsCpoServer(
    private val transportServer: TransportServer,
    private val service: LocationsCpoInterface
) {

    init {
        transportServer.handle(
            method = HttpMethod.GET,
            path = listOf(
                FixedPathSegment("/ocpi/cpo/2.1.1/locations")
            ),
            queryParams = listOf("date_from", "date_to", "offset", "limit")
        ) { req ->
            val dateFrom = req.queryParams["date_from"]
            val dateTo = req.queryParams["date_to"]

            service
                .getLocations(
                    dateFrom = dateFrom?.let { Instant.parse(it) },
                    dateTo = dateTo?.let { Instant.parse(it) },
                    offset = req.queryParams["offset"]?.toInt() ?: 0,
                    limit = req.queryParams["limit"]?.toInt()
                )
                .toPaginatedHttpResponse(request = req)
        }

        transportServer.handle(
            method = HttpMethod.GET,
            path = listOf(
                FixedPathSegment("/ocpi/cpo/2.1.1/locations"),
                VariablePathSegment("locationId")
            )
        ) { req ->
            service
                .getLocation(
                    locationId = req.pathParams["locationId"]!!
                )
                .toHttpResponse()
        }

        transportServer.handle(
            method = HttpMethod.GET,
            path = listOf(
                FixedPathSegment("/ocpi/cpo/2.1.1/locations"),
                VariablePathSegment("locationId"),
                VariablePathSegment("evseUid")
            )
        ) { req ->
            service
                .getEvse(
                    locationId = req.pathParams["locationId"]!!,
                    evseUid = req.pathParams["evseUid"]!!
                )
                .toHttpResponse()
        }

        transportServer.handle(
            method = HttpMethod.GET,
            path = listOf(
                FixedPathSegment("/ocpi/cpo/2.1.1/locations"),
                VariablePathSegment("locationId"),
                VariablePathSegment("evseUid"),
                VariablePathSegment("connectorId")
            )
        ) { req ->
            service
                .getConnector(
                    locationId = req.pathParams["locationId"]!!,
                    evseUid = req.pathParams["evseUid"]!!,
                    connectorId = req.pathParams["connectorId"]!!
                )
                .toHttpResponse()
        }
    }
}
