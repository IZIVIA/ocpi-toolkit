package com.izivia.ocpi.toolkit.transport.domain

data class HttpResponse(
    val status: HttpStatus,
    val body: String? = null,
    val headers: Map<String, String> = emptyMap(),
) {
    fun withHeaderMixin(key: String, value: String) = copy(headers = headers.plus(key to value))
    fun withHeadersMixin(extraHeaders: Map<String, String>) = copy(headers = headers.plus(extraHeaders))
}
