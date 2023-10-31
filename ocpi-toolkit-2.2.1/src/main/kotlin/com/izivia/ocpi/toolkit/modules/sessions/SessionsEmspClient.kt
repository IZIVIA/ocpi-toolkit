package com.izivia.ocpi.toolkit.modules.sessions

import com.izivia.ocpi.toolkit.common.*
import com.izivia.ocpi.toolkit.modules.credentials.repositories.PlatformRepository
import com.izivia.ocpi.toolkit.modules.sessions.domain.ChargingPreferences
import com.izivia.ocpi.toolkit.modules.sessions.domain.ChargingPreferencesResponseType
import com.izivia.ocpi.toolkit.modules.sessions.domain.Session
import com.izivia.ocpi.toolkit.modules.versions.domain.ModuleID
import com.izivia.ocpi.toolkit.transport.TransportClient
import com.izivia.ocpi.toolkit.transport.TransportClientBuilder
import com.izivia.ocpi.toolkit.transport.domain.HttpMethod
import com.izivia.ocpi.toolkit.transport.domain.HttpRequest
import java.time.Instant

/**
 * Sends calls to the CPO
 * @property transportClientBuilder used to build transport client
 * @property serverVersionsEndpointUrl used to know which platform to communicate with
 * @property platformRepository used to get information about the platform (endpoint, token)
 */
class SessionsEmspClient(
    private val transportClientBuilder: TransportClientBuilder,
    private val serverVersionsEndpointUrl: String,
    private val platformRepository: PlatformRepository
) : SessionsCpoInterface {
    private suspend fun buildTransport(): TransportClient = transportClientBuilder
        .buildFor(
            module = ModuleID.sessions,
            platform = serverVersionsEndpointUrl,
            platformRepository = platformRepository
        )

    override suspend fun getSessions(
        dateFrom: Instant?,
        dateTo: Instant?,
        offset: Int,
        limit: Int?
    ): OcpiResponseBody<SearchResult<Session>> =
        with(buildTransport()) {
            send(
                HttpRequest(
                    method = HttpMethod.GET,
                    queryParams = listOfNotNull(
                        dateFrom?.let { "date_from" to dateFrom.toString() },
                        dateTo?.let { "date_to" to dateTo.toString() },
                        "offset" to offset.toString(),
                        limit?.let { "limit" to limit.toString() }
                    ).toMap()
                ).withRequiredHeaders(
                    requestId = generateRequestId(),
                    correlationId = generateCorrelationId()
                )
                    .authenticate(platformRepository = platformRepository, platformUrl = serverVersionsEndpointUrl)
            )
                .parsePaginatedBody(offset)
        }

    override suspend fun putChargingPreferences(
        sessionId: CiString,
        chargingPreferences: ChargingPreferences
    ): OcpiResponseBody<ChargingPreferencesResponseType> =
        with(buildTransport()) {
            send(
                HttpRequest(
                    method = HttpMethod.PUT,
                    path = "/$sessionId/charging_preferences",
                    body = mapper.writeValueAsString(chargingPreferences)
                ).withRequiredHeaders(
                    requestId = generateRequestId(),
                    correlationId = generateCorrelationId()
                )
                    .authenticate(platformRepository = platformRepository, platformUrl = serverVersionsEndpointUrl)
            )
                .parseBody()
        }
}
