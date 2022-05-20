package com.izivia.ocpi.toolkit.modules.locations.domain

import com.izivia.ocpi.toolkit.annotations.Partial
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