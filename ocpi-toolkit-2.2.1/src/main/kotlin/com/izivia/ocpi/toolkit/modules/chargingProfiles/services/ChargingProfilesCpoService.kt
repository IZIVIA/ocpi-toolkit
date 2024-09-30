package com.izivia.ocpi.toolkit.modules.chargingProfiles.services

import com.izivia.ocpi.toolkit.common.CiString
import com.izivia.ocpi.toolkit.common.OcpiResponseBody
import com.izivia.ocpi.toolkit.common.URL
import com.izivia.ocpi.toolkit.common.validation.validate
import com.izivia.ocpi.toolkit.common.validation.validateInt
import com.izivia.ocpi.toolkit.common.validation.validateLength
import com.izivia.ocpi.toolkit.modules.chargingProfiles.ChargingProfilesCpoInterface
import com.izivia.ocpi.toolkit.modules.chargingProfiles.domain.ChargingProfileResponse
import com.izivia.ocpi.toolkit.modules.chargingProfiles.domain.SetChargingProfile

open class ChargingProfilesCpoService(
    private val service: ChargingProfilesChargePointService,
) : ChargingProfilesCpoInterface {

    override suspend fun getActiveChargingProfile(
        sessionId: CiString,
        duration: Int,
        responseUrl: URL,
    ): OcpiResponseBody<ChargingProfileResponse> =
        validate {
            validateLength("sessionId", sessionId, 36)
            validateInt("duration", duration, 0, null)
            validateLength("response_url", responseUrl, 255)
        }
            .let { OcpiResponseBody.success(service.getActiveChargingProfile(sessionId, duration, responseUrl)) }

    override suspend fun putChargingProfile(
        sessionId: CiString,
        setChargingProfile: SetChargingProfile,
    ): OcpiResponseBody<ChargingProfileResponse> =
        validate {
            validateLength("sessionId", sessionId, 36)
            setChargingProfile.validate()
        }
            .let { OcpiResponseBody.success(service.putChargingProfile(sessionId, setChargingProfile)) }

    override suspend fun deleteChargingProfile(
        sessionId: CiString,
        responseUrl: URL,
    ): OcpiResponseBody<ChargingProfileResponse> =
        validate {
            validateLength("sessionId", sessionId, 36)
            validateLength("response_url", responseUrl, 255)
        }
            .let { OcpiResponseBody.success(service.deleteChargingProfile(sessionId, responseUrl)) }
}
