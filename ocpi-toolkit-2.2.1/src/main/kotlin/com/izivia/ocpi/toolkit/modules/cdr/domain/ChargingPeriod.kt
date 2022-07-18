package com.izivia.ocpi.toolkit.modules.cdr.domain

import com.izivia.ocpi.toolkit.annotations.Partial
import com.izivia.ocpi.toolkit.common.CiString
import java.time.Instant

/**
 * A Charging Period consists of a start timestamp and a list of possible values that influence this period, for example: amount of
energy charged this period, maximum current during this period etc.
 *@property start_date_time Start timestamp of the charging period. A period ends when the next period
starts. The last period ends when the session ends.
 *@property dimensions List of relevant values for this charging period.
 *@property tariff_id Unique identifier of the Tariff that is relevant for this Charging Period. If not
provided, no Tariff is relevant during this period.
 *
 */
@Partial
data class ChargingPeriod(
    val start_date_time: Instant,
    val dimensions: List<CdrDimension>,
    val tariff_id: CiString? = null
)

