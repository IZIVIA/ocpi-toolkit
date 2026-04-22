package com.izivia.ocpi.toolkit211.modules.locations.domain

import com.izivia.ocpi.toolkit.annotations.Partial

@Partial
data class RegularHours(
    val weekday: Int,
    val periodBegin: String,
    val periodEnd: String,
)
