package com.izivia.ocpi.toolkit.modules.chargingProfiles.services

import com.izivia.ocpi.toolkit.common.CiString
import com.izivia.ocpi.toolkit.common.OcpiResponseBody
import com.izivia.ocpi.toolkit.common.validation.validate
import com.izivia.ocpi.toolkit.modules.chargingProfiles.ChargingProfilesScspInterface
import com.izivia.ocpi.toolkit.modules.chargingProfiles.domain.ActiveChargingProfile
import com.izivia.ocpi.toolkit.modules.chargingProfiles.domain.ActiveChargingProfileResult
import com.izivia.ocpi.toolkit.modules.chargingProfiles.domain.ChargingProfileResult
import com.izivia.ocpi.toolkit.modules.chargingProfiles.domain.ClearProfileResult
import com.izivia.ocpi.toolkit.modules.chargingProfiles.repositories.ChargingProfilesScspRepository

open class ChargingProfilesScspService(
    private val repository: ChargingProfilesScspRepository,
) : ChargingProfilesScspInterface {
    override suspend fun postCallbackActiveChargingProfile(
        requestId: String,
        result: ActiveChargingProfileResult,
    ): OcpiResponseBody<Any> = OcpiResponseBody.of {
        validate {
            result.validate()
        }

        repository
            .postCallbackActiveChargingProfile(requestId, result)
    }

    override suspend fun postCallbackChargingProfile(
        requestId: String,
        result: ChargingProfileResult,
    ): OcpiResponseBody<Any> = OcpiResponseBody.of {
        validate {
            result.validate()
        }

        repository
            .postCallbackChargingProfile(requestId, result)
    }

    override suspend fun postCallbackClearProfile(
        requestId: String,
        result: ClearProfileResult,
    ): OcpiResponseBody<Any> = OcpiResponseBody.of {
        validate {
            result.validate()
        }

        repository
            .postCallbackClearProfile(requestId, result)
    }

    override suspend fun putActiveChargingProfile(
        sessionId: CiString,
        activeChargingProfile: ActiveChargingProfile,
    ): OcpiResponseBody<Any> = OcpiResponseBody.of {
        validate {
            activeChargingProfile.validate()
        }

        repository
            .putActiveChargingProfile(sessionId, activeChargingProfile)
    }
}
