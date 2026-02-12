package com.izivia.ocpi.toolkit.transport

import com.izivia.ocpi.toolkit.transport.context.currentRequestMessageRoutingHeadersOrNull
import com.izivia.ocpi.toolkit.transport.domain.HttpContentType
import com.izivia.ocpi.toolkit.transport.domain.HttpHeaders
import com.izivia.ocpi.toolkit.transport.domain.HttpRequest
import com.izivia.ocpi.toolkit.transport.domain.HttpResponse
import java.util.*

interface TransportClient {
    /**
     * Send a provided request to a server using a particular transport layer
     * @param request the request to be sent to a server through the transport layer
     * @return the response sent by the server
     */
    suspend fun send(request: HttpRequest): HttpResponse
}

abstract class AbstractTransportClient(
    private val authToken: String,
) : TransportClient {
    override suspend fun send(request: HttpRequest): HttpResponse {
        val enhancedRequest = request.withRequiredHeaders(
            authToken = generateAuthToken(),
            requestId = generateRequestId(),
            correlationId = generateCorrelationId(request),
            contentTypeHeader = generateContentType(request),
        )
            .withHeadersMixin(generateRequestMessageRoutingHeaders()?.toHttpHeaders() ?: emptyMap())

        return doSend(enhancedRequest)
    }

    protected open suspend fun generateAuthToken(): String {
        return "Token ${authToken.encodeBase64()}"
    }

    /**
     * Override this method if you want to use your own login to generate de X_Request_ID header value
     * @return String X_Request_ID header value
     */
    protected open suspend fun generateRequestId(): String = UUID.randomUUID().toString()

    /**
     * Override this method if you want to use your own login to generate de X_Correlation_ID header value.
     * By default, preserves previously set correlation id
     * @return String X_Correlation_ID header value
     */
    protected open suspend fun generateCorrelationId(request: HttpRequest): String =
        request.getHeader(HttpHeaders.X_CORRELATION_ID) ?: UUID.randomUUID().toString()

    /**
     * Overriding this method should only be needed, if you need support a custom module
     * @return String ContentType header value
     */
    protected open suspend fun generateContentType(request: HttpRequest): String? =
        request.body?.let { HttpContentType.APPLICATION_JSON }

    protected open suspend fun generateRequestMessageRoutingHeaders() = currentRequestMessageRoutingHeadersOrNull()

    protected abstract suspend fun doSend(request: HttpRequest): HttpResponse
}
