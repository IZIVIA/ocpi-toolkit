package com.izivia.ocpi.toolkit211.modules.tariff.domain

import com.izivia.ocpi.toolkit.annotations.Partial

@Partial
data class TariffElement(
    val priceComponents: List<PriceComponent>,
    val restrictions: TariffRestrictions? = null,
)
