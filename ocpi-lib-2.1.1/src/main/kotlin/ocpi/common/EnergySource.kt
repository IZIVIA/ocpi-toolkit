package ocpi.common

import java.math.BigDecimal

/**
 * Key-value pairs (enum + percentage) of energy sources. All given values should add up to 100 percent per category.
 *
 * @property source The type of energy source.
 * @property percentage Percentage of this source (0-100) in the mix.
 */
data class EnergySource(
    val source: EnergySourceCategory,
    val percentage: BigDecimal
)

data class EnergySourcePatch(
    val source: EnergySourceCategory?,
    val percentage: BigDecimal?
)
