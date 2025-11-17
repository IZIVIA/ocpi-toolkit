package com.izivia.ocpi.toolkit.modules.hubclientinfo

import com.izivia.ocpi.toolkit.common.SearchResult
import com.izivia.ocpi.toolkit.modules.hubclientinfo.domain.ClientInfo
import java.time.Instant

/**
 * Sender Interface (usually HUB)
 *
 * - GET:Get the list of known ClientInfo objects, last updated between the {date_from} and {date_to} paginated)
 * - POST: n/a
 * - PUT: n/a
 * - PATCH: n/a
 * - DELETE: n/a
 */
interface HubClientInfoSenderInterface {

    /**
     * If additional parameters: {date_from} and/or {date_to} are provided, only ClientInfo objects with (last_updated)
     * between the given {date_from} (including) and {date_to} (excluding) will be returned.
     *
     * This request is paginated, it supports the pagination related URL parameters.
     *
     * @param dateFrom Instant? Only return ClientInfo that have last_updated after or equal to this Date/Time
     * (inclusive).
     * @param dateTo Instant?Only return ClientInfo that have last_updated up to this Date/Time, but not including
     * (exclusive).
     * @param offset Int? The offset of the first object returned. Default is 0.
     * @param limit Int? Maximum number of objects to GET.
     * @return List<ClientInfo> The endpoint response with list of valid ClientInfo objects, the header will contain the
     * pagination related headers. Any older information that is not specified in the response is considered as no
     * longer valid. Each object must contain all required fields. Fields that are not specified may be considered as
     * null values.
     */
    suspend fun getAll(
        dateFrom: Instant?,
        dateTo: Instant?,
        offset: Int,
        limit: Int?,
    ): SearchResult<ClientInfo>
}
