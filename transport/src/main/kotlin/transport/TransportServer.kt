package transport

import transport.domain.HttpMethod
import transport.domain.HttpResponse
import transport.domain.PathSegment


// TODO: documentation
abstract class TransportServer {
    abstract fun handle(
        method: HttpMethod,
        path: List<PathSegment>,
        queryParams: List<String> = emptyList(),
        callback: (pathParams: Map<String, String>, queryParams: Map<String, String?>) -> HttpResponse
    )
    abstract fun start()
}