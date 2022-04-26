package ocpi.locations

import com.fasterxml.jackson.module.kotlin.readValue
import common.mapper
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

    override fun getLocations(dateFrom: Instant?, dateTo: Instant?, offset: Int?, limit: Int?): List<Location> =
        transportClient
            .send(
                HttpRequest(
                    method = HttpMethod.GET,
                    path = "/ocpi/cpo/2.1.1/locations/",
                    queryParams = listOfNotNull(
                        dateFrom?.let { "date_from" to dateFrom.toString() },
                        dateTo?.let { "date_to" to dateTo.toString() },
                        offset?.let { "offset" to offset.toString() },
                        limit?.let { "limit" to limit.toString() }
                    ).toMap()
                )
            )
            .run {
                mapper.readValue(body)
            }

    override fun getLocation(locationId: String): Location? =
        transportClient
            .send(
                HttpRequest(
                    method = HttpMethod.GET,
                    path = "/ocpi/cpo/2.1.1/locations/$locationId"
                )
            )
            .run {
                mapper.readValue(body)
            }

    override fun getEvse(locationId: String, evseUid: String): Evse? =
        transportClient
            .send(
                HttpRequest(
                    method = HttpMethod.GET,
                    path = "/ocpi/cpo/2.1.1/locations/$locationId/$evseUid"
                )
            )
            .run {
                mapper.readValue(body)
            }

    override fun getConnector(locationId: String, evseUid: String, connectorId: String): Connector? =
        transportClient
            .send(
                HttpRequest(
                    method = HttpMethod.GET,
                    path = "/ocpi/cpo/2.1.1/locations/$locationId/$evseUid/$connectorId"
                )
            )
            .run {
                mapper.readValue(body)
            }
}