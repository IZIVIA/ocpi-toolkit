package com.izivia.ocpi.toolkit211.modules.locations.domain

import com.izivia.ocpi.toolkit.annotations.Partial
import java.math.BigDecimal

@Partial
data class EnvironmentalImpact(
    val category: EnvironmentalImpactCategory,
    val amount: BigDecimal,
)
