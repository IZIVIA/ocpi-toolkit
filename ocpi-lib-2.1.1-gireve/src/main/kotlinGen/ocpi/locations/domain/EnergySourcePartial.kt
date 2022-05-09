//  ----------
//  - WARNING -
//  ----------
//  This code is generated AND MUST NOT BE EDITED
//  ----------
package ocpi.locations.domain

import java.math.BigDecimal

/**
 * Partial representation of [ocpi.locations.domain.EnergySource]
 */
data class EnergySourcePartial(
  val source: EnergySourceCategory?,
  val percentage: BigDecimal?,
)

fun EnergySource.toPartial(): EnergySourcePartial {
   return EnergySourcePartial(
     source = source,
    percentage = percentage
   )
}

fun List<EnergySource>.toPartial(): List<EnergySourcePartial> {
   return mapNotNull { it.toPartial() }
}
