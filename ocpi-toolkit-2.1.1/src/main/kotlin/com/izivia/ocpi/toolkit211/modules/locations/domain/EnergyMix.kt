package com.izivia.ocpi.toolkit211.modules.locations.domain

import com.izivia.ocpi.toolkit.annotations.Partial

@Partial
data class EnergyMix(
    val isGreenEnergy: Boolean,
    val energySources: List<EnergySource>?,
    val environImpact: List<EnvironmentalImpact>?,
    val supplierName: String?,
    val energyProductName: String?,
)
