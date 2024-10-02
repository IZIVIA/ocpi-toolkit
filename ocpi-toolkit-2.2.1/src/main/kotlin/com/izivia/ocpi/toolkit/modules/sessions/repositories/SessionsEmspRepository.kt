package com.izivia.ocpi.toolkit.modules.sessions.repositories

import com.izivia.ocpi.toolkit.common.CiString
import com.izivia.ocpi.toolkit.modules.sessions.domain.Session
import com.izivia.ocpi.toolkit.modules.sessions.domain.SessionPartial

/**
 * Typically implemented by market roles like: eMSP and SCSP.
 *
 * Sessions are Client Owned Objects, so the endpoints need to contain the required extra fields: {party_id} and {country_code}
 *
 * - GET: Retrieve a Session object from the eMSP’s system with Session.id equal to {session_id}.
 * - POST: n/a
 * - PUT: Send a new/updated Session object to the eMSP.
 * - PATCH: Update the Session object with Session.id equal to {session_id}.
 * - DELETE: n/a
 */
interface SessionsEmspRepository {

    /**
     * GET Method
     *
     * The CPO system might request the current version of a Session object from the eMSP’s system to, for example, validate the state,
     * or because the CPO has received an error during a PATCH operation.
     *
     * @param countryCode (max-length 2) Country code of the CPO performing the GET on the eMSP’s system.
     * @param partyId (max-length 3) Party ID (Provider ID) of the CPO performing the GET on the eMSP’s system.
     * @param sessionId (max-length 36) id of the Session object to get from the eMSP’s system.
     * @return Session Requested Session object.
     */
    suspend fun getSession(countryCode: CiString, partyId: CiString, sessionId: CiString): Session?

    /**
     * PUT Method
     *
     * Inform the eMSP’s system about a new/updated Session object in the CPO’s system.
     * When a PUT request is received for an existing Session object (the object is PUT to the same URL), The newly received Session
     * object SHALL replace the existing object.
     * Any charging_periods from the existing object SHALL be replaced by the charging_periods from the newly received
     * Session object. If the new Session object does not contain charging_periods (field is omitted or contains any empty list), the
     * charging_periods of the existing object SHALL be removed (replaced by the new empty list).
     *
     * @param countryCode (max-length 2) Country code of the CPO performing the GET on the eMSP’s system.
     * @param partyId (max-length 3) Party ID (Provider ID) of the CPO performing the GET on the eMSP’s system.
     * @param sessionId (max-length 36) id of the Session object to get from the eMSP’s system.
     * @param session New or updated Session object.
     * @return Session New or updated Session object.
     */
    suspend fun putSession(countryCode: CiString, partyId: CiString, sessionId: CiString, session: Session): Session?

    /**
     * PATCH Method
     *
     * Same as the PUT method, but only the fields/objects that need to be updated have to be present. Fields/objects which are not
     * specified are considered unchanged.
     * When a PATCH request contains the charging_periods field (inside a Session object), this SHALL be processed as a request to
     * add all the ChargingPeriod objects to the existing Session object. If the request charging_periods list is omitted (or contains an
     * empty list), no changes SHALL be made to the existing list of charging_periods.
     * If existing ChargingPeriod objects in a Session need to be replaced or removed, the Sender SHALL use the PUT method to replace
     * the entire Session object (including all the charging_periods).
     *
     * @param countryCode (max-length 2) Country code of the CPO performing the GET on the eMSP’s system.
     * @param partyId (max-length 3) Party ID (Provider ID) of the CPO performing the GET on the eMSP’s system.
     * @param sessionId (max-length 36) id of the Session object to get from the eMSP’s system.
     * @param session New or updated Session object.
     * @return Session New or updated Session object.
     */
    suspend fun patchSession(
        countryCode: CiString,
        partyId: CiString,
        sessionId: CiString,
        session: SessionPartial,
    ): Session?
}
