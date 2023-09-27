package com.izivia.ocpi.toolkit.modules.types

import com.izivia.ocpi.toolkit.annotations.Partial
import java.math.BigDecimal

/**
 * @property excl_vat Price/Cost excluding VAT.
 * @property incl_vat Price/Cost including VAT.
 */

@Partial
data class Price(
    val excl_vat: BigDecimal,
    val incl_vat: BigDecimal? = null
)
