package com.izivia.ocpi.toolkit.common

import java.time.Instant

/** OCPI response envelope for acknowledgements whose data is not used. */
data class OcpiResponseStatus(
    val statusCode: Int,
    val statusMessage: String? = null,
    val timestamp: Instant,
)
