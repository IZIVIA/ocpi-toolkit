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

    private val normalizedHeaders by lazy { headers.mapKeys { it.key.lowercase() } }

    /**
     * Returns the value of a header by its key. The key is not case-sensitive.
     */
    fun getHeader(key: String): String? = normalizedHeaders[key.lowercase()]

    /**
     * For debugging issues, OCPI implementations are required to include unique IDs via HTTP headers in every
     * request/response
     *
     * - X-Request-ID: Every request SHALL contain a unique request ID, the response to this request SHALL contain the same
     * ID.
     * - X-Correlation-ID: Every request/response SHALL contain a unique correlation ID, every response to this request
     * SHALL contain the same ID.
     *
     * This method should be called when responding to a request from a client.
     */
    fun getDebugHeaders() = listOfNotNull(
        getHeader(HttpHeaders.X_REQUEST_ID)?.let { HttpHeaders.X_REQUEST_ID to it },
        getHeader(HttpHeaders.X_CORRELATION_ID)?.let { HttpHeaders.X_CORRELATION_ID to it },
    ).toMap()

    fun withHeaders(headers: Map<String, String> = emptyMap()) = copy(headers = headers)
    fun withHeaderMixin(key: String, value: String) = copy(headers = headers.plus(key to value))
    fun withHeadersMixin(extraHeaders: Map<String, String>) = copy(headers = headers.plus(extraHeaders))

    fun withRequiredHeaders(
        authToken: String,
        requestId: String,
        correlationId: String,
        contentTypeHeader: String?,
    ): HttpRequest =
        withHeadersMixin(
            buildMap {
                put(HttpHeaders.AUTHORIZATION, authToken)
                put(HttpHeaders.X_REQUEST_ID, requestId)
                put(HttpHeaders.X_CORRELATION_ID, correlationId)
                contentTypeHeader?.let { put(HttpHeaders.CONTENT_TYPE, it) }
            },
        )
}
