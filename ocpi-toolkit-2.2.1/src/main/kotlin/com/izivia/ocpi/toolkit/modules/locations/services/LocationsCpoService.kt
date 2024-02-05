package com.izivia.ocpi.toolkit.modules.locations.services

import com.izivia.ocpi.toolkit.common.*
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
    private val service: LocationsCpoRepository
) : LocationsCpoInterface {

    override suspend fun getLocations(
        dateFrom: Instant?,
        dateTo: Instant?,
        offset: Int,
        limit: Int?,
        countryCode: String?,
        partyId: String?
    ): OcpiResponseBody<SearchResult<Location>> = OcpiResponseBody.of {
        validate {
            if (dateFrom != null && dateTo != null) validateDates("dateFrom", dateFrom, "dateTo", dateTo)
            if (limit != null) validateInt("limit", limit, 0, null)
            validateInt("offset", offset, 0, null)
            if (countryCode != null) validateLength("countryCode", countryCode, 2)
            if (partyId != null) validateLength("partyId", partyId, 3)
        }

        service
            .getLocations(dateFrom, dateTo, offset, limit, countryCode, partyId)
            .also { searchResult ->
                searchResult.list.forEach { location -> location.validate() }
            }
    }

    override suspend fun getLocation(
        locationId: CiString,
        countryCode: String?,
        partyId: String?
    ): OcpiResponseBody<Location?> = OcpiResponseBody.of {
        validate {
            validateLength("locationId", locationId, 36)
            if (countryCode != null) validateLength("countryCode", countryCode, 2)
            if (partyId != null) validateLength("partyId", partyId, 3)
        }

        service
            .getLocation(locationId, countryCode, partyId)
            ?.validate()
    }

    override suspend fun getEvse(
        locationId: CiString,
        evseUid: CiString,
        countryCode: String?,
        partyId: String?
    ): OcpiResponseBody<Evse?> = OcpiResponseBody.of {
        validate {
            validateLength("locationId", locationId, 36)
            validateLength("evseUid", evseUid, 36)
            if (countryCode != null) validateLength("countryCode", countryCode, 2)
            if (partyId != null) validateLength("partyId", partyId, 3)
        }

        service
            .getEvse(locationId, evseUid, countryCode, partyId)
            ?.validate()
    }

    override suspend fun getConnector(
        locationId: CiString,
        evseUid: CiString,
        connectorId: CiString,
        countryCode: String?,
        partyId: String?
    ): OcpiResponseBody<Connector?> = OcpiResponseBody.of {
        validate {
            validateLength("locationId", locationId, 36)
            validateLength("evseUid", evseUid, 36)
            validateLength("connectorId", connectorId, 36)
            if (countryCode != null) validateLength("countryCode", countryCode, 2)
            if (partyId != null) validateLength("partyId", partyId, 3)
        }

        service
            .getConnector(locationId, evseUid, connectorId, countryCode, partyId)
            ?.validate()
    }
}
