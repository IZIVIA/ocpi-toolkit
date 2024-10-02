package com.izivia.ocpi.toolkit.modules.chargingProfiles.domain

import java.math.BigDecimal

data class ChargingProfilePeriod(
    val startPeriod: Int,
    val limit: BigDecimal,
)
