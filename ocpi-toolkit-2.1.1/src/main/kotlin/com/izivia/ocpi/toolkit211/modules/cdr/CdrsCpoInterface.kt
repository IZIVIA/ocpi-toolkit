package com.izivia.ocpi.toolkit211.modules.cdr

import com.izivia.ocpi.toolkit211.common.SearchResult
import com.izivia.ocpi.toolkit211.modules.cdr.domain.Cdr
import java.time.Instant

/**
 * Typically implemented by market roles like: CPO.
 *
 * - GET: Fetch CDRs last updated between the {date_from} and {date_to} (paginated).
 */
interface CdrsCpoInterface {

    suspend fun getCdrs(
        dateFrom: Instant?,
        dateTo: Instant?,
        offset: Int = 0,
        limit: Int?,
    ): SearchResult<Cdr>
}
