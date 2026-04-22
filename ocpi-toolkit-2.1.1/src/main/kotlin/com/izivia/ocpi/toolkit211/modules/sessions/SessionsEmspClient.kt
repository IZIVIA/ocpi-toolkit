package com.izivia.ocpi.toolkit211.modules.sessions

import com.izivia.ocpi.toolkit.transport.TransportClient
import com.izivia.ocpi.toolkit.transport.domain.HttpMethod
import com.izivia.ocpi.toolkit.transport.domain.HttpRequest
import com.izivia.ocpi.toolkit211.common.*
import com.izivia.ocpi.toolkit211.modules.sessions.domain.Session
import com.izivia.ocpi.toolkit211.modules.sessions.domain.SessionPartial
import com.izivia.ocpi.toolkit211.modules.versions.domain.ModuleID
import java.time.Instant

/**
 * Sends calls to the CPO
 * @property transportClientBuilder used to build transport client
 * @property partnerId used to know which partner to communicate with
 */
class SessionsEmspClient(
    private val transportClientBuilder: TransportClientBuilder,
    private val partnerId: String,
    private val ignoreInvalidListEntry: Boolean = false,
) : SessionsCpoInterface {

    private suspend fun buildTransport(): TransportClient = transportClientBuilder
        .buildFor(
            partnerId = partnerId,
            module = ModuleID.sessions,
        )

    override suspend fun getSessions(
        dateFrom: Instant?,
        dateTo: Instant?,
        offset: Int,
        limit: Int?,
    ): SearchResult<Session> = with(buildTransport()) {
        send(
            HttpRequest(
                method = HttpMethod.GET,
                queryParams = listOfNotNull(
                    dateFrom?.let { "date_from" to dateFrom.toString() },
                    dateTo?.let { "date_to" to dateTo.toString() },
                    "offset" to offset.toString(),
                    limit?.let { "limit" to limit.toString() },
                ).toMap(),
            ),
        ).let { res ->
            if (ignoreInvalidListEntry) {
                res.parseSearchResultIgnoringInvalid<Session, SessionPartial>(offset)
            } else {
                res.parseSearchResult<Session>(offset)
            }
        }
    }

    suspend fun getSessionsNextPage(
        previousResponse: SearchResult<Session>,
    ): SearchResult<Session>? = getNextPage<Session, SessionPartial>(
        transportClientBuilder = transportClientBuilder,
        partnerId = partnerId,
        previousResponse = previousResponse,
        ignoreInvalidListEntry = ignoreInvalidListEntry,
    )
}
