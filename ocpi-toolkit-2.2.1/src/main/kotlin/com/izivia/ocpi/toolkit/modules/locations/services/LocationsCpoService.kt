package com.izivia.ocpi.toolkit.modules.locations.services

import com.izivia.ocpi.toolkit.common.CiString
import com.izivia.ocpi.toolkit.common.OcpiResponseBody
import com.izivia.ocpi.toolkit.common.SearchResult
import com.izivia.ocpi.toolkit.common.validation.validate
import com.izivia.ocpi.toolkit.common.validation.validateDates
import com.izivia.ocpi.toolkit.common.validation.validateInt
import com.izivia.ocpi.toolkit.common.validation.validateLength
import com.izivia.ocpi.toolkit.modules.locations.LocationsCpoInterface
import com.izivia.ocpi.toolkit.modules.locations.domain.Connector
import com.izivia.ocpi.toolkit.modules.locations.domain.Evse
import com.izivia.ocpi.toolkit.modules.locations.domain.Location
import com.izivia.ocpi.toolkit.modules.locations.repositories.LocationsCpoRepository
import java.time.Instant

open class LocationsCpoService(
    private val service: LocationsCpoRepository,
) : LocationsCpoInterface {

    override suspend fun getLocations(
        dateFrom: Instant?,
        dateTo: Instant?,
        offset: Int,
        limit: Int?,
    ): OcpiResponseBody<SearchResult<Location>> = OcpiResponseBody.of {
        validate {
            if (dateFrom != null && dateTo != null) validateDates("dateFrom", dateFrom, "dateTo", dateTo)
            if (limit != null) validateInt("limit", limit, 0, null)
            validateInt("offset", offset, 0, null)
        }

        service
            .getLocations(dateFrom, dateTo, offset, limit)
            .also { searchResult ->
                searchResult.list.forEach { location -> location.validate() }
            }
    }

    override suspend fun getLocation(
        locationId: CiString,
    ): OcpiResponseBody<Location?> = OcpiResponseBody.of {
        validate {
            validateLength("locationId", locationId, 36)
        }

        service
            .getLocation(locationId)
            ?.validate()
    }

    override suspend fun getEvse(
        locationId: CiString,
        evseUid: CiString,
    ): OcpiResponseBody<Evse?> = OcpiResponseBody.of {
        validate {
            validateLength("locationId", locationId, 36)
            validateLength("evseUid", evseUid, 36)
        }

        service
            .getEvse(locationId, evseUid)
            ?.validate()
    }

    override suspend fun getConnector(
        locationId: CiString,
        evseUid: CiString,
        connectorId: CiString,
    ): OcpiResponseBody<Connector?> = OcpiResponseBody.of {
        validate {
            validateLength("locationId", locationId, 36)
            validateLength("evseUid", evseUid, 36)
            validateLength("connectorId", connectorId, 36)
        }

        service
            .getConnector(locationId, evseUid, connectorId)
            ?.validate()
    }
}
