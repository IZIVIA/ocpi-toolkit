//  ----------
//  - WARNING -
//  ----------
//  This code is generated AND MUST NOT BE EDITED
//  ----------
package ocpi.locations.domain

/**
 * Partial representation of [ocpi.locations.domain.Hours]
 */
data class HoursPartial(
  val regular_hours: List<RegularHoursPartial>?,
  val twenty_four_seven: Boolean?,
  val exceptional_openings: List<ExceptionalPeriodPartial>?,
  val exceptional_closings: List<ExceptionalPeriodPartial>?,
)

fun Hours.toPartial(): HoursPartial {
   return HoursPartial(
     regular_hours = regular_hours?.toPartial(),
    twenty_four_seven = twenty_four_seven,
    exceptional_openings = exceptional_openings.toPartial(),
    exceptional_closings = exceptional_closings.toPartial()
   )
}

fun List<Hours>.toPartial(): List<HoursPartial> {
   return mapNotNull { it.toPartial() }
}
