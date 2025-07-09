package com.izivia.ocpi.toolkit.modules.locations.services

import com.izivia.ocpi.toolkit.common.CiString
import com.izivia.ocpi.toolkit.common.SearchResult
import com.izivia.ocpi.toolkit.common.validation.validateDates
import com.izivia.ocpi.toolkit.common.validation.validateInt
import com.izivia.ocpi.toolkit.common.validation.validateLength
import com.izivia.ocpi.toolkit.common.validation.validateParams
import com.izivia.ocpi.toolkit.modules.locations.LocationsCpoInterface
import com.izivia.ocpi.toolkit.modules.locations.domain.Connector
import com.izivia.ocpi.toolkit.modules.locations.domain.Evse
import com.izivia.ocpi.toolkit.modules.locations.domain.Location
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
        validateParams {
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

    override suspend fun getLocation(
        locationId: CiString,
    ): Location? {
        validateParams {
            validateLength("locationId", locationId, 36)
        }

        return service
            .getLocation(locationId)
            ?.validate()
    }

    override suspend fun getEvse(
        locationId: CiString,
        evseUid: CiString,
    ): Evse? {
        validateParams {
            validateLength("locationId", locationId, 36)
            validateLength("evseUid", evseUid, 36)
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
        validateParams {
            validateLength("locationId", locationId, 36)
            validateLength("evseUid", evseUid, 36)
            validateLength("connectorId", connectorId, 36)
        }

        return service
            .getConnector(locationId, evseUid, connectorId)
            ?.validate()
    }
}
