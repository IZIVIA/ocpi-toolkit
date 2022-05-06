package tests.integration

import samples.common.Http4kTransportClient
import samples.common.Http4kTransportServer
import java.net.ServerSocket

abstract class BaseServerIntegrationTest: BaseDBIntegrationTest() {

    private fun getFreeNetworkPort() = ServerSocket(0).use { it.localPort }

    protected fun buildTransportServer(): Http4kTransportServer {
        val port = getFreeNetworkPort()
        return Http4kTransportServer(
            baseUrl = "http://localhost:$port",
            port = port
        )
    }

    protected fun Http4kTransportServer.getClient() =
        Http4kTransportClient(baseUrl)
}
