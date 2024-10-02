package com.izivia.ocpi.toolkit.modules.chargingProfiles.domain

data class ActiveChargingProfileResult(
    val result: ChargingProfileResultType,
    val profile: ActiveChargingProfile? = null,
)
