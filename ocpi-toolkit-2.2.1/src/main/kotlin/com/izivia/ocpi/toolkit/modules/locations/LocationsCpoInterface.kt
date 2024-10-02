package com.izivia.ocpi.toolkit.modules.locations

import com.izivia.ocpi.toolkit.common.CiString
import com.izivia.ocpi.toolkit.common.OcpiResponseBody
import com.izivia.ocpi.toolkit.common.SearchResult
import com.izivia.ocpi.toolkit.modules.locations.domain.Connector
import com.izivia.ocpi.toolkit.modules.locations.domain.Evse
import com.izivia.ocpi.toolkit.modules.locations.domain.Location
import java.time.Instant

/**
 * CPO Interface
 *
 * - GET: Fetch a list locations, last updated between the {date_from} and {date_to} (paginated), or get a specific
 * location, EVSE or Connector.
 * - POST: n/a
 * - PUT: n/a
 * - PATCH: n/a
 * - DELETE: n/a
 */
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
    suspend fun getLocations(
        dateFrom: Instant?,
        dateTo: Instant?,
        offset: Int = 0,
        limit: Int?,
    ): OcpiResponseBody<SearchResult<Location>>

    /**
     * @param locationId String max-length = 36
     */
    suspend fun getLocation(locationId: CiString): OcpiResponseBody<Location?>

    /**
     * @param locationId String max-length = 36
     * @param evseUid String? max-length = 36
     */
    suspend fun getEvse(locationId: CiString, evseUid: CiString): OcpiResponseBody<Evse?>

    /**
     * @param locationId String max-length = 36
     * @param evseUid max-length = 36
     * @param connectorId max-length = 36
     */
    suspend fun getConnector(
        locationId: CiString,
        evseUid: CiString,
        connectorId: CiString,
    ): OcpiResponseBody<Connector?>
}
