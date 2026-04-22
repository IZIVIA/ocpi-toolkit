package com.izivia.ocpi.toolkit211.modules.locations.domain

import com.izivia.ocpi.toolkit.annotations.Partial
import java.time.Instant

@Partial
data class StatusSchedule(
    val periodBegin: Instant,
    val periodEnd: Instant?,
    val status: Status,
)
