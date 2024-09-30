package com.izivia.ocpi.toolkit.modules.sessions.services

import com.izivia.ocpi.toolkit.common.CiString
import com.izivia.ocpi.toolkit.common.OcpiResponseBody
import com.izivia.ocpi.toolkit.common.validation.validate
import com.izivia.ocpi.toolkit.common.validation.validateLength
import com.izivia.ocpi.toolkit.common.validation.validateSame
import com.izivia.ocpi.toolkit.modules.sessions.SessionsEmspInterface
import com.izivia.ocpi.toolkit.modules.sessions.domain.Session
import com.izivia.ocpi.toolkit.modules.sessions.domain.SessionPartial
import com.izivia.ocpi.toolkit.modules.sessions.repositories.SessionsEmspRepository

open class SessionsEmspService(
    private val service: SessionsEmspRepository,
) : SessionsEmspInterface {
    override suspend fun getSession(
        countryCode: CiString,
        partyId: CiString,
        sessionId: CiString,
    ): OcpiResponseBody<Session?> = OcpiResponseBody.of {
        validate {
            validateLength("countryCode", countryCode, 2)
            validateLength("partyId", partyId, 3)
            validateLength("sessionId", sessionId, 36)
        }
        service
            .getSession(countryCode, partyId, sessionId)
            ?.validate()
    }

    override suspend fun putSession(
        countryCode: CiString,
        partyId: CiString,
        sessionId: CiString,
        session: Session,
    ): OcpiResponseBody<Session?> = OcpiResponseBody.of {
        validate {
            session.validate()
            validateLength("countryCode", countryCode, 2)
            validateLength("partyId", partyId, 3)
            validateLength("sessionId", sessionId, 36)
            validateSame("countryCode", countryCode, session.countryCode)
            validateSame("partyId", partyId, session.partyId)
            validateSame("sessionId", sessionId, session.id)
        }
        service.putSession(countryCode, partyId, sessionId, session)?.validate()
    }

    override suspend fun patchSession(
        countryCode: CiString,
        partyId: CiString,
        sessionId: CiString,
        session: SessionPartial,
    ): OcpiResponseBody<Session?> = OcpiResponseBody.of {
        validate {
            session.validate()
            validateLength("countryCode", countryCode, 2)
            validateLength("partyId", partyId, 3)
            validateLength("sessionId", sessionId, 36)
        }
        service.patchSession(countryCode, partyId, sessionId, session)?.validate()
    }
}
