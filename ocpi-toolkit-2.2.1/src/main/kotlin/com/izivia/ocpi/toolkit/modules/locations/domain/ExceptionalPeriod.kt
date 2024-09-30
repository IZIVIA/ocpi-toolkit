package com.izivia.ocpi.toolkit.modules.locations.domain

import com.izivia.ocpi.toolkit.annotations.Partial
import java.time.Instant

/**
 * Specifies one exceptional period for opening or access hours.
 *
 * @property periodBegin Begin of the exception.
 * @property periodEnd End of the exception.
 */
@Partial
data class ExceptionalPeriod(
    val periodBegin: Instant,
    val periodEnd: Instant,
)
