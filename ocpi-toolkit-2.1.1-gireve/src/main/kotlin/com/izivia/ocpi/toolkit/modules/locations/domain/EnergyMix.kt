package com.izivia.ocpi.toolkit.modules.locations.domain

import com.izivia.ocpi.toolkit.annotations.Partial

/**
 * This type is used to specify the energy mix and environmental impact of the supplied energy at a location or in a
 * tariff.
 *
 * @property is_green_energy True if 100% from regenerative sources. (CO2 and nuclear waste is zero)
 * @property energy_sources Key-value pairs (enum + percentage) of energy sources of this location's tariff.
 * @property environ_impact Key-value pairs (enum + percentage) of nuclear waste and CO2 exhaust of this location's
 * tariff.
 * @property supplier_name (max-length=64) Name of the energy supplier, delivering the energy for this location or
 * tariff. **
 * @property energy_product_name (max-length=64) Name of the energy suppliers product/tariff plan used at this
 * location. **
 *
 * ** These fields can be used to look-up energy qualification or to show it directly to the customer (for well-known
 * brands like Greenpeace Energy, etc.)
 */
@Partial
data class EnergyMix(
    val is_green_energy: Boolean,
    val energy_sources: List<EnergySource>?,
    val environ_impact: List<EnvironmentalImpact>?,
    val supplier_name: String?,
    val energy_product_name: String?
)