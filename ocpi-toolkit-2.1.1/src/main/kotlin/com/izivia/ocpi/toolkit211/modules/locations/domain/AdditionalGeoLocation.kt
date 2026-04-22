package com.izivia.ocpi.toolkit211.modules.locations.domain

import com.izivia.ocpi.toolkit.annotations.Partial
import com.izivia.ocpi.toolkit211.modules.types.DisplayText

@Partial
data class AdditionalGeoLocation(
    val latitude: String,
    val longitude: String,
    val name: DisplayText?,
)
