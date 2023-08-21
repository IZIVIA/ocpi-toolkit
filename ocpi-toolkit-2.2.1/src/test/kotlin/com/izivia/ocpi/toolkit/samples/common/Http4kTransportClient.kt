package com.izivia.ocpi.toolkit.samples.common

import com.izivia.ocpi.toolkit.transport.TransportClient
import com.izivia.ocpi.toolkit.transport.domain.HttpRequest
import com.izivia.ocpi.toolkit.transport.domain.HttpResponse
import com.izivia.ocpi.toolkit.transport.domain.parseHttpStatus
import org.http4k.client.JettyClient
import org.http4k.core.*
import org.http4k.filter.ClientFilters

class Http4kTransportClient(
    val client: HttpHandler
) : TransportClient {
    override fun send(request: HttpRequest): HttpResponse {
        return client(request.toHttp4k()).let {
            HttpResponse(
                status = parseHttpStatus(it.status.code),
                body = it.bodyString(),
                headers = it.headers
                    .filter { (_, value) -> value != null }
                    .associate { (key, value) -> key to value!! }
            )
        }
    }

    companion object {
        operator fun invoke(baseUrl: String) =
            Http4kTransportClient(ClientFilters.SetBaseUriFrom(Uri.of(baseUrl)).then(JettyClient()))
    }
}

fun HttpRequest.toHttp4k() =
    Request(
        method = Method.valueOf(method.name),
        uri = path
    )
        .run {
            queryParams.toList().foldRight(this) { queryParam, r ->
                r.query(queryParam.first, queryParam.second)
            }
        }
        .run {
            this@toHttp4k.headers.toList().foldRight(this) { header, r ->
                r.header(header.first, header.second)
            }
        }
        .run {
            this@toHttp4k.body?.let { body -> body(body) } ?: this
        }
