package com.izivia.ocpi.toolkit.modules.chargingProfiles.services

import com.izivia.ocpi.toolkit.common.CiString
import com.izivia.ocpi.toolkit.common.URL
import com.izivia.ocpi.toolkit.common.validation.validateInt
import com.izivia.ocpi.toolkit.common.validation.validateLength
import com.izivia.ocpi.toolkit.common.validation.validateParams
import com.izivia.ocpi.toolkit.modules.chargingProfiles.ChargingProfilesCpoInterface
import com.izivia.ocpi.toolkit.modules.chargingProfiles.domain.ChargingProfileResponse
import com.izivia.ocpi.toolkit.modules.chargingProfiles.domain.SetChargingProfile

open class ChargingProfilesCpoValidator(
    private val service: ChargingProfilesCpoInterface,
) : ChargingProfilesCpoInterface {

    override suspend fun getActiveChargingProfile(
        sessionId: CiString,
        duration: Int,
        responseUrl: URL,
    ): ChargingProfileResponse {
        validateParams {
            validateLength("sessionId", sessionId, 36)
            validateInt("duration", duration, 0, null)
            validateLength("response_url", responseUrl, 255)
        }

        return service.getActiveChargingProfile(sessionId, duration, responseUrl)
    }

    override suspend fun putChargingProfile(
        sessionId: CiString,
        setChargingProfile: SetChargingProfile,
    ): ChargingProfileResponse {
        validateParams {
            validateLength("sessionId", sessionId, 36)
            setChargingProfile.validate()
        }

        return service.putChargingProfile(sessionId, setChargingProfile)
    }

    override suspend fun deleteChargingProfile(
        sessionId: CiString,
        responseUrl: URL,
    ): ChargingProfileResponse {
        validateParams {
            validateLength("sessionId", sessionId, 36)
            validateLength("response_url", responseUrl, 255)
        }

        return service.deleteChargingProfile(sessionId, responseUrl)
    }
}
