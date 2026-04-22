package com.izivia.ocpi.toolkit211.modules.locations.services

import com.izivia.ocpi.toolkit211.common.CiString
import com.izivia.ocpi.toolkit211.common.validation.validate
import com.izivia.ocpi.toolkit211.common.validation.validateLength
import com.izivia.ocpi.toolkit211.common.validation.validateSame
import com.izivia.ocpi.toolkit211.modules.locations.LocationsEmspInterface
import com.izivia.ocpi.toolkit211.modules.locations.domain.*

open class LocationsEmspValidator(
    private val service: LocationsEmspInterface,
) : LocationsEmspInterface {

    override suspend fun getLocation(
        countryCode: CiString,
        partyId: CiString,
        locationId: CiString,
    ): Location? {
        validate {
            validateLength("countryCode", countryCode, 2)
            validateLength("partyId", partyId, 3)
            validateLength("locationId", locationId, 39)
        }

        return service
            .getLocation(countryCode = countryCode, partyId = partyId, locationId = locationId)
            ?.validate()
    }

    override suspend fun getEvse(
        countryCode: CiString,
        partyId: CiString,
        locationId: CiString,
        evseUid: CiString,
    ): Evse? {
        validate {
            validateLength("countryCode", countryCode, 2)
            validateLength("partyId", partyId, 3)
            validateLength("locationId", locationId, 39)
            validateLength("evseUid", evseUid, 39)
        }

        return service
            .getEvse(countryCode = countryCode, partyId = partyId, locationId = locationId, evseUid = evseUid)
            ?.validate()
    }

    override suspend fun getConnector(
        countryCode: CiString,
        partyId: CiString,
        locationId: CiString,
        evseUid: CiString,
        connectorId: CiString,
    ): Connector? {
        validate {
            validateLength("countryCode", countryCode, 2)
            validateLength("partyId", partyId, 3)
            validateLength("locationId", locationId, 39)
            validateLength("evseUid", evseUid, 39)
            validateLength("connectorId", connectorId, 36)
        }

        return service
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
    ): LocationPartial {
        validate {
            validateLength("countryCode", countryCode, 2)
            validateLength("partyId", partyId, 3)
            validateLength("locationId", locationId, 39)
            validateSame("locationId", locationId, location.id)
            location.validate()
        }

        return service
            .putLocation(countryCode = countryCode, partyId = partyId, locationId = locationId, location = location)
            .validate()
    }

    override suspend fun putEvse(
        countryCode: CiString,
        partyId: CiString,
        locationId: CiString,
        evseUid: CiString,
        evse: Evse,
    ): EvsePartial {
        validate {
            validateLength("countryCode", countryCode, 2)
            validateLength("partyId", partyId, 3)
            validateLength("locationId", locationId, 39)
            validateLength("evseUid", evseUid, 39)
            validateSame("evseUid", evseUid, evse.uid)
            evse.validate()
        }

        return service
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
    ): ConnectorPartial {
        validate {
            validateLength("countryCode", countryCode, 2)
            validateLength("partyId", partyId, 3)
            validateLength("locationId", locationId, 39)
            validateLength("evseUid", evseUid, 39)
            validateLength("connectorId", connectorId, 36)
            validateSame("connectorId", connectorId, connector.id)
            connector.validate()
        }

        return service
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
    ): LocationPartial? {
        validate {
            validateLength("countryCode", countryCode, 2)
            validateLength("partyId", partyId, 3)
            validateLength("locationId", locationId, 39)
            location.validate()
        }

        return service
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
    ): EvsePartial? {
        validate {
            validateLength("countryCode", countryCode, 2)
            validateLength("partyId", partyId, 3)
            validateLength("locationId", locationId, 39)
            validateLength("evseUid", evseUid, 39)
            evse.validate()
        }

        return service
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
    ): ConnectorPartial? {
        validate {
            validateLength("countryCode", countryCode, 2)
            validateLength("partyId", partyId, 3)
            validateLength("locationId", locationId, 39)
            validateLength("evseUid", evseUid, 39)
            validateLength("connectorId", connectorId, 36)
            connector.validate()
        }

        return service
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
