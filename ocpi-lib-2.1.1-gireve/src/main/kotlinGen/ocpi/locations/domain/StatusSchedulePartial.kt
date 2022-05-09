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
data class StatusSchedulePartial(
  val period_begin: Instant?,
  val period_end: Instant?,
  val status: Status?,
)

fun StatusSchedule.toPartial(): StatusSchedulePartial {
   return StatusSchedulePartial(
     period_begin = period_begin,
    period_end = period_end,
    status = status
   )
}

fun List<StatusSchedule>.toPartial(): List<StatusSchedulePartial> {
   return mapNotNull { it.toPartial() }
}
