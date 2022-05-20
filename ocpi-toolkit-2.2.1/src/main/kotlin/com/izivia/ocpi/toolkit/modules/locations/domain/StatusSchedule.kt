package com.izivia.ocpi.toolkit.modules.locations.domain

import io.github.quatresh.annotations.Partial
import java.time.Instant

/**
 * This type is used to schedule status periods in the future. The eMSP can provide this information to the EV user for
 * trip planning purpose. A period MAY have no end. Example: "This station will be running as of tomorrow. Today it is
 * still planned and under construction."
 *
 * Note that the scheduled status is purely informational. When the status actually changes, the CPO must push an update
 * to the EVSEs `status` field itself.
 *
 * @property period_begin Begin of the scheduled period.
 * @property period_end End of the scheduled period, if known.
 * @property status Status value during the scheduled period.
 */
@Partial
data class StatusSchedule(
    val period_begin: Instant,
    val period_end: Instant?,
    val status: Status
)