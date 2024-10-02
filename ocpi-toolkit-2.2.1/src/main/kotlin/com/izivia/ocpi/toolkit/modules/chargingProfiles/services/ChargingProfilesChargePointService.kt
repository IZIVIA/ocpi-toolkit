package com.izivia.ocpi.toolkit.modules.chargingProfiles.services

import com.izivia.ocpi.toolkit.common.URL
import com.izivia.ocpi.toolkit.modules.chargingProfiles.domain.ChargingProfileResponse
import com.izivia.ocpi.toolkit.modules.chargingProfiles.domain.SetChargingProfile

interface ChargingProfilesChargePointService {
    suspend fun getActiveChargingProfile(
        sessionId: String,
        duration: Int,
        responseUrl: URL,
    ): ChargingProfileResponse

    suspend fun putChargingProfile(
        sessionId: String,
        setChargingProfile: SetChargingProfile,
    ): ChargingProfileResponse

    suspend fun deleteChargingProfile(
        sessionId: String,
        responseUrl: URL,
    ): ChargingProfileResponse
}
