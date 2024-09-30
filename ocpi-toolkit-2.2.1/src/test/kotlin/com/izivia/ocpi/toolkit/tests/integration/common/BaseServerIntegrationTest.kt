package com.izivia.ocpi.toolkit.tests.integration.common

import com.izivia.ocpi.toolkit.common.checkToken
import com.izivia.ocpi.toolkit.modules.credentials.repositories.PartnerRepository
import com.izivia.ocpi.toolkit.samples.common.Http4kTransportClient
import com.izivia.ocpi.toolkit.samples.common.Http4kTransportServer
import java.net.ServerSocket

abstract class BaseServerIntegrationTest : BaseDBIntegrationTest() {

    private fun getFreeNetworkPort() = ServerSocket(0).use { it.localPort }

    protected fun buildTransportServer(
        partnerRepository: PartnerRepository? = null,
    ): Http4kTransportServer {
        val port = getFreeNetworkPort()
        return Http4kTransportServer(
            baseUrl = "http://localhost:$port",
            port = port,
        ) {
            partnerRepository?.checkToken(it)
        }
    }

    protected fun Http4kTransportServer.getClient() =
        Http4kTransportClient(baseUrl)
}
