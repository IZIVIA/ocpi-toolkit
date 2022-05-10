//  ----------
//  - WARNING -
//  ----------
//  This code is generated AND MUST NOT BE EDITED
//  ----------
package ocpi.locations.domain

import kotlin.Boolean
import kotlin.collections.List

/**
 * Partial representation of [ocpi.locations.domain.Hours]
 */
public data class HoursPartial(
  public val regular_hours: List<RegularHours>?,
  public val twenty_four_seven: Boolean?,
  public val exceptional_openings: List<ExceptionalPeriod>?,
  public val exceptional_closings: List<ExceptionalPeriod>?,
)

public fun Hours.toPartial(): HoursPartial {
   return HoursPartial(
     regular_hours = regular_hours,
    twenty_four_seven = twenty_four_seven,
    exceptional_openings = exceptional_openings,
    exceptional_closings = exceptional_closings
   )
}

public fun List<Hours>.toPartial(): List<HoursPartial> {
   return mapNotNull { it.toPartial() }
}
