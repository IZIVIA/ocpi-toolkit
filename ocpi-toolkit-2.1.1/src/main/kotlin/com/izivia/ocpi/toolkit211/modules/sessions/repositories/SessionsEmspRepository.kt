package com.izivia.ocpi.toolkit211.modules.sessions.repositories

import com.izivia.ocpi.toolkit211.common.CiString
import com.izivia.ocpi.toolkit211.modules.sessions.domain.Session
import com.izivia.ocpi.toolkit211.modules.sessions.domain.SessionPartial

interface SessionsEmspRepository {

    suspend fun getSession(countryCode: CiString, partyId: CiString, sessionId: CiString): Session?

    suspend fun putSession(
        countryCode: CiString,
        partyId: CiString,
        sessionId: CiString,
        session: Session,
    ): Session

    suspend fun patchSession(
        countryCode: CiString,
        partyId: CiString,
        sessionId: CiString,
        session: SessionPartial,
    ): Session?
}
