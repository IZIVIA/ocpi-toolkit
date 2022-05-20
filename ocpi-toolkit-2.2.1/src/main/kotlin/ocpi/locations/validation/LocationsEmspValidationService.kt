package ocpi.locations.validation

import common.CiString
import common.OcpiResponseBody
import common.validation.validate
import common.validation.validateLength
import ocpi.locations.LocationsEmspInterface
import ocpi.locations.domain.*
import ocpi.locations.services.LocationsEmspService

class LocationsEmspValidationService(
    private val service: LocationsEmspService
) : LocationsEmspInterface {

    override fun getLocation(
        countryCode: CiString,
        partyId: CiString,
        locationId: CiString
    ): OcpiResponseBody<Location?> = OcpiResponseBody.of {
        validate {
            validateLength("countryCode", countryCode, 2)
            validateLength("partyId", partyId, 3)
            validateLength("locationId", locationId, 36)
        }

        service
            .getLocation(countryCode = countryCode, partyId = partyId, locationId = locationId)
            ?.validate()
    }


    override fun getEvse(
        countryCode: CiString,
        partyId: CiString,
        locationId: CiString,
        evseUid: CiString
    ): OcpiResponseBody<Evse?> = OcpiResponseBody.of {
        validate {
            validateLength("countryCode", countryCode, 2)
            validateLength("partyId", partyId, 3)
            validateLength("locationId", locationId, 36)
            validateLength("evseUid", evseUid, 36)
        }

        service
            .getEvse(countryCode = countryCode, partyId = partyId, locationId = locationId, evseUid = evseUid)
            ?.validate()
    }

    override fun getConnector(
        countryCode: CiString,
        partyId: CiString,
        locationId: CiString,
        evseUid: CiString,
        connectorId: CiString
    ): OcpiResponseBody<Connector?> = OcpiResponseBody.of {
        validate {
            validateLength("countryCode", countryCode, 2)
            validateLength("partyId", partyId, 3)
            validateLength("locationId", locationId, 36)
            validateLength("evseUid", evseUid, 36)
            validateLength("connectorId", connectorId, 36)
        }

        service
            .getConnector(
                countryCode = countryCode,
                partyId = partyId,
                locationId = locationId,
                evseUid = evseUid,
                connectorId = connectorId
            )
            ?.validate()
    }

    override fun putLocation(
        countryCode: CiString,
        partyId: CiString,
        locationId: CiString,
        location: Location
    ): OcpiResponseBody<Location> = OcpiResponseBody.of {
        validate {
            validateLength("countryCode", countryCode, 2)
            validateLength("partyId", partyId, 3)
            validateLength("locationId", locationId, 36)
            location.validate()
        }

        service
            .putLocation(countryCode = countryCode, partyId = partyId, locationId = locationId, location = location)
            .validate()
    }

    override fun putEvse(
        countryCode: CiString,
        partyId: CiString,
        locationId: CiString,
        evseUid: CiString,
        evse: Evse
    ): OcpiResponseBody<Evse> = OcpiResponseBody.of {
        validate {
            validateLength("countryCode", countryCode, 2)
            validateLength("partyId", partyId, 3)
            validateLength("locationId", locationId, 36)
            validateLength("evseUid", evseUid, 36)
            evse.validate()
        }

        service
            .putEvse(
                countryCode = countryCode,
                partyId = partyId,
                locationId = locationId,
                evseUid = evseUid,
                evse = evse
            )
            .validate()
    }

    override fun putConnector(
        countryCode: CiString,
        partyId: CiString,
        locationId: CiString,
        evseUid: CiString,
        connectorId: CiString,
        connector: Connector
    ): OcpiResponseBody<Connector> = OcpiResponseBody.of {
        validate {
            validateLength("countryCode", countryCode, 2)
            validateLength("partyId", partyId, 3)
            validateLength("locationId", locationId, 36)
            validateLength("evseUid", evseUid, 36)
            validateLength("connectorId", connectorId, 36)
            connector.validate()
        }

        service
            .putConnector(
                countryCode = countryCode,
                partyId = partyId,
                locationId = locationId,
                evseUid = evseUid,
                connectorId = connectorId,
                connector = connector
            )
            .validate()
    }

    override fun patchLocation(
        countryCode: CiString,
        partyId: CiString,
        locationId: CiString,
        location: LocationPartial
    ): OcpiResponseBody<Location?> = OcpiResponseBody.of {
        validate {
            validateLength("countryCode", countryCode, 2)
            validateLength("partyId", partyId, 3)
            validateLength("locationId", locationId, 36)
            location.validate()
        }

        service
            .patchLocation(
                countryCode = countryCode,
                partyId = partyId,
                locationId = locationId,
                location = location
            )
            ?.validate()
    }

    override fun patchEvse(
        countryCode: CiString,
        partyId: CiString,
        locationId: CiString,
        evseUid: CiString,
        evse: EvsePartial
    ): OcpiResponseBody<Evse?> = OcpiResponseBody.of {
        validate {
            validateLength("countryCode", countryCode, 2)
            validateLength("partyId", partyId, 3)
            validateLength("locationId", locationId, 36)
            validateLength("evseUid", evseUid, 36)
            evse.validate()
        }

        service
            .patchEvse(
                countryCode = countryCode,
                partyId = partyId,
                locationId = locationId,
                evseUid = evseUid,
                evse = evse
            )
            ?.validate()
    }

    override fun patchConnector(
        countryCode: CiString,
        partyId: CiString,
        locationId: CiString,
        evseUid: CiString,
        connectorId: CiString,
        connector: ConnectorPartial
    ): OcpiResponseBody<Connector?> = OcpiResponseBody.of {
        validate {
            validateLength("countryCode", countryCode, 2)
            validateLength("partyId", partyId, 3)
            validateLength("locationId", locationId, 36)
            validateLength("evseUid", evseUid, 36)
            validateLength("connectorId", connectorId, 36)
            connector.validate()
        }

        service
            .patchConnector(
                countryCode = countryCode,
                partyId = partyId,
                locationId = locationId,
                evseUid = evseUid,
                connectorId = connectorId,
                connector = connector
            )
            ?.validate()
    }
}
