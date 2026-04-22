package com.izivia.ocpi.toolkit211.modules.locations.domain

import com.izivia.ocpi.toolkit.annotations.Partial

@Partial
data class BusinessDetails(
    val name: String,
    val website: String? = null,
    val logo: Image? = null,
)
