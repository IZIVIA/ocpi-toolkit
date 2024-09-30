package com.izivia.ocpi.toolkit.modules.chargingProfiles.domain

import java.time.Instant

data class ActiveChargingProfile(
    val startDateTime: Instant,
    val chargingProfile: ChargingProfile,
)
