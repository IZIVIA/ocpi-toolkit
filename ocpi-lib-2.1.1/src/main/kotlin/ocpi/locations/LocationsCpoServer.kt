package ocpi.locations

import common.OcpiResponseBody
import common.mapper
import common.paginatedHeaders
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
    private val service: LocationsCpoInterface
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

            val response = service.getLocations(
                dateFrom = dateFrom?.let { Instant.parse(it) },
                dateTo = dateTo?.let { Instant.parse(it) },
                offset = req.queryParams["offset"]?.toInt() ?: 0,
                limit = req.queryParams["limit"]?.toInt()
            )

            HttpResponse(
                status = 200,
                body = mapper.writeValueAsString(
                    OcpiResponseBody(
                        data = response.data?.list,
                        status_code = response.status_code,
                        status_message = response.status_message,
                        timestamp = response.timestamp
                    )
                ),
                headers = response.paginatedHeaders(
                    url = "${transportServer.baseUrl}/ocpi/cpo/2.1.1/locations",
                    queryList = listOfNotNull(
                        dateFrom?.let { "dateFrom=$dateFrom" },
                        dateTo?.let { "dateTo=$dateTo" }
                    )
                )
            )
        }

        transportServer.handle(
            method = HttpMethod.GET,
            path = listOf(
                FixedPathSegment("/ocpi/cpo/2.1.1/locations"),
                VariablePathSegment("locationId")
            )
        ) { req ->
            val response = service.getLocation(
                locationId = req.pathParams["locationId"]!!
            )

            HttpResponse(
                status = 200,
                body = mapper.writeValueAsString(response)
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
            val response = service.getEvse(
                locationId = req.pathParams["locationId"]!!,
                evseUid = req.pathParams["evseUid"]!!
            )

            HttpResponse(
                status = 200,
                body = mapper.writeValueAsString(response)
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
            val response = service.getConnector(
                locationId = req.pathParams["locationId"]!!,
                evseUid = req.pathParams["evseUid"]!!,
                connectorId = req.pathParams["connectorId"]!!
            )

            HttpResponse(
                status = 200,
                body = mapper.writeValueAsString(response)
            )
        }
    }
}
