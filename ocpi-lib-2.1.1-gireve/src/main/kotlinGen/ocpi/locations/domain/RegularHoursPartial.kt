//  ----------
//  - WARNING -
//  ----------
//  This code is generated AND MUST NOT BE EDITED
//  ----------
package ocpi.locations.domain

/**
 * Partial representation of [ocpi.locations.domain.RegularHours]
 */
data class RegularHoursPartial(
  val weekday: Int?,
  val period_begin: String?,
  val period_end: String?,
)

fun RegularHours.toPartial(): RegularHoursPartial {
   return RegularHoursPartial(
     weekday = weekday,
    period_begin = period_begin,
    period_end = period_end
   )
}

fun List<RegularHours>.toPartial(): List<RegularHoursPartial> {
   return mapNotNull { it.toPartial() }
}
