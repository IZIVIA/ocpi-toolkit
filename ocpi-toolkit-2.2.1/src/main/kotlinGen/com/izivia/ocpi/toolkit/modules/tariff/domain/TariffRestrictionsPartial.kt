//  ----------
//  - WARNING -
//  ----------
//  This code is generated AND MUST NOT BE EDITED
//  ----------
package com.izivia.ocpi.toolkit.modules.tariff.domain

import java.math.BigDecimal
import java.time.DayOfWeek
import kotlin.Int
import kotlin.String
import kotlin.collections.List

/**
 * Partial representation of [com.izivia.ocpi.toolkit.modules.tariff.domain.TariffRestrictions]
 */
public data class TariffRestrictionsPartial(
  public val start_time: String?,
  public val end_time: String?,
  public val start_date: String?,
  public val end_date: String?,
  public val min_kwh: BigDecimal?,
  public val max_kwh: BigDecimal?,
  public val min_current: BigDecimal?,
  public val max_current: BigDecimal?,
  public val min_power: BigDecimal?,
  public val max_power: BigDecimal?,
  public val min_duration: Int?,
  public val max_duration: Int?,
  public val day_of_week: List<DayOfWeek>?,
  public val reservation: ReservationRestrictionType?,
)

public fun TariffRestrictions.toPartial(): TariffRestrictionsPartial {
   return TariffRestrictionsPartial(
     start_time = start_time,
    end_time = end_time,
    start_date = start_date,
    end_date = end_date,
    min_kwh = min_kwh,
    max_kwh = max_kwh,
    min_current = min_current,
    max_current = max_current,
    min_power = min_power,
    max_power = max_power,
    min_duration = min_duration,
    max_duration = max_duration,
    day_of_week = day_of_week,
    reservation = reservation
   )
}

public fun List<TariffRestrictions>.toPartial(): List<TariffRestrictionsPartial> {
   return mapNotNull { it.toPartial() }
}
