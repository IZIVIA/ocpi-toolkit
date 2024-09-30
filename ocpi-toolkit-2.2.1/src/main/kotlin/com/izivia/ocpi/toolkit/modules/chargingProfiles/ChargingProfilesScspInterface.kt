package com.izivia.ocpi.toolkit.modules.chargingProfiles

import com.izivia.ocpi.toolkit.common.CiString
import com.izivia.ocpi.toolkit.common.OcpiResponseBody
import com.izivia.ocpi.toolkit.modules.chargingProfiles.domain.ActiveChargingProfile
import com.izivia.ocpi.toolkit.modules.chargingProfiles.domain.ActiveChargingProfileResult
import com.izivia.ocpi.toolkit.modules.chargingProfiles.domain.ChargingProfileResult
import com.izivia.ocpi.toolkit.modules.chargingProfiles.domain.ClearProfileResult

/**
 * The ChargingProfiles module consists of two interfaces: a Receiver interface that enables a Sender (and its clients)
 * to send ChargingProfiles to a Location/EVSE, and an Sender interface to receive the response from the Location/EVSE
 * asynchronously.
 *
 * This is the sender interface
 *
 * Method: Description
 * - GET: n/a
 * - POST: Receive the asynchronous response from the Charge Point.
 * - PUT: Receiver (typically CPO) can send an updated ActiveChargingProfile when other inputs have made changes to
 * existing profile. When the Receiver (typically CPO) sends a update profile to the EVSE, for an other reason
 * then the Sender (Typically SCSP) asking, the Sender SHALL post an update to this interface. When a local
 * input influence the ActiveChargingProfile in the EVSE AND the Receiver (typically CPO) is made aware of this,
 * the Receiver SHALL post an update to this interface.
 * - PATCH: n/a
 * - DELETE: n/a
 */
interface ChargingProfilesScspInterface {
    suspend fun postCallbackActiveChargingProfile(
        requestId: String,
        result: ActiveChargingProfileResult,
    ): OcpiResponseBody<Any>

    suspend fun postCallbackChargingProfile(
        requestId: String,
        result: ChargingProfileResult,
    ): OcpiResponseBody<Any>

    suspend fun postCallbackClearProfile(
        requestId: String,
        result: ClearProfileResult,
    ): OcpiResponseBody<Any>

    suspend fun putActiveChargingProfile(
        sessionId: CiString,
        activeChargingProfile: ActiveChargingProfile,
    ): OcpiResponseBody<Any>
}
