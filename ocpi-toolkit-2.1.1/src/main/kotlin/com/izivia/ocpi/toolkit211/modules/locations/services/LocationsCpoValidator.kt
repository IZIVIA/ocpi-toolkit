package com.izivia.ocpi.toolkit211.modules.locations.services

import com.izivia.ocpi.toolkit211.common.CiString
import com.izivia.ocpi.toolkit211.common.SearchResult
import com.izivia.ocpi.toolkit211.common.validation.validate
import com.izivia.ocpi.toolkit211.common.validation.validateDates
import com.izivia.ocpi.toolkit211.common.validation.validateInt
import com.izivia.ocpi.toolkit211.common.validation.validateLength
import com.izivia.ocpi.toolkit211.modules.locations.LocationsCpoInterface
import com.izivia.ocpi.toolkit211.modules.locations.domain.Connector
import com.izivia.ocpi.toolkit211.modules.locations.domain.Evse
import com.izivia.ocpi.toolkit211.modules.locations.domain.Location
import java.time.Instant

open class LocationsCpoValidator(
    private val service: LocationsCpoInterface,
) : LocationsCpoInterface {

    override suspend fun getLocations(
        dateFrom: Instant?,
        dateTo: Instant?,
        offset: Int,
        limit: Int?,
    ): SearchResult<Location> {
        validate {
            if (dateFrom != null && dateTo != null) validateDates("dateFrom", dateFrom, "dateTo", dateTo)
            if (limit != null) validateInt("limit", limit, 0, null)
            validateInt("offset", offset, 0, null)
        }

        return service
            .getLocations(dateFrom, dateTo, offset, limit)
            .also { searchResult ->
                searchResult.list.forEach { location -> location.validate() }
            }
    }

    override suspend fun getLocation(locationId: CiString): Location? {
        validate {
            validateLength("locationId", locationId, 39)
        }

        return service
            .getLocation(locationId)
            ?.validate()
    }

    override suspend fun getEvse(locationId: CiString, evseUid: CiString): Evse? {
        validate {
            validateLength("locationId", locationId, 39)
            validateLength("evseUid", evseUid, 39)
        }

        return service
            .getEvse(locationId, evseUid)
            ?.validate()
    }

    override suspend fun getConnector(
        locationId: CiString,
        evseUid: CiString,
        connectorId: CiString,
    ): Connector? {
        validate {
            validateLength("locationId", locationId, 39)
            validateLength("evseUid", evseUid, 39)
            validateLength("connectorId", connectorId, 36)
        }

        return service
            .getConnector(locationId, evseUid, connectorId)
            ?.validate()
    }
}
