package com.izivia.ocpi.toolkit211.modules.locations.domain

import com.izivia.ocpi.toolkit.annotations.Partial

@Partial
data class Hours(
    val regularHours: List<RegularHours>?,
    val twentyfourseven: Boolean,
    val exceptionalOpenings: List<ExceptionalPeriod>?,
    val exceptionalClosings: List<ExceptionalPeriod>?,
)
