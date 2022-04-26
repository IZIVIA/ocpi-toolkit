package ocpi.locations

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
            HttpMethod.GET,
            listOf(
                FixedPathSegment("/ocpi/cpo/2.0/locations")
            ),
            listOf("dateFrom", "dateTo", "offset", "limit")
        ) { req ->
            val location = controller.getLocations(
                dateFrom = req.queryParams["dateFrom"]?.let { Instant.parse(it) },
                dateTo = req.queryParams["dateTo"]?.let { Instant.parse(it) },
                offset = req.queryParams["offset"]?.toInt(),
                limit = req.queryParams["limit"]?.toInt()
            )

            HttpResponse(
                status = 200,
                body = mapper.writeValueAsString(location)
            )
        }

        transportServer.handle(
            HttpMethod.GET,
            listOf(
                FixedPathSegment("/ocpi/cpo/2.0/locations"),
                VariablePathSegment("locationId")
            )
        ) { req ->
            val location = controller.getLocation(
                locationId = req.pathParams["locationId"]!!
            )

            HttpResponse(
                status = 200,
                body = mapper.writeValueAsString(location)
            )
        }

        transportServer.handle(
            HttpMethod.GET,
            listOf(
                FixedPathSegment("/ocpi/cpo/2.0/locations"),
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
                body = mapper.writeValueAsString(evse)
            )
        }

        transportServer.handle(
            HttpMethod.GET,
            listOf(
                FixedPathSegment("/ocpi/cpo/2.0/locations"),
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
                body = mapper.writeValueAsString(connector)
            )
        }
    }
}