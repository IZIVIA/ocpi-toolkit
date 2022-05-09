//  ----------
//  - WARNING -
//  ----------
//  This code is generated AND MUST NOT BE EDITED
//  ----------
package ocpi.locations.domain

/**
 * Partial representation of [ocpi.locations.domain.EnergyMix]
 */
data class EnergyMixPartial(
  val is_green_energy: Boolean?,
  val energy_sources: List<EnergySourcePartial>?,
  val environ_impact: List<EnvironmentalImpactPartial>?,
  val supplier_name: String?,
  val energy_product_name: String?,
)

fun EnergyMix.toPartial(): EnergyMixPartial {
   return EnergyMixPartial(
     is_green_energy = is_green_energy,
    energy_sources = energy_sources.toPartial(),
    environ_impact = environ_impact.toPartial(),
    supplier_name = supplier_name,
    energy_product_name = energy_product_name
   )
}

fun List<EnergyMix>.toPartial(): List<EnergyMixPartial> {
   return mapNotNull { it.toPartial() }
}
