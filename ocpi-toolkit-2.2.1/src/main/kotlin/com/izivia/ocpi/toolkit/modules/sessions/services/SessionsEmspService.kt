package com.izivia.ocpi.toolkit.modules.sessions.services

import com.izivia.ocpi.toolkit.common.CiString
import com.izivia.ocpi.toolkit.modules.sessions.SessionsEmspInterface
import com.izivia.ocpi.toolkit.modules.sessions.domain.Session
import com.izivia.ocpi.toolkit.modules.sessions.domain.SessionPartial
import com.izivia.ocpi.toolkit.modules.sessions.domain.toPartial
import com.izivia.ocpi.toolkit.modules.sessions.repositories.SessionsEmspRepository

open class SessionsEmspService(
    private val repository: SessionsEmspRepository,
) : SessionsEmspInterface {

    override suspend fun getSession(
        countryCode: CiString,
        partyId: CiString,
        sessionId: CiString,
    ): Session? {
        return repository
            .getSession(countryCode, partyId, sessionId)
    }

    override suspend fun putSession(
        countryCode: CiString,
        partyId: CiString,
        sessionId: CiString,
        session: Session,
    ): SessionPartial {
        return repository.putSession(countryCode, partyId, sessionId, session)
            .toPartial()
    }

    override suspend fun patchSession(
        countryCode: CiString,
        partyId: CiString,
        sessionId: CiString,
        session: SessionPartial,
    ): SessionPartial? {
        return repository.patchSession(countryCode, partyId, sessionId, session)
            ?.toPartial()
    }
}
