package ocpi.locations

import common.OcpiResponseBody
import common.mapper
import transport.TransportServer
import transport.domain.FixedPathSegment
import transport.domain.HttpMethod
import transport.domain.HttpResponse
import transport.domain.VariablePathSegment
import java.time.Instant

/**
 * Receives calls from a CPO
 * @property transportServer
 */
class LocationsCpoServer(
    private val transportServer: TransportServer,
    private val controller: LocationsCpoInterface
) {

    init {
        transportServer.handle(
            method = HttpMethod.GET,
            path = listOf(
                FixedPathSegment("/ocpi/cpo/2.1.1/locations")
            ),
            queryParams = listOf("dateFrom", "dateTo", "offset", "limit")
        ) { req ->
            val dateFrom = req.queryParams["dateFrom"]
            val dateTo = req.queryParams["dateTo"]

            val locationSearchResult = controller.getLocations(
                dateFrom = dateFrom?.let { Instant.parse(it) },
                dateTo = dateTo?.let { Instant.parse(it) },
                offset = req.queryParams["offset"]?.toInt(),
                limit = req.queryParams["limit"]?.toInt()
            )

            val nextPageOffset = (locationSearchResult.offset + locationSearchResult.limit)
                .takeIf { it <= locationSearchResult.totalCount }

            val params = listOfNotNull(
                dateFrom?.let { "dateFrom=$dateFrom" },
                dateTo?.let { "dateTo=$dateTo" },
                nextPageOffset?.let { "offset=$nextPageOffset" },
                "limit=${locationSearchResult.limit}"
            ).joinToString("&", "?")

            HttpResponse(
                status = 200,
                body = mapper.writeValueAsString(mapper.writeValueAsString(OcpiResponseBody.success(locationSearchResult.list))),
                headers = listOfNotNull(
                    nextPageOffset?.let { "Link" to "<${transportServer.baseUrl}/ocpi/cpo/2.1.1/locations$params>; rel=\"next\"" },
                    "X-Total-Count" to locationSearchResult.totalCount.toString(),
                    "X-Limit" to locationSearchResult.limit.toString()
                ).toMap()
            )
        }

        transportServer.handle(
            method = HttpMethod.GET,
            path = listOf(
                FixedPathSegment("/ocpi/cpo/2.1.1/locations"),
                VariablePathSegment("locationId")
            )
        ) { req ->
            val location = controller.getLocation(
                locationId = req.pathParams["locationId"]!!
            )

            HttpResponse(
                status = 200,
                body = mapper.writeValueAsString(mapper.writeValueAsString(OcpiResponseBody.success(location)))
            )
        }

        transportServer.handle(
            method = HttpMethod.GET,
            path = listOf(
                FixedPathSegment("/ocpi/cpo/2.1.1/locations"),
                VariablePathSegment("locationId"),
                VariablePathSegment("evseUid")
            )
        ) { req ->
            val evse = controller.getEvse(
                locationId = req.pathParams["locationId"]!!,
                evseUid = req.pathParams["evseUid"]!!
            )

            HttpResponse(
                status = 200,
                body = mapper.writeValueAsString(OcpiResponseBody.success(evse))
            )
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
            val connector = controller.getConnector(
                locationId = req.pathParams["locationId"]!!,
                evseUid = req.pathParams["evseUid"]!!,
                connectorId = req.pathParams["connectorId"]!!
            )

            HttpResponse(
                status = 200,
                body = mapper.writeValueAsString(OcpiResponseBody.success(connector))
            )
        }
    }
}
