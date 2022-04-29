package ocpi.locations.services

import common.*
import ocpi.locations.LocationsCpoInterface
import ocpi.locations.domain.Connector
import ocpi.locations.domain.Evse
import ocpi.locations.domain.Location
import ocpi.locations.repositories.LocationsCpoRepository
import ocpi.locations.validators.validate
import java.time.Instant

class LocationsCpoService(
    private val repository: LocationsCpoRepository
) : LocationsCpoInterface {

    override fun getLocations(
        dateFrom: Instant?,
        dateTo: Instant?,
        offset: Int,
        limit: Int?
    ): OcpiResponseBody<SearchResult<Location>> = OcpiResponseBody.of {
        validate {
            if (dateFrom != null && dateTo != null) validateDates("dateFrom", dateFrom, "dateTo", dateTo)
            if (limit != null) validateInt("limit", limit, 0, null)
            validateInt("offset", offset, 0, null)
        }

        repository
            .getLocations(dateFrom, dateTo, offset, limit)
            .also { searchResult ->
                searchResult.list.forEach { location -> location.validate() }
            }
    }

    override fun getLocation(
        locationId: String
    ): OcpiResponseBody<Location?> = OcpiResponseBody.of {
        validate {
            validateLength("locationId", locationId, 39)
        }

        repository
            .getLocation(locationId)
            ?.validate()
    }

    override fun getEvse(
        locationId: String,
        evseUid: String
    ): OcpiResponseBody<Evse?> = OcpiResponseBody.of {
        validate {
            validateLength("locationId", locationId, 39)
            validateLength("evseUid", evseUid, 39)
        }

        repository
            .getEvse(locationId, evseUid)
            ?.validate()
    }

    override fun getConnector(
        locationId: String,
        evseUid: String,
        connectorId: String
    ): OcpiResponseBody<Connector?> = OcpiResponseBody.of {
        validate {
            validateLength("locationId", locationId, 39)
            validateLength("evseUid", evseUid, 39)
            validateLength("connectorId", connectorId, 39)
        }

        repository
            .getConnector(locationId, evseUid, connectorId)
            ?.validate()
    }
}