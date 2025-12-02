package com.izivia.ocpi.toolkit.modules.tariff

import com.izivia.ocpi.toolkit.common.SearchResult
import com.izivia.ocpi.toolkit.modules.tariff.domain.Tariff
import java.time.Instant

/**
 * CPO Interface
 *
 * - GET: Returns Tariff objects from the CPO, last updated between the {date_from} and {date_to} (paginated)
 * - POST: n/a
 * - PUT: n/a
 * - PATCH: n/a
 * - DELETE: n/a
 */
interface TariffCpoInterface {

    /**
     * If additional parameters: {date_from} and/or {date_to} are provided, only Tariffs with last_updated between the
     * given {date_from} (including) and {date_to} (excluding) will be returned.
     *
     * This request is paginated, it supports the pagination related URL parameters.
     *
     * @param dateFrom Instant? Only return Tariffs that have last_updated after or equal to this Date/Time (inclusive).
     * @param dateTo Instant? Only return Tariffs that have last_updated up to this Date/Time, but not including (exclusive).
     * @param offset Int? The offset of the first object returned. Default is 0.
     * @param limit Int? Maximum number of objects to GET.
     * @return The endpoint returns an object with a list of valid Tariffs, the header will contain the pagination
     * related headers. Any older information that is not specified in the response is considered no longer valid.
     * Each object must contain all required fields. Fields that are not specified may be considered as null values.
     */
    suspend fun getTariffs(
        dateFrom: Instant?,
        dateTo: Instant?,
        offset: Int = 0,
        limit: Int?,
    ): SearchResult<Tariff>
}
