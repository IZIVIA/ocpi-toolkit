package com.izivia.ocpi.toolkit211.modules.tariff.domain

import com.izivia.ocpi.toolkit.annotations.Partial
import java.math.BigDecimal
import java.time.DayOfWeek

@Partial
data class TariffRestrictions(
    val startTime: String? = null,
    val endTime: String? = null,
    val startDate: String? = null,
    val endDate: String? = null,
    val minKwh: BigDecimal? = null,
    val maxKwh: BigDecimal? = null,
    val minPower: BigDecimal? = null,
    val maxPower: BigDecimal? = null,
    val minDuration: Int? = null,
    val maxDuration: Int? = null,
    val dayOfWeek: List<DayOfWeek>? = null,
)
