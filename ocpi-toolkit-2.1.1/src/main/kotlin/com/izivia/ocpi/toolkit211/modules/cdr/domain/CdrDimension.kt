package com.izivia.ocpi.toolkit211.modules.cdr.domain

import com.izivia.ocpi.toolkit.annotations.Partial
import java.math.BigDecimal

@Partial
data class CdrDimension(
    val type: CdrDimensionType,
    val volume: BigDecimal,
)
