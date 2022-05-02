//  ----------
//  - WARNING -
//  ----------
//  This code is generated AND MUST NOT BE EDITED
//  ----------
package ocpi.locations.domain

import java.time.Instant

/**
 * Partial representation of [ocpi.locations.domain.StatusSchedule]
 */
public data class StatusSchedulePartial(
  public val period_begin: Instant?,
  public val period_end: Instant?,
  public val status: Status?,
)
