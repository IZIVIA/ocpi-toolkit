package com.izivia.ocpi.toolkit.modules.chargingProfiles.services

import com.izivia.ocpi.toolkit.common.CiString
import com.izivia.ocpi.toolkit.common.validation.validateParams
import com.izivia.ocpi.toolkit.modules.chargingProfiles.ChargingProfilesScspInterface
import com.izivia.ocpi.toolkit.modules.chargingProfiles.domain.ActiveChargingProfile
import com.izivia.ocpi.toolkit.modules.chargingProfiles.domain.ActiveChargingProfileResult
import com.izivia.ocpi.toolkit.modules.chargingProfiles.domain.ChargingProfileResult
import com.izivia.ocpi.toolkit.modules.chargingProfiles.domain.ClearProfileResult

open class ChargingProfilesScspValidator(
    private val service: ChargingProfilesScspInterface,
) : ChargingProfilesScspInterface {
    override suspend fun postCallbackActiveChargingProfile(
        requestId: String,
        result: ActiveChargingProfileResult,
    ) {
        validateParams {
            result.validate()
        }

        service
            .postCallbackActiveChargingProfile(requestId, result)
    }

    override suspend fun postCallbackChargingProfile(
        requestId: String,
        result: ChargingProfileResult,
    ) {
        validateParams {
            result.validate()
        }

        service
            .postCallbackChargingProfile(requestId, result)
    }

    override suspend fun postCallbackClearProfile(
        requestId: String,
        result: ClearProfileResult,
    ) {
        validateParams {
            result.validate()
        }

        service
            .postCallbackClearProfile(requestId, result)
    }

    override suspend fun putActiveChargingProfile(
        sessionId: CiString,
        activeChargingProfile: ActiveChargingProfile,
    ) {
        validateParams {
            activeChargingProfile.validate()
        }

        service
            .putActiveChargingProfile(sessionId, activeChargingProfile)
    }
}
