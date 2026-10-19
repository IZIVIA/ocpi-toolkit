package com.izivia.ocpi.toolkit.modules.versions

import com.izivia.ocpi.toolkit.common.OcpiModuleServer
import com.izivia.ocpi.toolkit.common.TimeProvider
import com.izivia.ocpi.toolkit.common.pathParam
import com.izivia.ocpi.toolkit.common.respondList
import com.izivia.ocpi.toolkit.common.respondObject
import com.izivia.ocpi.toolkit.common.withoutMessageRoutingHeaders
import com.izivia.ocpi.toolkit.modules.versions.services.VersionsService
import com.izivia.ocpi.toolkit.transport.TransportServer
import com.izivia.ocpi.toolkit.transport.domain.FixedPathSegment
import com.izivia.ocpi.toolkit.transport.domain.HttpMethod
import com.izivia.ocpi.toolkit.transport.domain.VariablePathSegment
import java.time.Instant

class VersionsServer(
    private val service: VersionsService,
    private val timeProvider: TimeProvider = TimeProvider { Instant.now() },
) : OcpiModuleServer("") {

    override suspend fun registerOn(transportServer: TransportServer) {
        transportServer.handle(
            method = HttpMethod.GET,
            path = listOf(FixedPathSegment(service.versionsBasePath)),
        ) { req ->
            withoutMessageRoutingHeaders {
                req.respondList(timeProvider.now()) {
                    service.getVersions()
                }
            }
        }

        transportServer.handle(
            method = HttpMethod.GET,
            path = listOf(
                FixedPathSegment(service.versionDetailsBasePath),
                VariablePathSegment("versionNumber"),
            ),
        ) { req ->
            withoutMessageRoutingHeaders {
                req.respondObject(timeProvider.now()) {
                    service.getVersionDetails(
                        versionNumber = req.pathParam("versionNumber"),
                    )
                }
            }
        }
    }
}
