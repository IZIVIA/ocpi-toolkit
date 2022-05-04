package transport

import transport.domain.HttpRequest
import transport.domain.HttpResponse

abstract class TransportClient {

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