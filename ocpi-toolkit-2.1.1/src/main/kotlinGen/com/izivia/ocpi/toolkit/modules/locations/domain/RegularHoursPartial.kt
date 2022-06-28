//  ----------
//  - WARNING -
//  ----------
//  This code is generated AND MUST NOT BE EDITED
//  ----------
package com.izivia.ocpi.toolkit.modules.locations.domain

import kotlin.Int
import kotlin.String
import kotlin.collections.List

/**
 * Partial representation of [com.izivia.ocpi.toolkit.modules.locations.domain.RegularHours]
 */
public data class RegularHoursPartial(
  public val weekday: Int?,
  public val period_begin: String?,
  public val period_end: String?,
)

public fun RegularHours.toPartial(): RegularHoursPartial {
   return RegularHoursPartial(
     weekday = weekday,
    period_begin = period_begin,
    period_end = period_end
   )
}

public fun List<RegularHours>.toPartial(): List<RegularHoursPartial> {
   return mapNotNull { it.toPartial() }
}
