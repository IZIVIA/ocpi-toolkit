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
public data class EnergySourcePartial(
  public val source: EnergySourceCategory?,
  public val percentage: BigDecimal?,
)

public fun EnergySource.toPartial(): EnergySourcePartial {
   return EnergySourcePartial(
     source = source,
    percentage = percentage
   )
}

public fun List<EnergySource>.toPartial(): List<EnergySourcePartial> {
   return mapNotNull { it.toPartial() }
}
