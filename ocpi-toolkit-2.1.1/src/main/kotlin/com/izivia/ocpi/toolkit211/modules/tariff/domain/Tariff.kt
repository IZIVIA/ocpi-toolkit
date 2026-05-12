package com.izivia.ocpi.toolkit211.modules.tariff.domain

import com.izivia.ocpi.toolkit.annotations.Partial
import com.izivia.ocpi.toolkit211.common.CiString
import com.izivia.ocpi.toolkit211.common.URL
import com.izivia.ocpi.toolkit211.modules.locations.domain.EnergyMix
import com.izivia.ocpi.toolkit211.modules.types.DisplayText
import java.time.Instant

@Partial
data class Tariff(
    val id: CiString,
    val currency: String,
    val tariffAltText: List<DisplayText>? = null,
    val tariffAltUrl: URL? = null,
    val elements: List<TariffElement>,
    val energyMix: EnergyMix? = null,
    val lastUpdated: Instant,
)
