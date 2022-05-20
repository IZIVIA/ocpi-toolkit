package ocpi.locations.validation

import common.OcpiResponseBody
import common.SearchResult
import common.validation.validate
import common.validation.validateDates
import common.validation.validateInt
import common.validation.validateLength
import ocpi.locations.LocationsCpoInterface
import ocpi.locations.domain.Connector
import ocpi.locations.domain.Evse
import ocpi.locations.domain.Location
import ocpi.locations.services.LocationsCpoService
import java.time.Instant

class LocationsCpoValidationService(
    private val service: LocationsCpoService
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

        service
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

        service
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

        service
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
            validateLength("connectorId", connectorId, 36)
        }

        service
            .getConnector(locationId, evseUid, connectorId)
            ?.validate()
    }
}
