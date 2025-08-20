package com.izivia.ocpi.toolkit.modules.chargingProfiles.services

import com.izivia.ocpi.toolkit.common.CiString
import com.izivia.ocpi.toolkit.common.validation.validate
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
        validate {
            result.validate()
        }

        service
            .postCallbackActiveChargingProfile(requestId, result)
    }

    override suspend fun postCallbackChargingProfile(
        requestId: String,
        result: ChargingProfileResult,
    ) {
        validate {
            result.validate()
        }

        service
            .postCallbackChargingProfile(requestId, result)
    }

    override suspend fun postCallbackClearProfile(
        requestId: String,
        result: ClearProfileResult,
    ) {
        validate {
            result.validate()
        }

        service
            .postCallbackClearProfile(requestId, result)
    }

    override suspend fun putActiveChargingProfile(
        sessionId: CiString,
        activeChargingProfile: ActiveChargingProfile,
    ) {
        validate {
            activeChargingProfile.validate()
        }

        service
            .putActiveChargingProfile(sessionId, activeChargingProfile)
    }
}
