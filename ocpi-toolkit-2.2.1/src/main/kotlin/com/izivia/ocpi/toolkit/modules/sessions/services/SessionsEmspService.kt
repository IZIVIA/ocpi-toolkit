package com.izivia.ocpi.toolkit.modules.sessions.services

import com.izivia.ocpi.toolkit.common.CiString
import com.izivia.ocpi.toolkit.common.OcpiResponseBody
import com.izivia.ocpi.toolkit.common.validation.validate
import com.izivia.ocpi.toolkit.common.validation.validateLength
import com.izivia.ocpi.toolkit.modules.sessions.SessionsEmspInterface
import com.izivia.ocpi.toolkit.modules.sessions.domain.Session
import com.izivia.ocpi.toolkit.modules.sessions.repositories.SessionsEmspRepository

class SessionsEmspService(
    private val service: SessionsEmspRepository
) : SessionsEmspInterface {
    override fun getSession(countryCode: CiString, partyId: CiString, sessionId: CiString): OcpiResponseBody<Session> =
        OcpiResponseBody.of {
            validate {
                validateLength("countryCode", countryCode, 2)
                validateLength("partyId", partyId, 3)
                validateLength("sessionId", sessionId, 36)
            }
            service.getSession(countryCode, partyId, sessionId).validate()
        }

    override fun putSession(
        countryCode: CiString,
        partyId: CiString,
        sessionId: CiString,
        session: Session
    ): OcpiResponseBody<Session?> = OcpiResponseBody.of {
        validate {
            session.validate()
            validateLength("countryCode", countryCode, 2)
            validateLength("partyId", partyId, 3)
            validateLength("sessionId", sessionId, 36)
        }
        service.putSession(countryCode, partyId, sessionId, session)?.validate()
    }

    override fun patchSession(
        countryCode: CiString,
        partyId: CiString,
        sessionId: CiString,
        session: Session
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
