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
public data class ExceptionalPeriodPartial(
  public val period_begin: Instant?,
  public val period_end: Instant?,
)

public fun ExceptionalPeriod.toPartial(): ExceptionalPeriodPartial {
   return ExceptionalPeriodPartial(
     period_begin = period_begin,
    period_end = period_end
   )
}

public fun List<ExceptionalPeriod>.toPartial(): List<ExceptionalPeriodPartial> {
   return mapNotNull { it.toPartial() }
}
