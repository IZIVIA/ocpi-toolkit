package com.izivia.ocpi.toolkit.modules.chargingProfiles

import com.izivia.ocpi.toolkit.common.CiString
import com.izivia.ocpi.toolkit.common.OcpiResponseBody
import com.izivia.ocpi.toolkit.common.URL
import com.izivia.ocpi.toolkit.modules.chargingProfiles.domain.ChargingProfileResponse
import com.izivia.ocpi.toolkit.modules.chargingProfiles.domain.SetChargingProfile

/**
 * The ChargingProfiles module consists of two interfaces: a Receiver interface that enables a Sender (and its clients)
 * to send ChargingProfiles to a Location/EVSE, and an Sender interface to receive the response from the Location/EVSE
 * asynchronously.
 *
 * This is the receiver interface
 *
 * Method: Description
 * - GET: Gets the ActiveChargingProfile for a specific charging session.
 * - POST: n/a (use PUT)
 * - PUT: Creates/updates a ChargingProfile for a specific charging session.
 * - PATCH: n/a (use PUT)
 * - DELETE: Cancels an existing ChargingProfile for a specific charging session.
 */
interface ChargingProfilesCpoInterface {

    /**
     * Retrieves the ActiveChargingProfile as it is currently planned for the the given session.
     *
     * Endpoint structure definition:
     * - /ocpi/cpo/2.2.1/chargingprofiles/{session_id}?duration={duration}&response_url={url}
     *
     * @param sessionId (max-length=36) The unique id that identifies the session in the CPO platform.
     * @param duration Length of the requested ActiveChargingProfile in seconds Duration in seconds.
     * @param responseUrl (max-length=255) URL that the ActiveChargingProfileResult POST should be send to. This URL
     * might contain an unique ID to be able to distinguish between GET ActiveChargingProfile requests.
     */
    suspend fun getActiveChargingProfile(
        sessionId: CiString,
        duration: Int,
        responseUrl: URL,
    ): OcpiResponseBody<ChargingProfileResponse>

    /**
     * Creates a new ChargingProfile on a session, or replaces an existing ChargingProfile on the EVSE.
     *
     * Endpoint structure definition:
     * - /ocpi/cpo/2.2.1/chargingprofiles/{session_id}
     *
     * @param sessionId (max-length=36) The unique id that identifies the session in the CPO platform.
     */
    suspend fun putChargingProfile(
        sessionId: CiString,
        setChargingProfile: SetChargingProfile,
    ): OcpiResponseBody<ChargingProfileResponse>

    /**
     * Clears the ChargingProfile set by the eMSP on the given session.
     *
     * Endpoint structure definition:
     * - /ocpi/cpo/2.2.1/chargingprofiles/{session_id}?response_url={url}
     *
     * @param sessionId (max-length=36) The unique id that identifies the session in the CPO platform.
     * @param responseUrl (max-length=255) URL that the ClearProfileResult POST should be send to. This URL might
     * contain an unique ID to be able to distinguish between DELETE ChargingProfile requests.
     */
    suspend fun deleteChargingProfile(
        sessionId: CiString,
        responseUrl: URL,
    ): OcpiResponseBody<ChargingProfileResponse>
}
