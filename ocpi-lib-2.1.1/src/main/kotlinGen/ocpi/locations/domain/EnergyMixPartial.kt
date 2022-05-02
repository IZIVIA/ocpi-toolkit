//  ----------
//  - WARNING -
//  ----------
//  This code is generated AND MUST NOT BE EDITED
//  ----------
package ocpi.locations.domain

import kotlin.Boolean
import kotlin.String
import kotlin.collections.List

/**
 * Partial representation of [ocpi.locations.domain.EnergyMix]
 */
public data class EnergyMixPartial(
  public val is_green_energy: Boolean?,
  public val energy_sources: List<EnergySourcePartial>?,
  public val environ_impact: List<EnvironmentalImpactPartial>?,
  public val supplier_name: String?,
  public val energy_product_name: String?,
)
