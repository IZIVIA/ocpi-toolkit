package com.izivia.ocpi.toolkit.samples.common

import com.izivia.ocpi.toolkit.common.OcpiException
import com.izivia.ocpi.toolkit.common.toHttpResponse
import com.izivia.ocpi.toolkit.transport.TransportServer
import com.izivia.ocpi.toolkit.transport.domain.*
import kotlinx.coroutines.runBlocking
import org.http4k.core.*
import org.http4k.filter.DebuggingFilters
import org.http4k.routing.RoutingHttpHandler
import org.http4k.routing.bind
import org.http4k.routing.path
import org.http4k.routing.routes
import org.http4k.server.Http4kServer
import org.http4k.server.Netty
import org.http4k.server.asServer

class Http4kTransportServer(
    val baseUrl: String,
    val port: Int
) : TransportServer {

    private val serverRoutes: MutableList<RoutingHttpHandler> = mutableListOf()
    private lateinit var server: Http4kServer

    override suspend fun handle(
        method: HttpMethod,
        path: List<PathSegment>,
        queryParams: List<String>,
        secured: Boolean,
        filters: List<(request: HttpRequest) -> Unit>,
        callback: suspend (request: HttpRequest) -> HttpResponse,
    ) {
        val pathParams = path
            .filterIsInstance(VariablePathSegment::class.java)
            .map { it.path }

        val route = path.joinToString("/") { segment ->
            when (segment) {
                is VariablePathSegment -> "{${segment.path}}"
                is FixedPathSegment -> segment.path
            }
        }

        serverRoutes.add(
            route bind Method.valueOf(method.name) to { req: Request ->
                try {
                    HttpRequest(
                        baseUrl = baseUrl,
                        method = method,
                        path = route,
                        pathParams = pathParams.associateWith { param -> req.path(param)!! },
                        queryParams = queryParams.associateWith { param -> req.query(param) },
                        headers = req.headers
                            .filter { (_, value) -> value != null }
                            .associate { (key, value) -> key to value!! },
                        body = req.bodyString()
                    )
                        .also { httpRequest -> filters.forEach { filter -> filter(httpRequest) } }
                        .let { httpRequest ->
                            runBlocking { callback(httpRequest) }
                        }
                        .let { httpResponse ->
                            Response(Status(httpResponse.status.code, null))
                                .body(httpResponse.body ?: "")
                                .headers(httpResponse.headers.toList())
                        }
                } catch (ocpiException: OcpiException) {
                    ocpiException.toHttpResponse().toHttp4kResponse()
                } catch (httpException: HttpException) {
                    Response(Status(httpException.status.code, httpException.reason))
                } catch (exception: Exception) {
                    Response(Status.INTERNAL_SERVER_ERROR)
                }
            }
        )
    }

    private fun HttpResponse.toHttp4kResponse() =
        Response(Status(status.code, null))
            .body(body ?: "")
            .headers(headers.toList())

    override fun start() {
        server = DebuggingFilters.PrintRequestAndResponse()
            .then(
                serverRoutes.reduce { a, b -> routes(a, b) }
            )
            .asServer(Netty(port))
            .start()
    }

    override fun stop() {
        server.stop()
    }
}
