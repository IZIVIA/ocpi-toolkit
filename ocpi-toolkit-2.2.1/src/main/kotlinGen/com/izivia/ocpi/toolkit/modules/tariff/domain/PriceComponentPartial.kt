//  ----------
//  - WARNING -
//  ----------
//  This code is generated AND MUST NOT BE EDITED
//  ----------
package com.izivia.ocpi.toolkit.modules.tariff.domain

import java.math.BigDecimal
import kotlin.Int
import kotlin.collections.List

/**
 * Partial representation of [com.izivia.ocpi.toolkit.modules.tariff.domain.PriceComponent]
 */
public data class PriceComponentPartial(
  public val type: TariffDimensionType?,
  public val price: BigDecimal?,
  public val vat: BigDecimal?,
  public val step_size: Int?,
)

public fun PriceComponent.toPartial(): PriceComponentPartial {
   return PriceComponentPartial(
     type = type,
    price = price,
    vat = vat,
    step_size = step_size
   )
}

public fun List<PriceComponent>.toPartial(): List<PriceComponentPartial> {
   return mapNotNull { it.toPartial() }
}
