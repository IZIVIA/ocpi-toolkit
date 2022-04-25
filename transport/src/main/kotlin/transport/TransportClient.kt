package transport

import transport.domain.HttpRequest
import transport.domain.HttpResponse


abstract class TransportClient {
    abstract fun send(request: HttpRequest): HttpResponse
}