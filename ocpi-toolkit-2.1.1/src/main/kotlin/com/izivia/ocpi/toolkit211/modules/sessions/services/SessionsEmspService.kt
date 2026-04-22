package com.izivia.ocpi.toolkit211.modules.sessions.services

import com.izivia.ocpi.toolkit211.common.CiString
import com.izivia.ocpi.toolkit211.modules.sessions.SessionsEmspInterface
import com.izivia.ocpi.toolkit211.modules.sessions.domain.Session
import com.izivia.ocpi.toolkit211.modules.sessions.domain.SessionPartial
import com.izivia.ocpi.toolkit211.modules.sessions.domain.toPartial
import com.izivia.ocpi.toolkit211.modules.sessions.repositories.SessionsEmspRepository

open class SessionsEmspService(
    private val repository: SessionsEmspRepository,
) : SessionsEmspInterface {

    override suspend fun getSession(
        countryCode: CiString,
        partyId: CiString,
        sessionId: CiString,
    ): Session? = repository.getSession(countryCode, partyId, sessionId)

    override suspend fun putSession(
        countryCode: CiString,
        partyId: CiString,
        sessionId: CiString,
        session: Session,
    ): SessionPartial = repository.putSession(countryCode, partyId, sessionId, session).toPartial()

    override suspend fun patchSession(
        countryCode: CiString,
        partyId: CiString,
        sessionId: CiString,
        session: SessionPartial,
    ): SessionPartial? = repository.patchSession(countryCode, partyId, sessionId, session)?.toPartial()
}
