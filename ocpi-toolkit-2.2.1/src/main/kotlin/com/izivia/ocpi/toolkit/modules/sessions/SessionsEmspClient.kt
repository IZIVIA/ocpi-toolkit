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
    private fun buildTransport(): TransportClient = transportClientBuilder
        .buildFor(
            module = ModuleID.sessions,
            platform = serverVersionsEndpointUrl,
            platformRepository = platformRepository
        )

    override fun getSessions(
        dateFrom: Instant?,
        dateTo: Instant?,
        offset: Int,
        limit: Int?
    ): OcpiResponseBody<SearchResult<Session>> =
        buildTransport()
            .send(
                HttpRequest(
                    method = HttpMethod.GET,
                    queryParams = listOfNotNull(
                        dateFrom?.let { "date_from" to dateFrom.toString() },
                        dateTo?.let { "date_to" to dateTo.toString() },
                        "offset" to offset.toString(),
                        limit?.let { "limit" to limit.toString() }
                    ).toMap()
                ).withDebugHeaders()
                    .authenticate(platformRepository = platformRepository, baseUrl = serverVersionsEndpointUrl)
            )
            .parsePaginatedBody(offset)

    override fun putChargingPreferences(
        sessionId: CiString,
        chargingPreferences: ChargingPreferences
    ): OcpiResponseBody<ChargingPreferencesResponseType> =
        buildTransport()
            .send(
                HttpRequest(
                    method = HttpMethod.PUT,
                    path = "/$sessionId/charging_preferences",
                    body = mapper.writeValueAsString(chargingPreferences)
                ).withDebugHeaders()
                    .authenticate(platformRepository = platformRepository, baseUrl = serverVersionsEndpointUrl)
            )
            .parseBody()
}
