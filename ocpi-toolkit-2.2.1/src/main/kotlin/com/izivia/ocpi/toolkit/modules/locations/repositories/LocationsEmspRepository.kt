package com.izivia.ocpi.toolkit.modules.locations.repositories

import com.izivia.ocpi.toolkit.modules.locations.domain.*

/**
 * Locations is a client owned object, so the end-points need to contain the required extra fields: {party_id} and
 * {country_code}. Example endpoint structures:
 * - /ocpi/emsp/2.1.1/locations/{country_code}/{party_id}/{location_id}
 * - /ocpi/emsp/2.1.1/locations/{country_code}/{party_id}/{location_id}/{evse_uid}
 * - /ocpi/emsp/2.1.1/locations/{country_code}/{party_id}/{location_id}/{evse_uid}/{connector_id}
 *
 * Method: Description
 * - GET: Retrieve a Location as it is stored in the eMSP system.
 * - POST: n/a (use PUT)
 * - PUT: Push new/updated Location, EVSE and/or Connectors to the eMSP
 * - PATCH: Notify the eMSP of partial updates to a Location, EVSEs or Connector (such as the status).
 * - DELETE: n/a (use PATCH)
 */
interface LocationsEmspRepository {
    /**
     * If the CPO wants to check the status of a Location, EVSE or Connector object in the eMSP system, it might GET the
     * object from the eMSP system for validation purposes. The CPO is the owner of the objects, so it would be
     * illogical if the eMSP system had a different status or was missing an object. If a discrepancy is found, the CPO
     * might push an update to the eMSP via a PUT or PATCH call.
     *
     * @param countryCode (max-length=2) Country code of the CPO requesting this PUT to the eMSP system.
     * @param partyId (max-length=3) Party ID (Provider ID) of the CPO requesting this PUT to the eMSP system.
     * @param locationId (max-length=39) Location.id of the Location object to retrieve.
     */
    suspend fun getLocation(countryCode: String, partyId: String, locationId: String): Location?

    /**
     * If the CPO wants to check the status of a Location, EVSE or Connector object in the eMSP system, it might GET the
     * object from the eMSP system for validation purposes. The CPO is the owner of the objects, so it would be
     * illogical if the eMSP system had a different status or was missing an object. If a discrepancy is found, the CPO
     * might push an update to the eMSP via a PUT or PATCH call.
     *
     * @param countryCode (max-length=2) Country code of the CPO requesting this PUT to the eMSP system.
     * @param partyId (max-length=3) Party ID (Provider ID) of the CPO requesting this PUT to the eMSP system.
     * @param locationId (max-length=39) Location.id of the Location object to retrieve.
     * @param evseUid (max-length=39) Evse.uid, required when requesting an EVSE or Connector object.
     */
    suspend fun getEvse(countryCode: String, partyId: String, locationId: String, evseUid: String): Evse?

    /**
     * If the CPO wants to check the status of a Location, EVSE or Connector object in the eMSP system, it might GET the
     * object from the eMSP system for validation purposes. The CPO is the owner of the objects, so it would be
     * illogical if the eMSP system had a different status or was missing an object. If a discrepancy is found, the CPO
     * might push an update to the eMSP via a PUT or PATCH call.
     *
     * @param countryCode (max-length=2) Country code of the CPO requesting this PUT to the eMSP system.
     * @param partyId (max-length=3) Party ID (Provider ID) of the CPO requesting this PUT to the eMSP system.
     * @param locationId (max-length=39) Location.id of the Location object to retrieve.
     * @param evseUid (max-length=39) Evse.uid, required when requesting an EVSE or Connector object.
     * @param connectorId (max-length=36) Connector.id, required when requesting a Connector object.
     */
    suspend fun getConnector(
        countryCode: String,
        partyId: String,
        locationId: String,
        evseUid: String,
        connectorId: String,
    ): Connector?

