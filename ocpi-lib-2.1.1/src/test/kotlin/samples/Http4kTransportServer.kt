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
import transport.domain.HttpMethod
import transport.domain.HttpResponse
import transport.domain.PathSegment

class Http4kTransportServer(
    private val port: Int
) : TransportServer() {

    private val serverRoutes: MutableList<RoutingHttpHandler> = mutableListOf()

    override fun handle(
        method: HttpMethod,
        path: List<PathSegment>,
        queryParams: List<String>,
        callback: (pathParams: Map<String, String>, queryParams: Map<String, String?>) -> HttpResponse
    ) {
        val pathParams = path.filter { it.param }.map { it.path }
        val route = path.joinToString("/") { if (it.param) "{${it.path}}" else it.path }

        serverRoutes.add(
            route bind Method.valueOf(method.name) to { req: Request ->
                callback(
                    pathParams.associateWith { param -> req.path(param)!! },
                    queryParams.associateWith { param -> req.query(param) },
                ).let {
                    Response(Status(it.status, null)).body(it.body)
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