package com.izivia.ocpi.toolkit.tests.integration.mock

import com.izivia.ocpi.toolkit.common.SearchResult
import com.izivia.ocpi.toolkit.common.toSearchResult
import com.izivia.ocpi.toolkit.modules.locations.domain.Connector
import com.izivia.ocpi.toolkit.modules.locations.domain.Evse
import com.izivia.ocpi.toolkit.modules.locations.domain.Location
import com.izivia.ocpi.toolkit.modules.locations.repositories.LocationsCpoRepository
import com.mongodb.client.MongoCollection
import org.litote.kmongo.and
import org.litote.kmongo.gte
import org.litote.kmongo.lte
import java.time.Instant

class LocationsCpoMongoRepository(
    private val collection: MongoCollection<Location>,
) : LocationsCpoRepository {

    override suspend fun getLocations(
        dateFrom: Instant?,
        dateTo: Instant?,
        offset: Int,
        limit: Int?,
    ): SearchResult<Location> =
        collection
            .run {
                find(
                    and(
                        dateFrom?.let { Location::lastUpdated gte dateFrom },
                        dateTo?.let { Location::lastUpdated lte dateTo },
                    ),
                )
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

    override suspend fun getLocation(locationId: String): Location? {
        TODO("Not yet implemented")
    }

    override suspend fun getEvse(locationId: String, evseUid: String): Evse? {
        TODO("Not yet implemented")
    }

    override suspend fun getConnector(locationId: String, evseUid: String, connectorId: String): Connector? {
        TODO("Not yet implemented")
    }
}
