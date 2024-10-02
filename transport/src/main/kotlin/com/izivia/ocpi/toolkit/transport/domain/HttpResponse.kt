package com.izivia.ocpi.toolkit.transport.domain

data class HttpResponse(
    val status: HttpStatus,
    val body: String? = null,
    val headers: Map<String, String> = emptyMap(),
)
