package com.izivia.ocpi.toolkit.modules.locations.domain

import com.izivia.ocpi.toolkit.annotations.Partial
import java.time.Instant

/**
 * This type is used to schedule status periods in the future. The eMSP can provide this information to the EV user for
 * trip planning purpose. A period MAY have no end. Example: "This station will be running as of tomorrow. Today it is
 * still planned and under construction."
 *
 * Note that the scheduled status is purely informational. When the status actually changes, the CPO must push an update
 * to the EVSEs `status` field itself.
 *
 * @property periodBegin Begin of the scheduled period.
 * @property periodEnd End of the scheduled period, if known.
 * @property status Status value during the scheduled period.
 */
@Partial
data class StatusSchedule(
    val periodBegin: Instant,
    val periodEnd: Instant?,
    val status: Status,
)
