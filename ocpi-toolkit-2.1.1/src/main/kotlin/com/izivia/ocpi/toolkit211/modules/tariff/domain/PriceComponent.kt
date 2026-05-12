package com.izivia.ocpi.toolkit211.modules.tariff.domain

import com.izivia.ocpi.toolkit.annotations.Partial
import java.math.BigDecimal

@Partial
data class PriceComponent(
    val type: TariffDimensionType,
    val price: BigDecimal,
    val vat: BigDecimal? = null,
    val stepSize: Int,
)
