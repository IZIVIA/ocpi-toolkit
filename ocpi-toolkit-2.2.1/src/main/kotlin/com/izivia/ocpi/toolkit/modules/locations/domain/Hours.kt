package com.izivia.ocpi.toolkit.modules.locations.domain

import com.izivia.ocpi.toolkit.annotations.Partial

/**
 * Opening and access hours of the location.
 * Note: regular_hours is required when twentyfourseven=false, this is much less confusing.
 *
 * @property regularHours Regular hours, weekday based. Should not be set for representing 24/7 as this is the most
 * common case.
 * @property twentyfourseven True to represent 24 hours a day and 7 days a week, except the given exceptions.
 * @property exceptionalOpenings Exceptions for specified calendar dates, time-range based. Periods the station is
 * operating/accessible. Additional to regular hours. May overlap regular rules.
 * @property exceptionalClosings Exceptions for specified calendar dates, time-range based. Periods the station is not
 * operating/accessible. Overwriting regularHours and exceptionalOpenings. Should not overlap exceptionalOpenings.
 */
@Partial
data class Hours(
    val regularHours: List<RegularHours>?,
    val twentyfourseven: Boolean,
    val exceptionalOpenings: List<ExceptionalPeriod>?,
    val exceptionalClosings: List<ExceptionalPeriod>?,
)
