package ocpi.common

import java.math.BigDecimal

/**
 * Key-value pairs (enum + amount) of waste and carbon dioxide emittion per kWh.
 * @property source The category of this value.
 * @property amount Amount of this portion in g/kWh.
 */
data class EnvironmentalImpact(
    val source: EnvironmentalImpactCategory,
    val amount: BigDecimal
)