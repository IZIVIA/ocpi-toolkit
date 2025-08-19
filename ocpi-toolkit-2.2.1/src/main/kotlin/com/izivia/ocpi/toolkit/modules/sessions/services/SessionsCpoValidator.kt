package com.izivia.ocpi.toolkit.modules.sessions.services

import com.izivia.ocpi.toolkit.common.CiString
import com.izivia.ocpi.toolkit.common.SearchResult
import com.izivia.ocpi.toolkit.common.validation.validateDates
import com.izivia.ocpi.toolkit.common.validation.validateInt
import com.izivia.ocpi.toolkit.common.validation.validateLength
import com.izivia.ocpi.toolkit.common.validation.validateParams
import com.izivia.ocpi.toolkit.modules.sessions.SessionsCpoInterface
import com.izivia.ocpi.toolkit.modules.sessions.domain.ChargingPreferences
import com.izivia.ocpi.toolkit.modules.sessions.domain.ChargingPreferencesResponseType
import com.izivia.ocpi.toolkit.modules.sessions.domain.Session
import java.time.Instant

open class SessionsCpoValidator(
    private val service: SessionsCpoInterface,
) : SessionsCpoInterface {

    override suspend fun getSessions(
        dateFrom: Instant,
        dateTo: Instant?,
        offset: Int,
        limit: Int?,
    ): SearchResult<Session> {
        validateParams {
            if (dateTo != null) validateDates("dateFrom", dateFrom, "dateTo", dateTo)
            if (limit != null) validateInt("limit", limit, 0, null)
            validateInt("offset", offset, 0, null)
        }

        return service
            .getSessions(dateFrom, dateTo, offset, limit)
            .also { searchResult ->
                searchResult.list.forEach { session -> session.validate() }
            }
    }

    override suspend fun putChargingPreferences(
        sessionId: CiString,
        chargingPreferences: ChargingPreferences,
    ): ChargingPreferencesResponseType {
        validateParams {
            validateLength("sessionId", sessionId, 36)
            chargingPreferences.validate()
        }

        return service.putChargingPreferences(sessionId, chargingPreferences) // nothing to validate
    }
}
