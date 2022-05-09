//  ----------
//  - WARNING -
//  ----------
//  This code is generated AND MUST NOT BE EDITED
//  ----------
package ocpi.locations.domain

import java.math.BigDecimal

/**
 * Partial representation of [ocpi.locations.domain.EnvironmentalImpact]
 */
data class EnvironmentalImpactPartial(
  val source: EnvironmentalImpactCategory?,
  val amount: BigDecimal?,
)

fun EnvironmentalImpact.toPartial(): EnvironmentalImpactPartial {
   return EnvironmentalImpactPartial(
     source = source,
    amount = amount
   )
}

fun List<EnvironmentalImpact>.toPartial(): List<EnvironmentalImpactPartial> {
   return mapNotNull { it.toPartial() }
}
