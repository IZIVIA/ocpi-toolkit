package transport

import transport.domain.HttpRequest
import transport.domain.HttpResponse


// TODO: documentation
abstract class TransportClient {
    abstract fun send(request: HttpRequest): HttpResponse
}