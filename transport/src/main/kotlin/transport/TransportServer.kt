package transport

import transport.domain.HttpMethod
import transport.domain.HttpRequest
import transport.domain.HttpResponse
import transport.domain.PathSegment

/**
 * @property baseUrl only used to know the next page for paginated responses.
 */
abstract class TransportServer(
    val baseUrl: String
) {
    /**
     * Creates a route and specifies how to respond
     * @param method
     * @param path
     * @param queryParams expected queryParams
     * @param callback
     */
    abstract fun handle(
        method: HttpMethod,
        path: List<PathSegment>,
        queryParams: List<String> = emptyList(),
        callback: (request: HttpRequest) -> HttpResponse
    )

    /**
     * Starts the server
     */
    abstract fun start()
}