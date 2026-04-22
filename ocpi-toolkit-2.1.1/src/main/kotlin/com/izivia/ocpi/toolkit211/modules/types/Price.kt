package com.izivia.ocpi.toolkit211.modules.types

import com.izivia.ocpi.toolkit.annotations.Partial
import java.math.BigDecimal

/**
 * @property exclVat Price/Cost excluding VAT.
 * @property inclVat Price/Cost including VAT.
 */
@Partial
data class Price(
    val exclVat: BigDecimal,
    val inclVat: BigDecimal? = null,
)
