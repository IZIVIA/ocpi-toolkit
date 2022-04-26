package transport.domain

data class HttpRequest(
    val method: HttpMethod,
    val path: String,
    val pathParams: Map<String, String?> = emptyMap(),
    val queryParams: Map<String, String?> = emptyMap(),
    val body: String? = null
)
