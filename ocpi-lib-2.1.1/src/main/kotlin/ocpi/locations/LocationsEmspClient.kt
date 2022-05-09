package ocpi.locations

import common.*
import ocpi.locations.domain.Connector
import ocpi.locations.domain.Evse
import ocpi.locations.domain.Location
import transport.TransportClient
import transport.domain.HttpMethod
import transport.domain.HttpRequest
import java.time.Instant

/**
 * Sends calls to the CPO
 * @property transportClient
 */
class LocationsEmspClient(
    private val transportClient: TransportClient
) : LocationsCpoInterface {

    override fun getLocations(
        token: String,
        dateFrom: Instant?,
        dateTo: Instant?,
        offset: Int,
        limit: Int?
    ): OcpiResponseBody<SearchResult<Location>> =
        transportClient
            .send(
                HttpRequest(
                    method = HttpMethod.GET,
                    path = "/locations",
                    queryParams = listOfNotNull(
                        dateFrom?.let { "date_from" to dateFrom.toString() },
                        dateTo?.let { "date_to" to dateTo.toString() },
                        "offset" to offset.toString(),
                        limit?.let { "limit" to limit.toString() }
                    ).toMap(),
                    headers = mapOf(authorizationHeader(token = token))
                )
            )
            .parsePaginatedBody(offset)

    override fun getLocation(token: String, locationId: String): OcpiResponseBody<Location?> =
        transportClient
            .send(
                HttpRequest(
                    method = HttpMethod.GET,
                    path = "/locations/$locationId",
                    headers = mapOf(authorizationHeader(token = token))
                )
            )
            .parseBody()

    override fun getEvse(token: String, locationId: String, evseUid: String): OcpiResponseBody<Evse?> =
        transportClient
            .send(
                HttpRequest(
                    method = HttpMethod.GET,
                    path = "/locations/$locationId/$evseUid",
                    headers = mapOf(authorizationHeader(token = token))
                )
            )
            .parseBody()

    override fun getConnector(
        token: String,
        locationId: String,
        evseUid: String,
        connectorId: String
    ): OcpiResponseBody<Connector?> =
        transportClient
            .send(
                HttpRequest(
                    method = HttpMethod.GET,
                    path = "/locations/$locationId/$evseUid/$connectorId",
                    headers = mapOf(authorizationHeader(token = token))
                )
            )
            .parseBody()
}
