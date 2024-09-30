package com.izivia.ocpi.toolkit.transport.domain

data class HttpRequest(
    val method: HttpMethod,
    val path: String = "",
    val baseUrl: String? = null,
    val pathParams: Map<String, String?> = emptyMap(),
    val queryParams: Map<String, String?> = emptyMap(),
    val body: String? = null,
    val headers: Map<String, String> = emptyMap(),
) {
    fun withHeaders(headers: Map<String, String> = emptyMap()) = copy(headers = headers)
}
