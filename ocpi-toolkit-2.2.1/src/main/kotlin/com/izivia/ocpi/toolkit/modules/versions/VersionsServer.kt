package com.izivia.ocpi.toolkit.modules.versions

import com.izivia.ocpi.toolkit.common.OcpiModuleServer
import com.izivia.ocpi.toolkit.common.httpResponse
import com.izivia.ocpi.toolkit.modules.versions.services.VersionsService
import com.izivia.ocpi.toolkit.transport.TransportServer
import com.izivia.ocpi.toolkit.transport.domain.HttpMethod

class VersionsServer(
    private val service: VersionsService,
    basePath: String = "/versions"
) : OcpiModuleServer(basePath) {

    override fun registerOn(transportServer: TransportServer) {
        transportServer.handle(
            method = HttpMethod.GET,
            path = basePathSegments
        ) { req ->
            req.httpResponse {
                service.getVersions()
            }
        }
    }
}
