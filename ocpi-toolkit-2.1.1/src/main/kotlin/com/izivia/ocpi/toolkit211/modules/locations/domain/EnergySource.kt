package com.izivia.ocpi.toolkit211.modules.locations.domain

import com.izivia.ocpi.toolkit.annotations.Partial
import java.math.BigDecimal

@Partial
data class EnergySource(
    val source: EnergySourceCategory,
    val percentage: BigDecimal,
)
