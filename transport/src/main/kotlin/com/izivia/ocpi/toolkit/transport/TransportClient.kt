package com.izivia.ocpi.toolkit.transport

import com.izivia.ocpi.toolkit.transport.domain.HttpRequest
import com.izivia.ocpi.toolkit.transport.domain.HttpResponse
import java.util.*

interface TransportClient {
    /**
     * Send a provided request to a server using a particular transport layer
     * @param request the request to be sent to a server through the transport layer
     * @return the response sent by the server
     */
    fun send(request: HttpRequest): HttpResponse

    /**
     * Override this method if you want to use your own login to generate de X_Correlation_ID header value.
     * @return String X_Correlation_ID header value
     */
    suspend fun generateCorrelationId(): String = UUID.randomUUID().toString()

    /**
     * Override this method if you want to use your own login to generate de X_Request_ID header value
     * @return String X_Request_ID header value
     */
    suspend fun generateRequestId(): String = UUID.randomUUID().toString()
}
