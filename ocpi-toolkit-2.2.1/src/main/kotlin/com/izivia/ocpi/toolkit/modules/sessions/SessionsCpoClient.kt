package com.izivia.ocpi.toolkit.modules.sessions

import com.izivia.ocpi.toolkit.common.*
import com.izivia.ocpi.toolkit.modules.credentials.repositories.PlatformRepository
import com.izivia.ocpi.toolkit.modules.sessions.domain.Session
import com.izivia.ocpi.toolkit.modules.versions.domain.ModuleID
import com.izivia.ocpi.toolkit.transport.TransportClient
import com.izivia.ocpi.toolkit.transport.TransportClientBuilder
import com.izivia.ocpi.toolkit.transport.domain.HttpMethod
import com.izivia.ocpi.toolkit.transport.domain.HttpRequest

/**
 * Sends calls to an eMSP server
 * @property transportClientBuilder used to build transport client
 * @property serverVersionsEndpointUrl used to know which platform to communicate with
 * @property platformRepository used to get information about the platform (endpoint, token)
 */
class SessionsCpoClient(
    private val transportClientBuilder: TransportClientBuilder,
    private val serverVersionsEndpointUrl: String,
    private val platformRepository: PlatformRepository
) : SessionsEmspInterface {
    private fun buildTransport(): TransportClient = transportClientBuilder
        .buildFor(
            module = ModuleID.sessions,
            platform = serverVersionsEndpointUrl,
            platformRepository = platformRepository
        )

    override fun getSession(countryCode: CiString, partyId: CiString, sessionId: CiString): OcpiResponseBody<Session> =
        buildTransport()
            .send(
                HttpRequest(
                    method = HttpMethod.GET,
                    path = "/$countryCode/$partyId/$sessionId"
                ).withDebugHeaders()
                    .authenticate(platformRepository = platformRepository, baseUrl = serverVersionsEndpointUrl)
            )
            .parseBody()

    override fun putSession(
        countryCode: CiString,
        partyId: CiString,
        sessionId: CiString,
        session: Session
    ): OcpiResponseBody<Session?> =
        buildTransport()
            .send(
                HttpRequest(
                    method = HttpMethod.PUT,
                    path = "/$countryCode/$partyId/$sessionId",
                    body = mapper.writeValueAsString(session)
                ).withDebugHeaders()
                    .authenticate(platformRepository = platformRepository, baseUrl = serverVersionsEndpointUrl)
            )
            .parseBody()

    override fun patchSession(
        countryCode: CiString,
        partyId: CiString,
        sessionId: CiString,
        session: Session
    ): OcpiResponseBody<Session?> =
        buildTransport()
            .send(
                HttpRequest(
                    method = HttpMethod.PATCH,
                    path = "/$countryCode/$partyId/$sessionId",
                    body = mapper.writeValueAsString(session)
                ).withDebugHeaders()
                    .authenticate(platformRepository = platformRepository, baseUrl = serverVersionsEndpointUrl)
            )
            .parseBody()
}