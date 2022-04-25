package transport.domain

data class HttpResponse(
    val status: Int,
    val body: String
)
