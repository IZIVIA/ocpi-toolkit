package com.izivia.ocpi.toolkit.modules.locations.services

import com.izivia.ocpi.toolkit.common.CiString
import com.izivia.ocpi.toolkit.common.OcpiResponseBody
import com.izivia.ocpi.toolkit.common.validation.validate
import com.izivia.ocpi.toolkit.common.validation.validateLength
import com.izivia.ocpi.toolkit.common.validation.validateSame
import com.izivia.ocpi.toolkit.modules.locations.LocationsEmspInterface
import com.izivia.ocpi.toolkit.modules.locations.domain.*
import com.izivia.ocpi.toolkit.modules.locations.repositories.LocationsEmspRepository

open class LocationsEmspService(
    private val service: LocationsEmspRepository,
) : LocationsEmspInterface {

    override suspend fun getLocation(
        countryCode: CiString,
        partyId: CiString,
        locationId: CiString,
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

    override suspend fun getEvse(
        countryCode: CiString,
        partyId: CiString,
        locationId: CiString,
        evseUid: CiString,
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

    override suspend fun getConnector(
        countryCode: CiString,
        partyId: CiString,
        locationId: CiString,
        evseUid: CiString,
        connectorId: CiString,
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
                connectorId = connectorId,
            )
            ?.validate()
    }

    override suspend fun putLocation(
        countryCode: CiString,
        partyId: CiString,
        locationId: CiString,
        location: Location,
    ): OcpiResponseBody<Location> = OcpiResponseBody.of {
        validate {
            validateLength("countryCode", countryCode, 2)
            validateLength("partyId", partyId, 3)
            validateLength("locationId", locationId, 36)
            validateSame("countryCode", countryCode, location.countryCode)
            validateSame("partyId", partyId, location.partyId)
            validateSame("locationId", locationId, location.id)
            location.validate()
        }

        service
            .putLocation(countryCode = countryCode, partyId = partyId, locationId = locationId, location = location)
            .validate()
    }

    override suspend fun putEvse(
        countryCode: CiString,
        partyId: CiString,
        locationId: CiString,
        evseUid: CiString,
        evse: Evse,
    ): OcpiResponseBody<Evse> = OcpiResponseBody.of {
        validate {
            validateLength("countryCode", countryCode, 2)
            validateLength("partyId", partyId, 3)
            validateLength("locationId", locationId, 36)
            validateLength("evseUid", evseUid, 36)
            validateSame("evseUid", evseUid, evse.uid)
            evse.validate()
        }

        service
            .putEvse(
                countryCode = countryCode,
                partyId = partyId,
                locationId = locationId,
                evseUid = evseUid,
                evse = evse,
            )
            .validate()
    }

    override suspend fun putConnector(
        countryCode: CiString,
        partyId: CiString,
        locationId: CiString,
        evseUid: CiString,
        connectorId: CiString,
        connector: Connector,
    ): OcpiResponseBody<Connector> = OcpiResponseBody.of {
        validate {
            validateLength("countryCode", countryCode, 2)
            validateLength("partyId", partyId, 3)
            validateLength("locationId", locationId, 36)
            validateLength("evseUid", evseUid, 36)
            validateLength("connectorId", connectorId, 36)
            validateSame("connectorId", connectorId, connector.id)
            connector.validate()
        }

        service
            .putConnector(
                countryCode = countryCode,
                partyId = partyId,
                locationId = locationId,
                evseUid = evseUid,
                connectorId = connectorId,
                connector = connector,
            )
            .validate()
    }

    override suspend fun patchLocation(
        countryCode: CiString,
        partyId: CiString,
        locationId: CiString,
        location: LocationPartial,
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
                location = location,
            )
            ?.validate()
    }

    override suspend fun patchEvse(
        countryCode: CiString,
        partyId: CiString,
        locationId: CiString,
        evseUid: CiString,
        evse: EvsePartial,
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
                evse = evse,
            )
            ?.validate()
    }

    override suspend fun patchConnector(
        countryCode: CiString,
        partyId: CiString,
        locationId: CiString,
        evseUid: CiString,
        connectorId: CiString,
        connector: ConnectorPartial,
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
                connector = connector,
            )
            ?.validate()
    }
}
