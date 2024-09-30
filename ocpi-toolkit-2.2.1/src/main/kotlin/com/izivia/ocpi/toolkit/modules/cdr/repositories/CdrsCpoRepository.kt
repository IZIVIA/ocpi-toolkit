package com.izivia.ocpi.toolkit.modules.cdr.repositories

import com.izivia.ocpi.toolkit.common.SearchResult
import com.izivia.ocpi.toolkit.modules.cdr.domain.Cdr
import java.time.Instant

/**
 * Typically implemented by market roles like: CPO.
 *
 * The CDRs endpoint can be used to retrieve CDRs.
 *
 * - GET: Fetch CDRs last updated (which in the current version of OCPI can only be the creation Date/Time) between the
 *   {date_from} and {date_to} (paginated).
 * - POST: n/a
 * - PUT: n/a
 * - PATCH: n/a
 * - DELETE: n/a
 */
interface CdrsCpoRepository {
    /**
     * GET Method
     *
     * Fetch CDRs from the CPO’s system.
     * If additional parameters: {date_from} and/or {date_to} are provided, only CDRs with last_updated between the
     * given {date_from} (including) and {date_to} (excluding) will be returned.
     * This request is paginated, it supports the pagination related URL parameters.
     *
     * @param dateFrom Instant? Only return CDRs that have last_updated after or equal to this Date/Time (inclusive).
     * @param dateTo Instant? Only return CDRs that have last_updated up to this Date/Time, but not including (exclusive).
     * @param offset Int? The offset of the first object returned. Default is 0.
     * @param limit Int? Maximum number of objects to GET.
     * @return List<Cdr> The endpoint returns a list of CDRs matching the given parameters in the GET request,
     * the header will contain the pagination related headers.
     */
    suspend fun getCdrs(
        dateFrom: Instant?,
        dateTo: Instant?,
        offset: Int = 0,
        limit: Int?,
    ): SearchResult<Cdr>
}
