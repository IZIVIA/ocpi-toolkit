package com.izivia.ocpi.toolkit.modules.sessions

import com.izivia.ocpi.toolkit.common.*
import com.izivia.ocpi.toolkit.modules.credentials.repositories.PartnerRepository
import com.izivia.ocpi.toolkit.modules.sessions.domain.Session
import com.izivia.ocpi.toolkit.modules.sessions.domain.SessionPartial
import com.izivia.ocpi.toolkit.modules.versions.domain.ModuleID
import com.izivia.ocpi.toolkit.transport.TransportClient
import com.izivia.ocpi.toolkit.transport.TransportClientBuilder
import com.izivia.ocpi.toolkit.transport.domain.HttpMethod
import com.izivia.ocpi.toolkit.transport.domain.HttpRequest

/**
 * Sends calls to an eMSP server
 * @property transportClientBuilder used to build transport client
 * @property partnerId used to know which partner to communicate with
 * @property partnerRepository used to get information about the partner (endpoint, token)
 */
class SessionsCpoClient(
    private val transportClientBuilder: TransportClientBuilder,
    private val partnerId: String,
    private val partnerRepository: PartnerRepository,
) : SessionsEmspInterface {
    private suspend fun buildTransport(): TransportClient = transportClientBuilder
        .buildFor(
            module = ModuleID.sessions,
            partnerId = partnerId,
            partnerRepository = partnerRepository,
        )

    override suspend fun getSession(
        countryCode: CiString,
        partyId: CiString,
        sessionId: CiString,
    ): OcpiResponseBody<Session?> =
        with(buildTransport()) {
            send(
                HttpRequest(
                    method = HttpMethod.GET,
                    path = "/$countryCode/$partyId/$sessionId",
                ).withRequiredHeaders(
                    requestId = generateRequestId(),
                    correlationId = generateCorrelationId(),
                )
                    .authenticate(partnerRepository = partnerRepository, partnerId = partnerId),
            )
                .parseBody()
        }

    override suspend fun putSession(
        countryCode: CiString,
        partyId: CiString,
        sessionId: CiString,
        session: Session,
    ): OcpiResponseBody<Session?> =
        with(buildTransport()) {
            send(
                HttpRequest(
                    method = HttpMethod.PUT,
                    path = "/$countryCode/$partyId/$sessionId",
                    body = mapper.writeValueAsString(session),
                ).withRequiredHeaders(
                    requestId = generateRequestId(),
                    correlationId = generateCorrelationId(),
                )
                    .authenticate(partnerRepository = partnerRepository, partnerId = partnerId),
            )
                .parseBody()
        }

    override suspend fun patchSession(
        countryCode: CiString,
        partyId: CiString,
        sessionId: CiString,
        session: SessionPartial,
    ): OcpiResponseBody<Session?> =
        with(buildTransport()) {
            send(
                HttpRequest(
                    method = HttpMethod.PATCH,
                    path = "/$countryCode/$partyId/$sessionId",
                    body = mapper.writeValueAsString(session),
                ).withRequiredHeaders(
                    requestId = generateRequestId(),
                    correlationId = generateCorrelationId(),
                )
                    .authenticate(partnerRepository = partnerRepository, partnerId = partnerId),
            )
                .parseBody()
        }
}
