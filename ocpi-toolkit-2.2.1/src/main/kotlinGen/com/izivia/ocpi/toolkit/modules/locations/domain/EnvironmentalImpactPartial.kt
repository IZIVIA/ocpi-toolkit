//  ----------
//  - WARNING -
//  ----------
//  This code is generated AND MUST NOT BE EDITED
//  ----------
package com.izivia.ocpi.toolkit.modules.locations.domain

import java.math.BigDecimal

/**
 * Partial representation of [com.izivia.ocpi.toolkit.modules.locations.domain.EnvironmentalImpact]
 */
public data class EnvironmentalImpactPartial(
  public val category: EnvironmentalImpactCategory?,
  public val amount: BigDecimal?,
)

public fun EnvironmentalImpact.toPartial(): EnvironmentalImpactPartial {
   return EnvironmentalImpactPartial(
     category = category,
    amount = amount
   )
}

public fun List<EnvironmentalImpact>.toPartial(): List<EnvironmentalImpactPartial> {
   return mapNotNull { it.toPartial() }
}
