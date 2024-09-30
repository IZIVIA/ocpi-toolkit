package com.izivia.ocpi.toolkit.modules.chargingProfiles.domain

import com.izivia.ocpi.toolkit.common.URL

data class SetChargingProfile(
    val chargingProfile: ChargingProfile,
    val responseUrl: URL,
)
