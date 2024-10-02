package com.izivia.ocpi.toolkit.modules.sessions.repositories

import com.izivia.ocpi.toolkit.common.CiString
import com.izivia.ocpi.toolkit.common.SearchResult
import com.izivia.ocpi.toolkit.modules.sessions.domain.ChargingPreferences
import com.izivia.ocpi.toolkit.modules.sessions.domain.ChargingPreferencesResponseType
import com.izivia.ocpi.toolkit.modules.sessions.domain.Session
import java.time.Instant

/**
 * Typically implemented by market roles like: CPO.
 *
 * - GET: Fetch Session objects of charging sessions last updated between the {date_from} and {date_to} (paginated).
 * - POST: n/a
 * - PUT: Setting Charging Preferences of an ongoing session.
 * - PATCH: n/a
 * - DELETE: n/a
 */
interface SessionsCpoRepository {

    /**
     * GET Method
     *
     * Fetch Sessions from a CPO system.
     * Only Sessions with last_update between the given {date_from} (including) and {date_to} (excluding) will be returned.
     * This request is paginated, it supports the pagination related URL parameters
     *
     * @param dateFrom Instant? Only return Sessions that have last_updated after this Date/Time.
     * @param dateTo Instant? Only return Sessions that have last_updated before this Date/Time.
     * @param offset Int? The offset of the first object returned. Default is 0.
     * @param limit Int? Maximum number of objects to GET.
     * @return List<Session> The response contains a list of Session objects that match the given parameters in the request, the header will contain the
     * pagination related headers.
     */
    suspend fun getSessions(dateFrom: Instant?, dateTo: Instant?, offset: Int = 0, limit: Int?): SearchResult<Session>

    /**
     * PUT Method
     *
     * Set/update the driver’s Charging Preferences for this charging session.
     * NOTE : The /charging_preferences URL suffix is required when setting Charging Preferences.
     *
     * @param sessionId (max-length 36) Session.id of the Session for which the Charging Preferences are to be set.
     * @param chargingPreferences Updated Charging Preferences of the driver for this Session.
     * @return ChargingPreferencesResponseType The response contains a ChargingPreferencesResponse value.
     */
    suspend fun putChargingPreferences(
        sessionId: CiString,
        chargingPreferences: ChargingPreferences,
    ): ChargingPreferencesResponseType
}
