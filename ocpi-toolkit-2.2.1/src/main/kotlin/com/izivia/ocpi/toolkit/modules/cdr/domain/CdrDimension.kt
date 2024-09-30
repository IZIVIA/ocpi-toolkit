package com.izivia.ocpi.toolkit.modules.cdr.domain

import com.izivia.ocpi.toolkit.annotations.Partial
import java.math.BigDecimal

/**
 * @property type : Type of CDR dimension.
 * @property volume : Volume of the dimension consumed, measured according to the dimension type.
 */
@Partial
data class CdrDimension(
    val type: CdrDimensionType,
    val volume: BigDecimal,
)
