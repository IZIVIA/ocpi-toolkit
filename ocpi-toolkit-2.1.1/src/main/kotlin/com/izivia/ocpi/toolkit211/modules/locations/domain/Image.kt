package com.izivia.ocpi.toolkit211.modules.locations.domain

import com.izivia.ocpi.toolkit.annotations.Partial
import com.izivia.ocpi.toolkit211.common.CiString

@Partial
data class Image(
    val url: String,
    val thumbnail: String?,
    val category: ImageCategory,
    val type: CiString,
    val width: Int?,
    val height: Int?,
)
