package com.izivia.ocpi.toolkit.modules.versions

import com.izivia.ocpi.toolkit.common.OcpiModuleServer
import com.izivia.ocpi.toolkit.common.httpResponse
import com.izivia.ocpi.toolkit.modules.versions.services.VersionDetailsService
import com.izivia.ocpi.toolkit.transport.TransportServer
import com.izivia.ocpi.toolkit.transport.domain.HttpMethod
import com.izivia.ocpi.toolkit.transport.domain.VariablePathSegment

class VersionDetailsServer(
    private val service: VersionDetailsService,
    basePath: String = ""
) : OcpiModuleServer(basePath) {

    override fun registerOn(transportServer: TransportServer) {
        transportServer.handle(
            method = HttpMethod.GET,
            path = basePathSegments + listOf(
                VariablePathSegment("versionNumber")
            )
        ) { req ->
            req.httpResponse {
                service.getVersionDetails(
                    versionNumber = req.pathParams["versionNumber"]!!
                )
            }
        }
    }
}
