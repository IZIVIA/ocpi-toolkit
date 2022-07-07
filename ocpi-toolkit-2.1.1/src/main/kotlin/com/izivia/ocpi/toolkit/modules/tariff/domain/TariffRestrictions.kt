package com.izivia.ocpi.toolkit.modules.tariff.domain

import com.izivia.ocpi.toolkit.annotations.Partial
import java.math.BigDecimal
import java.time.DayOfWeek

/**
 * These restrictions are not for the entire Charging Session. They only describe if and when a TariffElement becomes active or
 * inactive during a Charging Session.
 *
 *@property start_time Start time of day in local time, the time zone is defined in the time_zone field of
 * the Location, for example 13:30, valid from this time of the day. Must be in 24h
 * format with leading zeros. Hour/Minute separator: ":" Regex: ([0-1][0-9]|2[0-3]):[0-5][0-9]
 *
 * @property end_time End time of day in local time, the time zone is defined in the time_zone field of
 * the Location, for example 19:45, valid until this time of the day. Same syntax as
 * start_time. If end_time < start_time then the period wraps around to the next
 * day. To stop at end of the day use: 00:00.
 *
 * @property start_date Start date in local time, the time zone is defined in the time_zone field of the
 * Location, for example: 2015-12-24, valid from this day (inclusive).
 * Regex:([12][0-9]{3})-(0[1-9]|1[0-2])-(0[1-9]|[12][0-9]|3[01])
 *
 * @property end_date End date in local time, the time zone is defined in the time_zone field of the
 * Location, for example: 2015-12-27, valid until this day (exclusive). Same syntax
 * as start_date.
 *
 * @property min_kwh Minimum consumed energy in kWh, for example 20, valid from this amount of
 * energy (inclusive) being used.
 *
 * @property max_kwh Maximum consumed energy in kWh, for example 50, valid until this amount of
 * energy (exclusive) being used.
 *
 * @property min_current  Sum of the minimum current (in Amperes) over all phases, for example 5. When
the EV is charging with more than, or equal to, the defined amount of current
this TariffElement is/becomes active. If the charging current is or becomes lower,
this TariffElement is not or no longer valid and becomes inactive. This describes
NOT the minimum current over the entire Charging Session. This restriction can
make a TariffElement become active when the charging current is above the
defined value, but the TariffElement MUST no longer be active when the
charging current drops below the defined value.
 *
 * @property max_current Sum of the maximum current (in Amperes) over all phases, for example 20.
When the EV is charging with less than the defined amount of current, this
TariffElement becomes/is active. If the charging current is or becomes higher,
this TariffElement is not or no longer valid and becomes inactive. This describes
NOT the maximum current over the entire Charging Session. This restriction can
make a TariffElement become active when the charging current is below this
value, but the TariffElement MUST no longer be active when the charging
current raises above the defined value
 *
 * @property min_power Minimum power in kW, for example 5. When the EV is charging with more than,
or equal to, the defined amount of power, this TariffElement is/becomes active. If
the charging power is or becomes lower, this TariffElement is not or no longer
valid and becomes inactive. This describes NOT the minimum power over the
entire Charging Session. This restriction can make a TariffElement become
active when the charging power is above this value, but the TariffElement MUST
no longer be active when the charging power drops below the defined value.
 *
 * @property max_power Maximum power in kW, for example 20. When the EV is charging with less than
the defined amount of power, this TariffElement becomes/is active. If the
charging power is or becomes higher, this TariffElement is not or no longer valid
and becomes inactive. This describes NOT the maximum power over the entire
Charging Session. This restriction can make a TariffElement become active
when the charging power is below this value, but the TariffElement MUST no
longer be active when the charging power raises above the defined value
 *
 * @property min_duration Minimum duration in seconds the Charging Session MUST last (inclusive).
When the duration of a Charging Session is longer than the defined value, this
TariffElement is or becomes active. Before that moment, this TariffElement is not
yet active.
 *
 * @property max_duration Maximum duration in seconds the Charging Session MUST last (exclusive).
When the duration of a Charging Session is shorter than the defined value, this
TariffElement is or becomes active. After that moment, this TariffElement is no
longer active.
 *
 * @property day_of_week Which day(s) of the week this TariffElement is active.
 *
 * @property reservation When this field is present, the TariffElement describes reservation costs. A
reservation starts when the reservation is made, and ends when the driver
starts charging on the reserved EVSE/Location, or when the reservation
expires. A reservation can only have: FLAT and TIME TariffDimensions, where
TIME is for the duration of the reservation
 *
 */

@Partial
data class TariffRestrictions(
    val start_time: String,
    val end_time: String,
    val start_date: String,
    val end_date: String,
    val min_kwh: BigDecimal,
    val max_kwh: BigDecimal,
    val min_current: BigDecimal,
    val max_current: BigDecimal,
    val min_power: BigDecimal,
    val max_power: BigDecimal,
    val min_duration: BigDecimal,
    val max_duration: BigDecimal,
    val day_of_week: DayOfWeek,
    val reservation: ReservationRestrictionType,
)