    /**
     * The CPO pushes available Location/EVSE or Connector objects to the eMSP. PUT is used to send new Location objects
     * to the eMSP, or to replace existing Locations.
     *
     * @param countryCode (max-length=2) Country code of the CPO requesting this PUT to the eMSP system.
     * @param partyId (max-length=3) Party ID (Provider ID) of the CPO requesting this PUT to the eMSP system.
     * @param locationId (max-length=39) Location.id of the new Location object, or the Location of which an EVSE or
     * Location object is send
     */
    suspend fun putLocation(
        countryCode: String,
        partyId: String,
        locationId: String,
        location: Location,
    ): Location

    /**
     * The CPO pushes available Location/EVSE or Connector objects to the eMSP. PUT is used to send new Location objects
     * to the eMSP, or to replace existing Locations.
     *
     * @param countryCode (max-length=2) Country code of the CPO requesting this PUT to the eMSP system.
     * @param partyId (max-length=3) Party ID (Provider ID) of the CPO requesting this PUT to the eMSP system.
     * @param locationId (max-length=39) Location.id of the new Location object, or the Location of which an EVSE or
     * Location object is send
     * @param evseUid (max-length=39) Evse.uid, required when an EVSE or Connector object is send/replaced.
     */
    suspend fun putEvse(
        countryCode: String,
        partyId: String,
        locationId: String,
        evseUid: String,
        evse: Evse,
    ): Evse

    /**
     * The CPO pushes available Location/EVSE or Connector objects to the eMSP. PUT is used to send new Location objects
     * to the eMSP, or to replace existing Locations.
     *
     * @param countryCode (max-length=2) Country code of the CPO requesting this PUT to the eMSP system.
     * @param partyId (max-length=3) Party ID (Provider ID) of the CPO requesting this PUT to the eMSP system.
     * @param locationId (max-length=39) Location.id of the new Location object, or the Location of which an EVSE or
     * Location object is send
     * @param evseUid (max-length=39) Evse.uid, required when an EVSE or Connector object is send/replaced.
     * @param connectorId (max-length=36) Connector.id, required when a Connector object is send/replaced.
     */
    suspend fun putConnector(
        countryCode: String,
        partyId: String,
        locationId: String,
        evseUid: String,
        connectorId: String,
        connector: Connector,
    ): Connector

    /**
     * Same as the PUT method, but only the fields/objects that have to be updated have to be present, other
     * fields/objects that are not specified are considered unchanged.
     *
     * @param countryCode (max-length=2) Country code of the CPO requesting this PUT to the eMSP system.
     * @param partyId (max-length=3) Party ID (Provider ID) of the CPO requesting this PUT to the eMSP system.
     * @param locationId (max-length=39) Location.id of the new Location object
     */
    suspend fun patchLocation(
        countryCode: String,
        partyId: String,
        locationId: String,
        location: LocationPartial,
    ): Location?

    /**
     * Same as the PUT method, but only the fields/objects that have to be updated have to be present, other
     * fields/objects that are not specified are considered unchanged.
     *
     * @param countryCode (max-length=2) Country code of the CPO requesting this PUT to the eMSP system.
     * @param partyId (max-length=3) Party ID (Provider ID) of the CPO requesting this PUT to the eMSP system.
     * @param locationId (max-length=39) Location.id of the Location of which an EVSE or Location object is send
     * @param evseUid (max-length=39) Evse.uid, required when an EVSE or Connector object is send/replaced.
     */
    suspend fun patchEvse(
        countryCode: String,
        partyId: String,
        locationId: String,
        evseUid: String,
        evse: EvsePartial,
    ): Evse?

    /**
     * Same as the PUT method, but only the fields/objects that have to be updated have to be present, other
     * fields/objects that are not specified are considered unchanged.
     *
     * @param countryCode (max-length=2) Country code of the CPO requesting this PUT to the eMSP system.
     * @param partyId (max-length=3) Party ID (Provider ID) of the CPO requesting this PUT to the eMSP system.
     * @param locationId (max-length=39) Location.id of the Location of which an EVSE or Location object is send
     * @param evseUid (max-length=39) Evse.uid, required when an EVSE or Connector object is send/replaced.
     * @param connectorId (max-length=36) Connector.id, required when a Connector object is send/replaced.
     */
    suspend fun patchConnector(
        countryCode: String,
        partyId: String,
        locationId: String,
        evseUid: String,
        connectorId: String,
        connector: ConnectorPartial,
    ): Connector?
}
