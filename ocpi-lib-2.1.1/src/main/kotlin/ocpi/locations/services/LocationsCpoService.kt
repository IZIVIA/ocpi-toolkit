package ocpi.locations.services

import common.*
import ocpi.locations.LocationsCpoInterface
import ocpi.locations.domain.Connector
import ocpi.locations.domain.Evse
import ocpi.locations.domain.Location
import ocpi.locations.repositories.LocationsCpoRepository
import ocpi.locations.validators.validate
import org.valiktor.ConstraintViolationException
import java.time.Instant

class LocationsCpoService(
    private val repository: LocationsCpoRepository
) : LocationsCpoInterface {

    override fun getLocations(
        dateFrom: Instant?,
        dateTo: Instant?,
        offset: Int,
        limit: Int?
    ): OcpiResponseBody<SearchResult<Location>> {
        return try {
            validate {
                if (dateFrom != null && dateTo != null) validateDates("dateFrom", dateFrom, "dateTo", dateTo)
                if (limit != null) validateInt("limit", limit, 0, null)
                validateInt("offset", offset, 0, null)
            }

            val locations = repository
                .getLocations(dateFrom, dateTo, offset, limit)
                .also { searchResult ->
                    searchResult.list.forEach { location -> location.validate() }
                }

            OcpiResponseBody(
                data = locations,
                status_code = OcpiStatusCode.SUCCESS.code,
                status_message = null,
                timestamp = Instant.now()
            )
        } catch (e: ConstraintViolationException) {
            OcpiResponseBody(
                data = null,
                status_code = OcpiStatusCode.INVALID_OR_MISSING_PARAMETERS.code,
                status_message = e.constraintViolations.toString(),
                timestamp = Instant.now()
            )
        }
    }

    override fun getLocation(locationId: String): OcpiResponseBody<Location?> {
        return try {
            validate {
                validateLength("locationId", locationId, 39)
            }

            val location = repository
                .getLocation(locationId)
                ?.validate()

            OcpiResponseBody(
                data = location,
                status_code = OcpiStatusCode.SUCCESS.code,
                status_message = null,
                timestamp = Instant.now()
            )
        } catch (e: ConstraintViolationException) {
            OcpiResponseBody(
                data = null,
                status_code = OcpiStatusCode.INVALID_OR_MISSING_PARAMETERS.code,
                status_message = e.constraintViolations.toString(),
                timestamp = Instant.now()
            )
        }
    }

    override fun getEvse(locationId: String, evseUid: String): OcpiResponseBody<Evse?> {
        return try {
            validate {
                validateLength("locationId", locationId, 39)
                validateLength("evseUid", evseUid, 39)
            }

            val evse = repository
                .getEvse(locationId, evseUid)
                ?.validate()

            OcpiResponseBody(
                data = evse,
                status_code = OcpiStatusCode.SUCCESS.code,
                status_message = null,
                timestamp = Instant.now()
            )
        } catch (e: ConstraintViolationException) {
            OcpiResponseBody(
                data = null,
                status_code = OcpiStatusCode.INVALID_OR_MISSING_PARAMETERS.code,
                status_message = e.constraintViolations.toString(),
                timestamp = Instant.now()
            )
        }
    }

    override fun getConnector(locationId: String, evseUid: String, connectorId: String): OcpiResponseBody<Connector?> {
        return try {
            validate {
                validateLength("locationId", locationId, 39)
                validateLength("evseUid", evseUid, 39)
                validateLength("connectorId", connectorId, 39)
            }

            val connector = repository
                .getConnector(locationId, evseUid, connectorId)
                ?.validate()

            OcpiResponseBody(
                data = connector,
                status_code = OcpiStatusCode.SUCCESS.code,
                status_message = null,
                timestamp = Instant.now()
            )
        } catch (e: ConstraintViolationException) {
            OcpiResponseBody(
                data = null,
                status_code = OcpiStatusCode.INVALID_OR_MISSING_PARAMETERS.code,
                status_message = e.constraintViolations.toString(),
                timestamp = Instant.now()
            )
        }
    }
}