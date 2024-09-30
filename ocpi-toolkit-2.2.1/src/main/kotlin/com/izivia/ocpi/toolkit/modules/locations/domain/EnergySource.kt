package com.izivia.ocpi.toolkit.modules.locations.domain

import com.izivia.ocpi.toolkit.annotations.Partial
import java.math.BigDecimal

/**
 * Key-value pairs (enum + percentage) of energy sources. All given values should add up to 100 percent per category.
 *
 * @property source The type of energy source.
 * @property percentage Percentage of this source (0-100) in the mix.
 */
@Partial
data class EnergySource(
    val source: EnergySourceCategory,
    val percentage: BigDecimal,
)
