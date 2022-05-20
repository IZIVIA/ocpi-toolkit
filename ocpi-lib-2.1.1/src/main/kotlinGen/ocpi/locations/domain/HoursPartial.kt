//  ----------
//  - WARNING -
//  ----------
//  This code is generated AND MUST NOT BE EDITED
//  ----------
package ocpi.locations.domain

/**
 * Partial representation of [ocpi.locations.domain.Hours]
 */
public data class HoursPartial(
  public val regular_hours: List<RegularHoursPartial>?,
  public val twenty_four_seven: Boolean?,
  public val exceptional_openings: List<ExceptionalPeriodPartial>?,
  public val exceptional_closings: List<ExceptionalPeriodPartial>?,
)

public fun Hours.toPartial(): HoursPartial {
   return HoursPartial(
     regular_hours = regular_hours?.toPartial(),
    twenty_four_seven = twenty_four_seven,
    exceptional_openings = exceptional_openings?.toPartial(),
    exceptional_closings = exceptional_closings?.toPartial()
   )
}

public fun List<Hours>.toPartial(): List<HoursPartial> {
   return mapNotNull { it.toPartial() }
}
