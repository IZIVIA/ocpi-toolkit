package com.izivia.ocpi.toolkit.samples.common

import com.izivia.ocpi.toolkit.transport.TransportClient
import com.izivia.ocpi.toolkit.transport.domain.HttpRequest
import com.izivia.ocpi.toolkit.transport.domain.HttpResponse
import com.izivia.ocpi.toolkit.transport.domain.parseHttpStatus
import org.http4k.client.JettyClient
import org.http4k.core.Method
import org.http4k.core.Request
import org.http4k.core.Uri
import org.http4k.core.then
import org.http4k.filter.ClientFilters

class Http4kTransportClient(
    baseUrl: String
) : TransportClient {

    val client = ClientFilters.SetBaseUriFrom(Uri.of(baseUrl)).then(JettyClient())

    override fun send(request: HttpRequest): HttpResponse {
        val http4kRequest = Request(
            method = Method.valueOf(request.method.name),
            uri = request.path
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
