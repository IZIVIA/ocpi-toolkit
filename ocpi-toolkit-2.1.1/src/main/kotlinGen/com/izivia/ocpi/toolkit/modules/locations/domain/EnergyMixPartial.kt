//  ----------
//  - WARNING -
//  ----------
//  This code is generated AND MUST NOT BE EDITED
//  ----------
package com.izivia.ocpi.toolkit.modules.locations.domain

import kotlin.Boolean
import kotlin.String
import kotlin.collections.List

/**
 * Partial representation of [com.izivia.ocpi.toolkit.modules.locations.domain.EnergyMix]
 */
public data class EnergyMixPartial(
  public val is_green_energy: Boolean?,
  public val energy_sources: List<EnergySourcePartial>?,
  public val environ_impact: List<EnvironmentalImpactPartial>?,
  public val supplier_name: String?,
  public val energy_product_name: String?,
)

public fun EnergyMix.toPartial(): EnergyMixPartial {
   return EnergyMixPartial(
     is_green_energy = is_green_energy,
    energy_sources = energy_sources?.toPartial(),
    environ_impact = environ_impact?.toPartial(),
    supplier_name = supplier_name,
    energy_product_name = energy_product_name
   )
}

public fun List<EnergyMix>.toPartial(): List<EnergyMixPartial> {
   return mapNotNull { it.toPartial() }
}
