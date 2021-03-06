//  ----------
//  - WARNING -
//  ----------
//  This code is generated AND MUST NOT BE EDITED
//  ----------
package com.izivia.ocpi.toolkit.modules.types

import java.math.BigDecimal
import kotlin.collections.List

/**
 * Partial representation of [com.izivia.ocpi.toolkit.modules.types.Price]
 */
public data class PricePartial(
  public val excl_vat: BigDecimal?,
  public val incl_vat: BigDecimal?,
)

public fun Price.toPartial(): PricePartial {
   return PricePartial(
     excl_vat = excl_vat,
    incl_vat = incl_vat
   )
}

public fun List<Price>.toPartial(): List<PricePartial> {
   return mapNotNull { it.toPartial() }
}
