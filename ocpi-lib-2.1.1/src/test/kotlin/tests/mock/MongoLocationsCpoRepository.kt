package tests.mock

import com.mongodb.client.MongoCollection
import com.mongodb.client.model.Filters.and
import common.SearchResult
import common.toSearchResult
import ocpi.locations.domain.Connector
import ocpi.locations.domain.Evse
import ocpi.locations.domain.Location
import ocpi.locations.repositories.LocationsCpoRepository
import org.litote.kmongo.gte
import org.litote.kmongo.lte
import java.time.Instant

class LocationsCpoMongoRepository(
    private val collection: MongoCollection<Location>
): LocationsCpoRepository {

    override fun getLocations(dateFrom: Instant?, dateTo: Instant?, offset: Int, limit: Int?): SearchResult<Location> =
        collection
            .run {
                if (dateFrom != null || dateTo != null) {
                    if (dateFrom == null)
                        find(Location::last_updated lte dateTo)
                    else if (dateTo == null)
                        find(Location::last_updated gte dateFrom )
                    else
                        find(
                            and(
                                Location::last_updated gte dateFrom,
                                Location::last_updated lte dateTo
                            )
                        )
                } else {
                    find()
                }
            }
            .toList()
            .let {
                val actualLimit = limit ?: 10
                val size = it.size

                it
                    .filterIndexed { index: Int, _: Location -> index + 1 > offset }
                    .take(actualLimit)
                    .toSearchResult(totalCount = size, limit = actualLimit, offset = offset)
            }

    override fun getLocation(locationId: String): Location? {
        TODO("Not yet implemented")
    }

    override fun getEvse(locationId: String, evseUid: String): Evse? {
        TODO("Not yet implemented")
    }

    override fun getConnector(locationId: String, evseUid: String, connectorId: String): Connector? {
        TODO("Not yet implemented")
    }
}