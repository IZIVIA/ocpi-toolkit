package com.izivia.ocpi.toolkit.tests.integration.mock

import com.izivia.ocpi.toolkit.common.SearchResult
import com.izivia.ocpi.toolkit.common.context.currentRequestMessageRoutingHeadersOrNull
import com.izivia.ocpi.toolkit.common.context.currentResponseMessageRoutingHeaders
import com.izivia.ocpi.toolkit.modules.locations.domain.Connector
import com.izivia.ocpi.toolkit.modules.locations.domain.Evse
import com.izivia.ocpi.toolkit.modules.locations.domain.Location
import com.izivia.ocpi.toolkit.modules.locations.repositories.LocationsCpoRepository
import java.time.Instant

class CustomLocationsCpoService(
    private val repository: LocationsCpoMongoRepository
) : LocationsCpoRepository {
    override suspend fun getLocations(
        dateFrom: Instant?,
        dateTo: Instant?,
        offset: Int,
        limit: Int?
    ): SearchResult<Location> {
        // This is an example, you will probably need something more specific
        // if you want to handle message routing headers
        val requestMessageRoutingHeaders = currentRequestMessageRoutingHeadersOrNull()
        val responseMessageRoutingHeaders = currentResponseMessageRoutingHeaders()
        responseMessageRoutingHeaders.toPartyId = requestMessageRoutingHeaders?.fromPartyId
        responseMessageRoutingHeaders.toCountryCode = requestMessageRoutingHeaders?.fromCountryCode
        responseMessageRoutingHeaders.fromPartyId = requestMessageRoutingHeaders?.toPartyId
        responseMessageRoutingHeaders.fromCountryCode = requestMessageRoutingHeaders?.toCountryCode

        return repository.getLocations(dateFrom, dateTo, offset, limit)
    }

    override suspend fun getLocation(locationId: String): Location? =
        repository.getLocation(locationId)

    override suspend fun getEvse(locationId: String, evseUid: String): Evse? =
        repository.getEvse(locationId, evseUid)

    override suspend fun getConnector(locationId: String, evseUid: String, connectorId: String): Connector? =
        repository.getConnector(locationId, evseUid, connectorId)
}
