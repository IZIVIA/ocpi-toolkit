package samples.common

import org.http4k.client.JettyClient
import org.http4k.core.Method
import org.http4k.core.Request
import transport.TransportClient
import transport.domain.HttpRequest
import transport.domain.HttpResponse
import transport.domain.parseHttpStatus

class Http4kTransportClient(
    baseUrl: String
) : TransportClient(baseUrl) {

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
                request.headers.toList().foldRight(this) { header, r ->
                    r.header(header.first, header.second)
                }
            }
            .run {
                request.body?.let { body -> body(body) } ?: this
            }

        return client(http4kRequest).let {
            HttpResponse(
                status = parseHttpStatus(it.status.code),
                body = it.bodyString(),
                headers = it.headers
                    .filter { (_, value) -> value != null }
                    .associate { (key, value) -> key to value!! }
            )
        }
    }
}
