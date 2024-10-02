package com.izivia.ocpi.toolkit.modules.tariff.domain

import com.izivia.ocpi.toolkit.annotations.Partial
import java.math.BigDecimal

/**
 * @property type Type of tariff dimension
 * @property price Price per unit (excl. VAT) for this tariff dimension.
 * @property vat Applicable VAT percentage for this tariff dimension. If omitted, no VAT
 * is applicable. Not providing a VAT is different from 0% VAT, which would
 * be a value of 0.0 here.
 * @property stepSize Minimum amount to be billed. This unit will be billed in this step_size
 * blocks. Amounts that are less then this step_size are rounded up to
 * the given step_size. For example: if type is TIME and step_size
 * has a value of 300, then time will be billed in blocks of 5 minutes. If 6
 * minutes were used, 10 minutes (2 blocks of step_size) will be billed.
 */

@Partial
data class PriceComponent(
    val type: TariffDimensionType,
    val price: BigDecimal,
    val vat: BigDecimal? = null,
    val stepSize: Int,
)
