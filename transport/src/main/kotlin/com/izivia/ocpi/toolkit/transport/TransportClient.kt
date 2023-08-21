package com.izivia.ocpi.toolkit.transport

import com.izivia.ocpi.toolkit.transport.domain.HttpRequest
import com.izivia.ocpi.toolkit.transport.domain.HttpResponse

interface TransportClient {
    /**
     * Send a provided request to a server using a particular transport layer
     * @param request the request to be sent to a server through the transport layer
     * @return the response sent by the server
     */
    fun send(request: HttpRequest): HttpResponse
}

interface TransportClientBuilder {
    fun build(baseUrl: String): TransportClient
}
