package samples

import org.http4k.core.*
import org.http4k.filter.DebuggingFilters
import org.http4k.routing.RoutingHttpHandler
import org.http4k.routing.bind
import org.http4k.routing.path
import org.http4k.routing.routes
import org.http4k.server.Netty
import org.http4k.server.asServer
import transport.TransportServer
import transport.domain.*

class Http4kTransportServer(
    url: String,
    private val port: Int
) : TransportServer("$url:$port") {

    private val serverRoutes: MutableList<RoutingHttpHandler> = mutableListOf()

    override fun handle(
        method: HttpMethod,
        path: List<PathSegment>,
        queryParams: List<String>,
        callback: (request: HttpRequest) -> HttpResponse
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
                callback(
                    HttpRequest(
                        method = method,
                        path = route,
                        pathParams = pathParams.associateWith { param -> req.path(param)!! },
                        queryParams = queryParams.associateWith { param -> req.query(param) },
                    )
                ).let {
                    Response(Status(it.status, null))
                        .body(it.body ?: "")
                        .headers(it.headers.toList())
                }
            }
        )
    }

    override fun start() {
        DebuggingFilters.PrintRequestAndResponse()
            .then(
                serverRoutes.reduce { a, b -> routes(a, b) }
            )
            .asServer(Netty(port)).start()
    }
}