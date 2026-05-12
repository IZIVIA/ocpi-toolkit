package com.izivia.ocpi.toolkit211.modules.sessions

import com.izivia.ocpi.toolkit211.common.CiString
import com.izivia.ocpi.toolkit211.modules.sessions.domain.Session
import com.izivia.ocpi.toolkit211.modules.sessions.domain.SessionPartial

/**
 * Typically implemented by market roles like: eMSP.
 *
 * Sessions are Client Owned Objects, so the endpoints need to contain the required extra fields: {party_id} and
 * {country_code}.
 *
 * - GET: Retrieve a Session object from the eMSP's system with Session.id equal to {session_id}.
 * - PUT: Send a new/updated Session object to the eMSP.
 * - PATCH: Update the Session object with Session.id equal to {session_id}.
 */
interface SessionsEmspInterface {

    /**
     * GET Method
     *
     * @param countryCode (max-length 2) Country code of the CPO performing the GET on the eMSP's system.
     * @param partyId (max-length 3) Party ID (Provider ID) of the CPO performing the GET on the eMSP's system.
     * @param sessionId (max-length 36) id of the Session object to get from the eMSP's system.
     * @return Session? Requested Session object.
     */
    suspend fun getSession(countryCode: CiString, partyId: CiString, sessionId: CiString): Session?

    /**
     * PUT Method
     *
     * @param countryCode (max-length 2) Country code of the CPO performing the PUT on the eMSP's system.
     * @param partyId (max-length 3) Party ID (Provider ID) of the CPO performing the PUT on the eMSP's system.
     * @param sessionId (max-length 36) id of the Session object to put on the eMSP's system.
     * @param session New or updated Session object.
     * @return SessionPartial The stored Session object.
     */
    suspend fun putSession(
        countryCode: CiString,
        partyId: CiString,
        sessionId: CiString,
        session: Session,
    ): SessionPartial

    /**
     * PATCH Method
     *
     * @param countryCode (max-length 2) Country code of the CPO performing the PATCH on the eMSP's system.
     * @param partyId (max-length 3) Party ID (Provider ID) of the CPO performing the PATCH on the eMSP's system.
     * @param sessionId (max-length 36) id of the Session object to patch on the eMSP's system.
     * @param session Partial Session object with fields to update.
     * @return SessionPartial? The patched Session object.
     */
    suspend fun patchSession(
        countryCode: CiString,
        partyId: CiString,
        sessionId: CiString,
        session: SessionPartial,
    ): SessionPartial?
}
