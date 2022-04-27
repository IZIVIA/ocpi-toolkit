package ocpi.locations.services

import common.OcpiResponseBody
import common.SearchResult
import ocpi.locations.LocationsCpoInterface
import ocpi.locations.domain.Connector
import ocpi.locations.domain.Evse
import ocpi.locations.domain.Location
import ocpi.locations.repositories.LocationsRepository
import ocpi.locations.validators.validate
import org.valiktor.*
import org.valiktor.constraints.Greater
import org.valiktor.constraints.Less
import java.time.Instant

class LocationsCpoService(
    private val repository: LocationsRepository
) : LocationsCpoInterface {

    override fun getLocations(
        dateFrom: Instant?,
        dateTo: Instant?,
        offset: Int,
        limit: Int?
    ): OcpiResponseBody<SearchResult<Location>> {
        return try {
            val violations = mutableSetOf<ConstraintViolation>()

            if (dateFrom != null && dateTo != null && dateFrom.isAfter(dateTo)) {
                violations.add(
                    DefaultConstraintViolation(
                        property = "dateFrom",
                        constraint = Greater(dateTo)
                    )
                )
            }

            if (offset < 0) {
                violations.add(
                    DefaultConstraintViolation(
                        property = "offset",
                        constraint = Less(0)
                    )
                )
            }

            if (limit != null && limit < 0) {
                violations.add(
                    DefaultConstraintViolation(
                        property = "limit",
                        constraint = Less(0)
                    )
                )
            }

            if (violations.isNotEmpty()) {
                throw ConstraintViolationException(violations)
            }

            val locations = repository
                .getLocations(dateFrom, dateTo, offset, limit)
                .also { searchResult ->
                    searchResult.list.forEach { location -> location.validate() }
                }

            OcpiResponseBody(
                data = locations,
                status_code = 1000,
                status_message = null,
                timestamp = Instant.now()
            )
        } catch (e: ConstraintViolationException) {
            OcpiResponseBody(
                data = null,
                status_code = 2001,
                status_message = e.constraintViolations.toString(),
                timestamp = Instant.now()
            )
        }
    }

    override fun getLocation(locationId: String): OcpiResponseBody<Location?> {
        return try {
            val violations = mutableSetOf<ConstraintViolation>()

            if (locationId.length > 39) {
                violations.add(
                    DefaultConstraintViolation(
                        property = "locationId size",
                        constraint = Greater(39)
                    )
                )
            }

            if (violations.isNotEmpty()) {
                throw ConstraintViolationException(violations)
            }

            val location = repository
                .getLocation(locationId)
                ?.validate()

            OcpiResponseBody(
                data = location,
                status_code = 1000,
                status_message = null,
                timestamp = Instant.now()
            )
        } catch (e: ConstraintViolationException) {
            OcpiResponseBody(
                data = null,
                status_code = 2001,
                status_message = e.constraintViolations.toString(),
                timestamp = Instant.now()
            )
        }
    }

    override fun getEvse(locationId: String, evseUid: String): OcpiResponseBody<Evse?> {
        return try {
            val violations = mutableSetOf<ConstraintViolation>()

            if (locationId.length > 39) {
                violations.add(
                    DefaultConstraintViolation(
                        property = "locationId size",
                        constraint = Greater(39)
                    )
                )
            }

            if (evseUid.length > 39) {
                violations.add(
                    DefaultConstraintViolation(
                        property = "evseUid size",
                        constraint = Greater(39)
                    )
                )
            }

            if (violations.isNotEmpty()) {
                throw ConstraintViolationException(violations)
            }

            val evse = repository
                .getEvse(locationId, evseUid)
                ?.validate()

            OcpiResponseBody(
                data = evse,
                status_code = 1000,
                status_message = null,
                timestamp = Instant.now()
            )
        } catch (e: ConstraintViolationException) {
            OcpiResponseBody(
                data = null,
                status_code = 2001,
                status_message = e.constraintViolations.toString(),
                timestamp = Instant.now()
            )
        }
    }

    override fun getConnector(locationId: String, evseUid: String, connectorId: String): OcpiResponseBody<Connector?> {
        return try {
            val violations = mutableSetOf<ConstraintViolation>()

            if (locationId.length > 39) {
                violations.add(
                    DefaultConstraintViolation(
                        property = "locationId size",
                        constraint = Greater(39)
                    )
                )
            }

            if (evseUid.length > 39) {
                violations.add(
                    DefaultConstraintViolation(
                        property = "evseUid size",
                        constraint = Greater(39)
                    )
                )
            }

            if (connectorId.length > 39) {
                violations.add(
                    DefaultConstraintViolation(
                        property = "connectorId size",
                        constraint = Greater(39)
                    )
                )
            }

            if (violations.isNotEmpty()) {
                throw ConstraintViolationException(violations)
            }

            val connector = repository
                .getConnector(locationId, evseUid, connectorId)
                ?.validate()

            OcpiResponseBody(
                data = connector,
                status_code = 1000,
                status_message = null,
                timestamp = Instant.now()
            )
        } catch (e: ConstraintViolationException) {
            OcpiResponseBody(
                data = null,
                status_code = 2001,
                status_message = e.constraintViolations.toString(),
                timestamp = Instant.now()
            )
        }
    }
}