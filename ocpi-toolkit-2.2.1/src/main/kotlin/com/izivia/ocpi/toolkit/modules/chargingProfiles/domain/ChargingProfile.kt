package com.izivia.ocpi.toolkit.modules.chargingProfiles.domain

import java.math.BigDecimal
import java.time.Instant

data class ChargingProfile(
    val startDateTime: Instant? = null,
    val duration: Int? = null,
    val chargingRateUnit: ChargingRateUnit,
    val minChargingRate: BigDecimal?,
    val chargingProfilePeriod: List<ChargingProfilePeriod>?,
)
