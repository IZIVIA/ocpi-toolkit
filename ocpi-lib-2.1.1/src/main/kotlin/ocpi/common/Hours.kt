package ocpi.common

/**
 * Opening and access hours of the location.
 *
 * Either regularHours or twentyFourSeven is set. Both cannot be null and both cannot be set.
 */
data class Hours(
    val regularHours: List<RegularHours>?,
    val twentyFourSeven: Boolean?,
    val exceptionalOpenings: List<ExceptionalPeriod>,
    val exceptionalClosings: List<ExceptionalPeriod>
)

data class HoursPatch(
    val regularHours: List<RegularHours>?,
    val twentyFourSeven: Boolean?,
    val exceptionalOpenings: List<ExceptionalPeriod>,
    val exceptionalClosings: List<ExceptionalPeriod>
)
