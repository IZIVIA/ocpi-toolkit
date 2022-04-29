package ocpi.locations.services

import common.OcpiResponseBody
import common.validate
import ocpi.locations.LocationsEmspInterface
import ocpi.locations.domain.*
import ocpi.locations.repositories.LocationsEmspRepository
import ocpi.locations.validators.validate
import org.valiktor.ConstraintViolationException

class LocationsEmspService(
    private val repository: LocationsEmspRepository
) : LocationsEmspInterface {

    override fun getLocation(countryCode: String, partyId: String, locationId: String): OcpiResponseBody<Location?> =
        try {
            validate {
                // TODO
            }

            OcpiResponseBody.success(
                data = repository
                    .getLocation(countryCode = countryCode, partyId = partyId, locationId = locationId)
                    ?.validate()
            )
        } catch (e: ConstraintViolationException) {
            OcpiResponseBody.invalid(e.constraintViolations.toString())
        }


    override fun getEvse(
        countryCode: String,
        partyId: String,
        locationId: String,
        evseUid: String
    ): OcpiResponseBody<Evse?> = try {
        validate {
            // TODO
        }

        OcpiResponseBody.success(
            data = repository
                .getEvse(countryCode = countryCode, partyId = partyId, locationId = locationId, evseUid = evseUid)
                ?.validate()
        )
    } catch (e: ConstraintViolationException) {
        OcpiResponseBody.invalid(e.constraintViolations.toString())
    }

    override fun getConnector(
        countryCode: String,
        partyId: String,
        locationId: String,
        evseUid: String,
        connectorId: String
    ): OcpiResponseBody<Connector?> = try {
        validate {
            // TODO
        }

        OcpiResponseBody.success(
            data = repository
                .getConnector(
                    countryCode = countryCode,
                    partyId = partyId,
                    locationId = locationId,
                    evseUid = evseUid,
                    connectorId = connectorId
                )
                ?.validate()
        )
    } catch (e: ConstraintViolationException) {
        OcpiResponseBody.invalid(e.constraintViolations.toString())
    }

    override fun putLocation(
        countryCode: String,
        partyId: String,
        locationId: String,
        location: Location
    ): OcpiResponseBody<Location> = try {
        validate {
            // TODO
        }

        OcpiResponseBody.success(
            data = repository
                .putLocation(countryCode = countryCode, partyId = partyId, locationId = locationId, location = location)
                .validate()
        )
    } catch (e: ConstraintViolationException) {
        OcpiResponseBody.invalid(e.constraintViolations.toString())
    }

    override fun putEvse(
        countryCode: String,
        partyId: String,
        locationId: String,
        evseUid: String,
        evse: Evse
    ): OcpiResponseBody<Evse> = try {
        validate {
            // TODO
        }

        OcpiResponseBody.success(
            data = repository
                .putEvse(
                    countryCode = countryCode,
                    partyId = partyId,
                    locationId = locationId,
                    evseUid = evseUid,
                    evse = evse
                )
                .validate()
        )
    } catch (e: ConstraintViolationException) {
        OcpiResponseBody.invalid(e.constraintViolations.toString())
    }

    override fun putConnector(
        countryCode: String,
        partyId: String,
        locationId: String,
        evseUid: String,
        connectorId: String,
        connector: Connector
    ): OcpiResponseBody<Connector> = try {
        validate {
            // TODO
        }

        OcpiResponseBody.success(
            data = repository
                .putConnector(
                    countryCode = countryCode,
                    partyId = partyId,
                    locationId = locationId,
                    evseUid = evseUid,
                    connectorId = connectorId,
                    connector = connector
                )
                .validate()
        )
    } catch (e: ConstraintViolationException) {
        OcpiResponseBody.invalid(e.constraintViolations.toString())
    }

    override fun patchLocation(
        countryCode: String,
        partyId: String,
        locationId: String,
        location: LocationPartial
    ): OcpiResponseBody<Location?> = try {
        validate {
            // TODO
        }

        OcpiResponseBody.success(
            data = repository
                .patchLocation(
                    countryCode = countryCode,
                    partyId = partyId,
                    locationId = locationId,
                    location = location
                )
                ?.validate()
        )
    } catch (e: ConstraintViolationException) {
        OcpiResponseBody.invalid(e.constraintViolations.toString())
    }

    override fun patchEvse(
        countryCode: String,
        partyId: String,
        locationId: String,
        evseUid: String,
        evse: EvsePartial
    ): OcpiResponseBody<Evse?> = try {
        validate {
            // TODO
        }

        OcpiResponseBody.success(
            data = repository
                .patchEvse(
                    countryCode = countryCode,
                    partyId = partyId,
                    locationId = locationId,
                    evseUid = evseUid,
                    evse = evse
                )
                ?.validate()
        )
    } catch (e: ConstraintViolationException) {
        OcpiResponseBody.invalid(e.constraintViolations.toString())
    }

    override fun patchConnector(
        countryCode: String,
        partyId: String,
        locationId: String,
        evseUid: String,
        connectorId: String,
        connector: ConnectorPartial
    ): OcpiResponseBody<Connector?> = try {
        validate {
            // TODO
        }

        OcpiResponseBody.success(
            data = repository
                .patchConnector(
                    countryCode = countryCode,
                    partyId = partyId,
                    locationId = locationId,
                    evseUid = evseUid,
                    connectorId = connectorId,
                    connector = connector
                )
                ?.validate()
        )
    } catch (e: ConstraintViolationException) {
        OcpiResponseBody.invalid(e.constraintViolations.toString())
    }
}