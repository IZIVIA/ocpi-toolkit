package com.izivia.ocpi.toolkit211.modules.locations.domain

import com.izivia.ocpi.toolkit.annotations.Partial

@Partial
data class GeoLocation(
    val latitude: String,
    val longitude: String,
)
