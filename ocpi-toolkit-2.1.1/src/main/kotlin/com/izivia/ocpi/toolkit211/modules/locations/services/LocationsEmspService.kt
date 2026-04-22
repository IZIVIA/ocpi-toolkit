package com.izivia.ocpi.toolkit211.modules.locations.services

import com.izivia.ocpi.toolkit211.common.CiString
import com.izivia.ocpi.toolkit211.modules.locations.LocationsEmspInterface
import com.izivia.ocpi.toolkit211.modules.locations.domain.*
import com.izivia.ocpi.toolkit211.modules.locations.repositories.LocationsEmspRepository

open class LocationsEmspService(
    private val repository: LocationsEmspRepository,
) : LocationsEmspInterface {

    override suspend fun getLocation(
        countryCode: CiString,
        partyId: CiString,
        locationId: CiString,
    ): Location? = repository.getLocation(countryCode = countryCode, partyId = partyId, locationId = locationId)

    override suspend fun getEvse(
        countryCode: CiString,
        partyId: CiString,
        locationId: CiString,
        evseUid: CiString,
    ): Evse? = repository.getEvse(
        countryCode = countryCode,
        partyId = partyId,
        locationId = locationId,
        evseUid = evseUid,
    )

    override suspend fun getConnector(
        countryCode: CiString,
        partyId: CiString,
        locationId: CiString,
        evseUid: CiString,
        connectorId: CiString,
    ): Connector? = repository.getConnector(
        countryCode = countryCode,
        partyId = partyId,
        locationId = locationId,
        evseUid = evseUid,
        connectorId = connectorId,
    )

    override suspend fun putLocation(
        countryCode: CiString,
        partyId: CiString,
        locationId: CiString,
        location: Location,
    ): LocationPartial = repository.putLocation(
        countryCode = countryCode,
        partyId = partyId,
        locationId = locationId,
        location = location,
    ).toPartial()

    override suspend fun putEvse(
        countryCode: CiString,
        partyId: CiString,
        locationId: CiString,
        evseUid: CiString,
        evse: Evse,
    ): EvsePartial = repository.putEvse(
        countryCode = countryCode,
        partyId = partyId,
        locationId = locationId,
        evseUid = evseUid,
        evse = evse,
    ).toPartial()

    override suspend fun putConnector(
        countryCode: CiString,
        partyId: CiString,
        locationId: CiString,
        evseUid: CiString,
        connectorId: CiString,
        connector: Connector,
    ): ConnectorPartial = repository.putConnector(
        countryCode = countryCode,
        partyId = partyId,
        locationId = locationId,
        evseUid = evseUid,
        connectorId = connectorId,
        connector = connector,
    ).toPartial()

    override suspend fun patchLocation(
        countryCode: CiString,
        partyId: CiString,
        locationId: CiString,
        location: LocationPartial,
    ): LocationPartial? = repository.patchLocation(
        countryCode = countryCode,
        partyId = partyId,
        locationId = locationId,
        location = location,
    )?.toPartial()

    override suspend fun patchEvse(
        countryCode: CiString,
        partyId: CiString,
        locationId: CiString,
        evseUid: CiString,
        evse: EvsePartial,
    ): EvsePartial? = repository.patchEvse(
        countryCode = countryCode,
        partyId = partyId,
        locationId = locationId,
        evseUid = evseUid,
        evse = evse,
    )?.toPartial()

    override suspend fun patchConnector(
        countryCode: CiString,
        partyId: CiString,
        locationId: CiString,
        evseUid: CiString,
        connectorId: CiString,
        connector: ConnectorPartial,
    ): ConnectorPartial? = repository.patchConnector(
        countryCode = countryCode,
        partyId = partyId,
        locationId = locationId,
        evseUid = evseUid,
        connectorId = connectorId,
        connector = connector,
    )?.toPartial()
}
