package transport

import transport.domain.HttpMethod
import transport.domain.HttpResponse


// TODO: documentation
abstract class TransportServer {
    abstract fun handle(
        method: HttpMethod,
        path: String,
        pathParams: List<String> = emptyList(),
        queryParams: List<String> = emptyList(),
        callback: (pathParams: Map<String, String>, queryParams: Map<String, String?>) -> HttpResponse
    )
    abstract fun start()
}