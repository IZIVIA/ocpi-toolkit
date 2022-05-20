package com.izivia.ocpi.toolkit.samples.common

import com.izivia.ocpi.toolkit.modules.versions.domain.Endpoint
import com.izivia.ocpi.toolkit.modules.versions.domain.Version

data class Platform(
    val url: String,
    val version: Version? = null,
    val endpoints: List<Endpoint>? = null,
    val tokenA: String? = null,
    val tokenB: String? = null,
    val tokenC: String? = null
)
