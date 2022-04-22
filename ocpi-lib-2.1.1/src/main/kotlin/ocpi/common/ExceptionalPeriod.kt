package ocpi.common

import java.time.Instant

/**
 * Specifies one exceptional period for opening or access hours.
 *
 * @property periodBegin Begin of the exception.
 * @property periodEnd End of the exception.
 */
data class ExceptionalPeriod(
    val periodBegin: Instant,
    val periodEnd: Instant
)
