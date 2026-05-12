package com.izivia.ocpi.toolkit211.modules.locations.repositories

import com.izivia.ocpi.toolkit211.modules.locations.domain.*

interface LocationsEmspRepository {

    suspend fun getLocation(countryCode: String, partyId: String, locationId: String): Location?

    suspend fun getEvse(countryCode: String, partyId: String, locationId: String, evseUid: String): Evse?

    suspend fun getConnector(
        countryCode: String,
        partyId: String,
        locationId: String,
        evseUid: String,
        connectorId: String,
    ): Connector?

    suspend fun putLocation(
        countryCode: String,
        partyId: String,
        locationId: String,
        location: Location,
    ): Location

    suspend fun putEvse(
        countryCode: String,
        partyId: String,
        locationId: String,
        evseUid: String,
        evse: Evse,
    ): Evse

    suspend fun putConnector(
        countryCode: String,
        partyId: String,
        locationId: String,
        evseUid: String,
        connectorId: String,
        connector: Connector,
    ): Connector

    suspend fun patchLocation(
        countryCode: String,
        partyId: String,
        locationId: String,
        location: LocationPartial,
    ): Location?

    suspend fun patchEvse(
        countryCode: String,
        partyId: String,
        locationId: String,
        evseUid: String,
        evse: EvsePartial,
    ): Evse?

    suspend fun patchConnector(
        countryCode: String,
        partyId: String,
        locationId: String,
        evseUid: String,
        connectorId: String,
        connector: ConnectorPartial,
    ): Connector?
}
