//  ----------
//  - WARNING -
//  ----------
//  This code is generated AND MUST NOT BE EDITED
//  ----------
package com.izivia.ocpi.toolkit.modules.locations.domain

import java.math.BigDecimal
import kotlin.collections.List

/**
 * Partial representation of [com.izivia.ocpi.toolkit.modules.locations.domain.EnvironmentalImpact]
 */
public data class EnvironmentalImpactPartial(
  public val source: EnvironmentalImpactCategory?,
  public val amount: BigDecimal?,
)

public fun EnvironmentalImpact.toPartial(): EnvironmentalImpactPartial {
   return EnvironmentalImpactPartial(
     source = source,
    amount = amount
   )
}

public fun List<EnvironmentalImpact>.toPartial(): List<EnvironmentalImpactPartial> {
   return mapNotNull { it.toPartial() }
}
