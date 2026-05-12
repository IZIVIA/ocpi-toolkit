package com.izivia.ocpi.toolkit211.modules.locations

import com.izivia.ocpi.toolkit211.common.CiString
import com.izivia.ocpi.toolkit211.common.SearchResult
import com.izivia.ocpi.toolkit211.modules.locations.domain.Connector
import com.izivia.ocpi.toolkit211.modules.locations.domain.Evse
import com.izivia.ocpi.toolkit211.modules.locations.domain.Location
import java.time.Instant

interface LocationsCpoInterface {

    suspend fun getLocations(
        dateFrom: Instant?,
        dateTo: Instant?,
        offset: Int = 0,
        limit: Int?,
    ): SearchResult<Location>

    suspend fun getLocation(locationId: CiString): Location?

    suspend fun getEvse(locationId: CiString, evseUid: CiString): Evse?

    suspend fun getConnector(
        locationId: CiString,
        evseUid: CiString,
        connectorId: CiString,
    ): Connector?
}
