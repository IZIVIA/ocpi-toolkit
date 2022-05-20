package com.izivia.ocpi.toolkit.modules.locations.domain

import com.izivia.ocpi.toolkit.annotations.Partial
import java.math.BigDecimal

/**
 * Key-value pairs (enum + amount) of waste and carbon dioxide emittion per kWh.
 * @property source The category of this value.
 * @property amount Amount of this portion in g/kWh.
 */
@Partial
data class EnvironmentalImpact(
    val source: EnvironmentalImpactCategory,
    val amount: BigDecimal
)