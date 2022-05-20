package com.izivia.ocpi.toolkit.transport

import com.izivia.ocpi.toolkit.transport.domain.HttpMethod
import com.izivia.ocpi.toolkit.transport.domain.HttpRequest
import com.izivia.ocpi.toolkit.transport.domain.HttpResponse
import com.izivia.ocpi.toolkit.transport.domain.PathSegment


abstract class TransportServer {
    /**
     * Creates a route and specifies how to respond. The callback may throw HttpExceptions.
     *
     * @param method
     * @param path
     * @param queryParams expected queryParams
     * @param callback
     */
    abstract fun handle(
        method: HttpMethod,
        path: List<PathSegment>,
        queryParams: List<String> = emptyList(),
        filters: List<(request: HttpRequest) -> Unit> = emptyList(),
        callback: (request: HttpRequest) -> HttpResponse
    )

    /**
     * Starts the server
     */
    abstract fun start()

    /**
     * Stops the server
     */
    abstract fun stop()
}