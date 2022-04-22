package ocpi.location

import ocpi.connector.Connector
import ocpi.evse.Evse
import java.time.Instant

interface LocationsCpoInterface {

    /**
     * If additional parameters: {date_from} and/or {date_to} are provided, only Locations with
     * (last_updated) between the given date_from and date_to will be returned. If an EVSE is
     * updated, also the 'parent' Location's last_updated fields is updated. If a Connector is
     * updated, the EVSE's last_updated and the Location's last_updated field are updated.
     *
     * This request is paginated, it supports the pagination related URL parameters.
     *
     * @param dateFrom Instant? Only return Locations that have last_updated after this Date/Time.
     * @param dateTo Instant? Only return Locations that have last_updated before this Date/Time.
     * @param offset Int? The offset of the first object returned. Default is 0.
     * @param limit Int? Maximum number of objects to GET.
     * @return List<Location> The endpoint returns a list of Location objects The header will
     * contain the pagination related headers.
     */
    fun getLocations(dateFrom: Instant?, dateTo: Instant?, offset: Int?, limit: Int?): List<Location>

    /**
     * @param locationId String max-length = 39
     */
    fun getLocation(locationId: String): Location

    /**
     * @param locationId String max-length = 39
     * @param evseUid String? max-length = 39
     */
    fun getLocation(locationId: String, evseUid: String): Evse

    /**
     * @param locationId String max-length = 39
     * @param evseUid max-length = 39
     * @param connectorId String? max-length = 39
     */
    fun getLocation(locationId: String, evseUid: String, connectorId: String): Connector
}