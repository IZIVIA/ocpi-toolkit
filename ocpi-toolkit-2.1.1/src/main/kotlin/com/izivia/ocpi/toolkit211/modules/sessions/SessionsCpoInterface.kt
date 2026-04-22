package com.izivia.ocpi.toolkit211.modules.sessions

import com.izivia.ocpi.toolkit211.common.SearchResult
import com.izivia.ocpi.toolkit211.modules.sessions.domain.Session
import java.time.Instant

/**
 * Typically implemented by market roles like: CPO.
 *
 * - GET: Fetch Session objects of charging sessions last updated between the {date_from} and {date_to} (paginated).
 */
interface SessionsCpoInterface {

    /**
     * GET Method
     *
     * Fetch Sessions from a CPO system.
     * Only Sessions with last_update between the given {date_from} (including) and {date_to} (excluding) will be
     * returned. This request is paginated, it supports the pagination related URL parameters
     *
     * @param dateFrom Instant Only return Sessions that have last_updated after this Date/Time.
     * @param dateTo Instant? Only return Sessions that have last_updated before this Date/Time.
     * @param offset Int The offset of the first object returned. Default is 0.
     * @param limit Int? Maximum number of objects to GET.
     * @return SearchResult<Session> The response contains a list of Session objects that match the given parameters.
     */
    suspend fun getSessions(
        dateFrom: Instant?,
        dateTo: Instant?,
        offset: Int = 0,
        limit: Int?,
    ): SearchResult<Session>
}
