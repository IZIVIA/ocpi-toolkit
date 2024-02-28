package com.izivia.ocpi.toolkit.modules.chargingProfiles.repositories

import com.izivia.ocpi.toolkit.modules.chargingProfiles.domain.ActiveChargingProfile
import com.izivia.ocpi.toolkit.modules.chargingProfiles.domain.ActiveChargingProfileResult
import com.izivia.ocpi.toolkit.modules.chargingProfiles.domain.ChargingProfileResult
import com.izivia.ocpi.toolkit.modules.chargingProfiles.domain.ClearProfileResult

interface ChargingProfilesScspRepository {
    suspend fun postCallbackActiveChargingProfile(requestId: String, result: ActiveChargingProfileResult)

    suspend fun postCallbackChargingProfile(requestId: String, result: ChargingProfileResult)

    suspend fun postCallbackClearProfile(requestId: String, result: ClearProfileResult)

    suspend fun putActiveChargingProfile(sessionId: String, activeChargingProfile: ActiveChargingProfile)
}
