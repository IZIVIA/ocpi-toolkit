package samples

import org.http4k.contract.ContractRoute
import org.http4k.contract.bindContract
import org.http4k.contract.contract
import org.http4k.core.*
import org.http4k.filter.DebuggingFilters
import org.http4k.routing.path
import org.http4k.server.Netty
import org.http4k.server.asServer
import transport.TransportServer
import transport.domain.HttpMethod
import transport.domain.HttpResponse

class Http4kTransportServer(
    private val port: Int
) : TransportServer() {

    private val serverRoutes: MutableList<ContractRoute> = mutableListOf()

    override fun handle(
        method: HttpMethod,
        path: String,
        pathParams: List<String>,
        queryParams: List<String>,
        callback: (pathParams: Map<String, String>, queryParams: Map<String, String?>) -> HttpResponse
    ) {
        serverRoutes.add(
            path bindContract Method.valueOf(method.name) to { req: Request ->
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
                contract { routes += serverRoutes }
            )
            .asServer(Netty(port)).start()
    }
}