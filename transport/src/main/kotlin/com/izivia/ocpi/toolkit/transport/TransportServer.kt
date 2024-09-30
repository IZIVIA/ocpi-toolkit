package com.izivia.ocpi.toolkit.transport

import com.izivia.ocpi.toolkit.transport.domain.HttpMethod
import com.izivia.ocpi.toolkit.transport.domain.HttpRequest
import com.izivia.ocpi.toolkit.transport.domain.HttpResponse
import com.izivia.ocpi.toolkit.transport.domain.PathSegment

interface TransportServer {
    /**
     * Creates a route and specifies how to respond. The callback may throw HttpExceptions.
     *
     * @param method
     * @param path
     * @param queryParams expected queryParams
     * @param callback
     */
    suspend fun handle(
        method: HttpMethod,
        path: List<PathSegment>,
        queryParams: List<String> = emptyList(),
        filters: List<(request: HttpRequest) -> Unit> = emptyList(),
        callback: suspend (request: HttpRequest) -> HttpResponse,
    )

    /**
     * Starts the server
     */
    fun start()

    /**
     * Stops the server
     */
    fun stop()

    /**
     * The configured baseUrl this server listens to.
     * Used by [com.izivia.ocpi.toolkit.common.OcpiSelfRegisteringModuleServer] to generate Endpoint URL.
     * Can be used by implementations of TransportServer to populate the baseUrl of HttpRequest (see example), which in
     * turn is used to generate NextPageUrl of SearchResult
     */
    fun baseUrl(): String
}
