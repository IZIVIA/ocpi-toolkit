package ocpi.locations.repositories

import common.SearchResult
import ocpi.locations.domain.Connector
import ocpi.locations.domain.Evse
import ocpi.locations.domain.Location
import java.time.Instant

interface LocationsCpoRepository {
    fun getLocations(dateFrom: Instant?, dateTo: Instant?, offset: Int = 0, limit: Int?): SearchResult<Location>
    fun getLocation(locationId: String): Location?
    fun getEvse(locationId: String, evseUid: String): Evse?
    fun getConnector(locationId: String, evseUid: String, connectorId: String): Connector?
}