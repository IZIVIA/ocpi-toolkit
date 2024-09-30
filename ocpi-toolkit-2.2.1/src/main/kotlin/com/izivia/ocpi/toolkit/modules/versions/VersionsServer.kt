package com.izivia.ocpi.toolkit.modules.versions

import com.izivia.ocpi.toolkit.common.OcpiModuleServer
import com.izivia.ocpi.toolkit.common.httpResponse
import com.izivia.ocpi.toolkit.modules.versions.services.VersionsService
import com.izivia.ocpi.toolkit.transport.TransportServer
import com.izivia.ocpi.toolkit.transport.domain.FixedPathSegment
import com.izivia.ocpi.toolkit.transport.domain.HttpMethod
import com.izivia.ocpi.toolkit.transport.domain.VariablePathSegment

class VersionsServer(
    private val service: VersionsService,
) : OcpiModuleServer("") {

    override suspend fun registerOn(transportServer: TransportServer) {
        transportServer.handle(
            method = HttpMethod.GET,
            path = listOf(FixedPathSegment(service.versionsBasePath)),
        ) { req ->
            req.httpResponse {
                service.getVersions()
            }
        }

        transportServer.handle(
            method = HttpMethod.GET,
            path = listOf(
                FixedPathSegment(service.versionDetailsBasePath),
                VariablePathSegment("versionNumber"),
            ),
        ) { req ->
            req.httpResponse {
                service.getVersionDetails(
                    versionNumber = req.pathParams["versionNumber"]!!,
                )
            }
        }
    }
}
