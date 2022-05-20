package com.izivia.ocpi.toolkit.transport

import com.izivia.ocpi.toolkit.transport.domain.HttpRequest
import com.izivia.ocpi.toolkit.transport.domain.HttpResponse

abstract class TransportClient(
    open val baseUrl: String
) {

    /**
     * Send a provided request to a server using a particular transport layer
     * @param request the request to be sent to a server through the transport layer
     * @return the response sent by the server
     */
    abstract fun send(request: HttpRequest): HttpResponse
}

abstract class TransportClientBuilder {
    abstract fun build(url: String): TransportClient
}