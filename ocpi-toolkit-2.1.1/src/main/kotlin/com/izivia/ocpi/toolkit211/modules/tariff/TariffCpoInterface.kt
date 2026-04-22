package com.izivia.ocpi.toolkit211.modules.tariff

import com.izivia.ocpi.toolkit211.common.SearchResult
import com.izivia.ocpi.toolkit211.modules.tariff.domain.Tariff
import java.time.Instant

/**
 * CPO Interface
 *
 * - GET: Returns Tariff objects from the CPO, last updated between the {date_from} and {date_to} (paginated)
 */
interface TariffCpoInterface {

    suspend fun getTariffs(
        dateFrom: Instant?,
        dateTo: Instant?,
        offset: Int = 0,
        limit: Int?,
    ): SearchResult<Tariff>
}
