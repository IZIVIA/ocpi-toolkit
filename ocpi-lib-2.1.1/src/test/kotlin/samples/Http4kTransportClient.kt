package samples

import org.http4k.client.JettyClient
import org.http4k.core.Method
import org.http4k.core.Request
import transport.TransportClient
import transport.domain.HttpRequest
import transport.domain.HttpResponse

class Http4kTransportClient(
    private val baseUrl: String
) : TransportClient() {

    val client = JettyClient()

    override fun send(request: HttpRequest): HttpResponse {
        val http4kRequest = Request(
            method = Method.valueOf(request.method.name),
            uri = "$baseUrl${request.path}"
        )
            .run {
                request.queryParams.toList().foldRight(this) { queryParam, r ->
                    r.query(queryParam.first, queryParam.second)
                }
            }
            .run {
                request.body?.let { body -> body(body) } ?: this
            }

        return client(http4kRequest).let {
            HttpResponse(
                status = it.status.code,
                body = it.bodyString(),
                headers = it.headers
                    .filter { header -> header.second != null }
                    .toMap() as Map<String, String>
            )
        }
    }
}
