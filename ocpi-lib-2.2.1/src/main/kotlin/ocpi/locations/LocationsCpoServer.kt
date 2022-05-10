package ocpi.locations

import common.httpResponse
import common.tokenFilter
import ocpi.credentials.repositories.PlatformRepository
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
    private val platformRepository: PlatformRepository,
    private val service: LocationsCpoInterface
) {

    init {
        transportServer.handle(
            method = HttpMethod.GET,
            path = listOf(
                FixedPathSegment("/locations")
            ),
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
            path = listOf(
                FixedPathSegment("/locations"),
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
            path = listOf(
                FixedPathSegment("/locations"),
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
            path = listOf(
                FixedPathSegment("/locations"),
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
