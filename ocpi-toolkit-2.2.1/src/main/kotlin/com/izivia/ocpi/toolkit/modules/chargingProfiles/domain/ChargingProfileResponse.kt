package com.izivia.ocpi.toolkit.modules.chargingProfiles.domain

data class ChargingProfileResponse(
    val result: ChargingProfileResponseType,
    val timeout: Int,
)
