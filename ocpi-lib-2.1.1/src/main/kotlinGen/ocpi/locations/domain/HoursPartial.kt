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
  public val regular_hours: List<RegularHoursPartial>?,
  public val twenty_four_seven: Boolean?,
  public val exceptional_openings: List<ExceptionalPeriodPartial>?,
  public val exceptional_closings: List<ExceptionalPeriodPartial>?,
)
