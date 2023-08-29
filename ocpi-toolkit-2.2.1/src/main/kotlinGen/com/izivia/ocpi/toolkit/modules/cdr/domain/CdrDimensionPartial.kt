//  ----------
//  - WARNING -
//  ----------
//  This code is generated AND MUST NOT BE EDITED
//  ----------
package com.izivia.ocpi.toolkit.modules.cdr.domain

import kotlin.Int
import kotlin.collections.List

/**
 * Partial representation of [com.izivia.ocpi.toolkit.modules.cdr.domain.CdrDimension]
 */
public data class CdrDimensionPartial(
  public val type: CdrDimensionType?,
  public val volume: Int?,
)

public fun CdrDimension.toPartial(): CdrDimensionPartial {
   return CdrDimensionPartial(
     type = type,
    volume = volume
   )
}

public fun List<CdrDimension>.toPartial(): List<CdrDimensionPartial> {
   return mapNotNull { it.toPartial() }
}
