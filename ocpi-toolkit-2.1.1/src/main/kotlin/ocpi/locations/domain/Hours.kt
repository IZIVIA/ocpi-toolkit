package ocpi.locations.domain

import io.github.quatresh.annotations.Partial

/**
 * Opening and access hours of the location.
 * Either regularHours or twentyFourSeven is set. Both cannot be null and both cannot be set.
 *
 * @property regular_hours Regular hours, weekday based. Should not be set for representing 24/7 as this is the most
 * common case.
 * @property twenty_four_seven True to represent 24 hours a day and 7 days a week, except the given exceptions.
 * @property exceptional_openings Exceptions for specified calendar dates, time-range based. Periods the station is
 * operating/accessible. Additional to regular hours. May overlap regular rules.
 * @property exceptional_closings Exceptions for specified calendar dates, time-range based. Periods the station is not
 * operating/accessible. Overwriting regularHours and exceptionalOpenings. Should not overlap exceptionalOpenings.
 */
@Partial
data class Hours(
    val regular_hours: List<RegularHours>?,
    val twenty_four_seven: Boolean?,
    val exceptional_openings: List<ExceptionalPeriod>?,
    val exceptional_closings: List<ExceptionalPeriod>?
)