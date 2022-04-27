package ocpi.locations.domain

import io.github.quatresh.annotations.Partial
import java.time.Instant

/**
 * Specifies one exceptional period for opening or access hours.
 *
 * @property period_begin Begin of the exception.
 * @property period_end End of the exception.
 */
@Partial
data class ExceptionalPeriod(
    val period_begin: Instant,
    val period_end: Instant
)


data class ExceptionalPeriodPartial(
    val period_begin: Instant?,
    val period_end: Instant?,
)