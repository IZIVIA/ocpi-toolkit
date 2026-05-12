package com.izivia.ocpi.toolkit211.modules.versions

import com.izivia.ocpi.toolkit.transport.TransportServer
import com.izivia.ocpi.toolkit.transport.domain.FixedPathSegment
import com.izivia.ocpi.toolkit.transport.domain.HttpMethod
import com.izivia.ocpi.toolkit.transport.domain.VariablePathSegment
import com.izivia.ocpi.toolkit211.common.OcpiModuleServer
import com.izivia.ocpi.toolkit211.common.TimeProvider
import com.izivia.ocpi.toolkit211.common.pathParam
import com.izivia.ocpi.toolkit211.common.respondList
import com.izivia.ocpi.toolkit211.common.respondObject
import com.izivia.ocpi.toolkit211.modules.versions.services.VersionsService
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
            req.respondList(timeProvider.now()) {
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
            req.respondObject(timeProvider.now()) {
                service.getVersionDetails(
                    versionNumber = req.pathParam("versionNumber"),
                )
            }
        }
    }
}
