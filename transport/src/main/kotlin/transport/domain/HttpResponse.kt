package transport.domain

data class HttpResponse(
    val status: Int,
    val body: String? = null,
    val headers: Map<String, String> = emptyMap()
)