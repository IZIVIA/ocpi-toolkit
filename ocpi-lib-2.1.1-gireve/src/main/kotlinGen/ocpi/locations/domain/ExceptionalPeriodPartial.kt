//  ----------
//  - WARNING -
//  ----------
//  This code is generated AND MUST NOT BE EDITED
//  ----------
package ocpi.locations.domain

import java.time.Instant

/**
 * Partial representation of [ocpi.locations.domain.ExceptionalPeriod]
 */
data class ExceptionalPeriodPartial(
  val period_begin: Instant?,
  val period_end: Instant?,
)

fun ExceptionalPeriod.toPartial(): ExceptionalPeriodPartial {
   return ExceptionalPeriodPartial(
     period_begin = period_begin,
    period_end = period_end
   )
}

fun List<ExceptionalPeriod>.toPartial(): List<ExceptionalPeriodPartial> {
   return mapNotNull { it.toPartial() }
}
