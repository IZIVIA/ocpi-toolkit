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
        var http4kRequest = Request(
            method = Method.valueOf(request.method.name),
            uri = "$baseUrl${request.path}"
        )

        request.queryParams.forEach { queryParam ->
            http4kRequest = http4kRequest.query(queryParam.key, queryParam.value)
        }

        request.body?.let { body ->
            http4kRequest = http4kRequest.body(body)
        }

        return client(http4kRequest).let {
            HttpResponse(
                status = it.status.code,
                body = it.bodyString()
            )
        }
    }
}