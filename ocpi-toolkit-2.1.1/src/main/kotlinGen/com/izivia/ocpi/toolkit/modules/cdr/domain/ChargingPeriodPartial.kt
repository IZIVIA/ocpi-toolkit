//  ----------
//  - WARNING -
//  ----------
//  This code is generated AND MUST NOT BE EDITED
//  ----------
package com.izivia.ocpi.toolkit.modules.cdr.domain

import java.time.Instant
import kotlin.String
import kotlin.collections.List

/**
 * Partial representation of [com.izivia.ocpi.toolkit.modules.cdr.domain.ChargingPeriod]
 */
public data class ChargingPeriodPartial(
  public val start_date_time: Instant?,
  public val dimension: List<CdrDimensionPartial>?,
  public val tariff_id: String?,
)

public fun ChargingPeriod.toPartial(): ChargingPeriodPartial {
   return ChargingPeriodPartial(
     start_date_time = start_date_time,
    dimension = dimension.toPartial(),
    tariff_id = tariff_id
   )
}

public fun List<ChargingPeriod>.toPartial(): List<ChargingPeriodPartial> {
   return mapNotNull { it.toPartial() }
}
