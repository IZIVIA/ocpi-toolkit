package com.izivia.ocpi.toolkit.samples.common

import com.izivia.ocpi.toolkit.transport.AbstractTransportClient
import com.izivia.ocpi.toolkit.transport.domain.HttpHeaders
import com.izivia.ocpi.toolkit.transport.domain.HttpRequest
import com.izivia.ocpi.toolkit.transport.domain.HttpResponse
import com.izivia.ocpi.toolkit.transport.domain.parseHttpStatus
import org.http4k.client.OkHttp
import org.http4k.core.*
import org.http4k.filter.ClientFilters

// These values are used for testing, it's not useful for your transport implementation
var requestCounter = 1
var correlationCounter = 1

class Http4kTransportClient(
    authToken: String,
    val client: HttpHandler,
) : AbstractTransportClient(authToken) {
    override suspend fun doSend(request: HttpRequest): HttpResponse {
        return client(request.toHttp4k()).let {
            HttpResponse(
                status = parseHttpStatus(it.status.code),
                body = it.bodyString(),
                headers = it.headers
                    .filter { (_, value) -> value != null }
                    .associate { (key, value) -> key to value!! },
            )
        }
    }

    override suspend fun generateCorrelationId(request: HttpRequest): String =
        request.getHeader(HttpHeaders.X_CORRELATION_ID) ?: "corr-id-${correlationCounter++}"

    override suspend fun generateRequestId(): String = "req-id-${requestCounter++}"

    companion object {
        operator fun invoke(client: HttpHandler) =
            Http4kTransportClient(
                authToken = "*",
                client = client,
            )

        operator fun invoke(baseUrl: String) =
            Http4kTransportClient(
                authToken = "*",
                ClientFilters.SetBaseUriFrom(Uri.of(baseUrl)).then(OkHttp()),
            )
    }
}

fun HttpRequest.toHttp4k() =
    Request(
        method = Method.valueOf(method.name),
        uri = path,
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
