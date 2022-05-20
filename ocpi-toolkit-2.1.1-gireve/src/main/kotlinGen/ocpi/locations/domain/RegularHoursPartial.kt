//  ----------
//  - WARNING -
//  ----------
//  This code is generated AND MUST NOT BE EDITED
//  ----------
package ocpi.locations.domain

/**
 * Partial representation of [ocpi.locations.domain.RegularHours]
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
