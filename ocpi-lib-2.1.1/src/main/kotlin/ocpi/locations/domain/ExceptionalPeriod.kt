package ocpi.locations.domain

import java.time.Instant

/**
 * Specifies one exceptional period for opening or access hours.
 *
 * @property period_begin Begin of the exception.
 * @property period_end End of the exception.
 */
data class ExceptionalPeriod(
    val period_begin: Instant,
    val period_end: Instant
)

data class ExceptionalPeriodPatch(
    val period_begin: Instant?,
    val period_end: Instant?
)

