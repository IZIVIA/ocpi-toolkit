package ocpi.common

/**
 * This type is used to specify the energy mix and environmental impact of the supplied energy at a location or in a
 * tariff.
 *
 * @property isGreenEnergy True if 100% from regenerative sources. (CO2 and nuclear waste is zero)
 * @property energySources Key-value pairs (enum + percentage) of energy sources of this location's tariff.
 * @property environImpact Key-value pairs (enum + percentage) of nuclear waste and CO2 exhaust of this location's
 * tariff.
 * @property supplierName Name of the energy supplier, delivering the energy for this location or tariff. **
 * @property energyProductName Name of the energy suppliers product/tariff plan used at this location. **
 *
 * ** These fields can be used to look-up energy qualification or to show it directly to the customer (for well-known
 * brands like Greenpeace Energy, etc.)
 */
data class EnergyMix(
    val isGreenEnergy: Boolean,
    val energySources: List<EnergySource>,
    val environImpact: List<EnvironmentalImpact>,
    val supplierName: String?,
    val energyProductName: String?
)
