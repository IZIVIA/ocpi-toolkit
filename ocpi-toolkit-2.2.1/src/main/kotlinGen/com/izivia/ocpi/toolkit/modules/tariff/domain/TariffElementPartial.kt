//  ----------
//  - WARNING -
//  ----------
//  This code is generated AND MUST NOT BE EDITED
//  ----------
package com.izivia.ocpi.toolkit.modules.tariff.domain

import kotlin.collections.List

/**
 * Partial representation of [com.izivia.ocpi.toolkit.modules.tariff.domain.TariffElement]
 */
public data class TariffElementPartial(
  public val price_components: List<PriceComponentPartial>?,
  public val restrictions: TariffRestrictionsPartial?,
)

public fun TariffElement.toPartial(): TariffElementPartial {
   return TariffElementPartial(
     price_components = price_components.toPartial(),
    restrictions = restrictions?.toPartial()
   )
}

public fun List<TariffElement>.toPartial(): List<TariffElementPartial> {
   return mapNotNull { it.toPartial() }
}
