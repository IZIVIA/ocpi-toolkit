package com.izivia.ocpi.toolkit211.modules.cdr.domain

import com.izivia.ocpi.toolkit.annotations.Partial
import com.izivia.ocpi.toolkit211.common.CiString
import java.time.Instant

@Partial
data class ChargingPeriod(
    val startDateTime: Instant,
    val dimensions: List<CdrDimension>,
    val tariffId: CiString? = null,
)
