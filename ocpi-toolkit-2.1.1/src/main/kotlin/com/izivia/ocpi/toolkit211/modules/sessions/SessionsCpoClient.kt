package com.izivia.ocpi.toolkit211.modules.sessions

import com.izivia.ocpi.toolkit.transport.TransportClient
import com.izivia.ocpi.toolkit.transport.domain.HttpMethod
import com.izivia.ocpi.toolkit.transport.domain.HttpRequest
import com.izivia.ocpi.toolkit211.common.CiString
import com.izivia.ocpi.toolkit211.common.TransportClientBuilder
import com.izivia.ocpi.toolkit211.common.parseOptionalResult
import com.izivia.ocpi.toolkit211.common.parseResultOrNull
import com.izivia.ocpi.toolkit211.modules.sessions.domain.Session
import com.izivia.ocpi.toolkit211.modules.sessions.domain.SessionPartial
import com.izivia.ocpi.toolkit211.modules.versions.domain.ModuleID
import com.izivia.ocpi.toolkit211.serialization.mapper
import com.izivia.ocpi.toolkit211.serialization.serializeObject

/**
 * Sends calls to an eMSP server
 * @property transportClientBuilder used to build transport client
 * @property partnerId used to know which partner to communicate with
 */
class SessionsCpoClient(
    private val transportClientBuilder: TransportClientBuilder,
    private val partnerId: String,
) : SessionsEmspInterface {

    private suspend fun buildTransport(): TransportClient = transportClientBuilder
        .buildFor(
            partnerId = partnerId,
            module = ModuleID.sessions,
        )

    override suspend fun getSession(
        countryCode: CiString,
        partyId: CiString,
        sessionId: CiString,
    ): Session? = with(buildTransport()) {
        send(
            HttpRequest(
                method = HttpMethod.GET,
                path = "/$countryCode/$partyId/$sessionId",
            ),
        ).parseOptionalResult()
    }

    override suspend fun putSession(
        countryCode: CiString,
        partyId: CiString,
        sessionId: CiString,
        session: Session,
    ): SessionPartial = with(buildTransport()) {
        send(
            HttpRequest(
                method = HttpMethod.PUT,
                path = "/$countryCode/$partyId/$sessionId",
                body = mapper.serializeObject(session),
            ),
        ).parseResultOrNull() ?: SessionPartial()
    }

    override suspend fun patchSession(
        countryCode: CiString,
        partyId: CiString,
        sessionId: CiString,
        session: SessionPartial,
    ): SessionPartial? = with(buildTransport()) {
        send(
            HttpRequest(
                method = HttpMethod.PATCH,
                path = "/$countryCode/$partyId/$sessionId",
                body = mapper.serializeObject(session),
            ),
        ).parseResultOrNull()
    }
}
