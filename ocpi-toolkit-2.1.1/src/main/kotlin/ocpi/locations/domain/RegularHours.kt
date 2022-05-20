package ocpi.locations.domain

import io.github.quatresh.annotations.Partial

/**
 * Regular recurring operation or access hours
 *
 * @property weekday (max-length=1) Number of day in the week, from Monday (1) till Sunday (7)
 * @property period_begin (max-length=5) Begin of the regular period given in hours and minutes. Must be in 24h format
 * with leading zeros. Example: "18:15". Hour/Minute separator: ":" Regex: ([0-1][0-9]|2[0-3]):[0-5][0-9]
 * @property period_end (max-length=5) End of the regular period, syntax as for periodBegin. Must be later than
 * periodBegin.
 */
@Partial
data class RegularHours(
    val weekday: Int,
    val period_begin: String,
    val period_end: String
)