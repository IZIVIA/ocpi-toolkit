package com.izivia.ocpi.toolkit211.modules.sessions.services

import com.izivia.ocpi.toolkit211.common.SearchResult
import com.izivia.ocpi.toolkit211.common.validation.validate
import com.izivia.ocpi.toolkit211.common.validation.validateDates
import com.izivia.ocpi.toolkit211.common.validation.validateInt
import com.izivia.ocpi.toolkit211.modules.sessions.SessionsCpoInterface
import com.izivia.ocpi.toolkit211.modules.sessions.domain.Session
import java.time.Instant

open class SessionsCpoValidator(
    private val service: SessionsCpoInterface,
) : SessionsCpoInterface {

    override suspend fun getSessions(
        dateFrom: Instant?,
        dateTo: Instant?,
        offset: Int,
        limit: Int?,
    ): SearchResult<Session> {
        validate {
            if (dateFrom != null && dateTo != null) validateDates("dateFrom", dateFrom, "dateTo", dateTo)
            if (limit != null) validateInt("limit", limit, 0, null)
            validateInt("offset", offset, 0, null)
        }

        return service
            .getSessions(dateFrom, dateTo, offset, limit)
            .also { searchResult ->
                searchResult.list.forEach { session -> session.validate() }
            }
    }
}
