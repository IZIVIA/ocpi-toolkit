package com.izivia.ocpi.toolkit211.modules.credentials

import com.izivia.ocpi.toolkit.transport.TransportServer
import com.izivia.ocpi.toolkit.transport.domain.HttpMethod
import com.izivia.ocpi.toolkit211.common.*
import com.izivia.ocpi.toolkit211.modules.credentials.domain.Credentials
import com.izivia.ocpi.toolkit211.modules.credentials.services.CredentialsServerService
import com.izivia.ocpi.toolkit211.modules.versions.domain.ModuleID
import com.izivia.ocpi.toolkit211.modules.versions.domain.VersionNumber
import com.izivia.ocpi.toolkit211.modules.versions.repositories.MutableVersionsRepository
import com.izivia.ocpi.toolkit211.serialization.deserializeObject
import com.izivia.ocpi.toolkit211.serialization.mapper
import java.time.Instant

class CredentialsServer(
    private val service: CredentialsServerService,
    private val timeProvider: TimeProvider = TimeProvider { Instant.now() },
    versionsRepository: MutableVersionsRepository? = null,
    basePathOverride: String? = null,
) : OcpiSelfRegisteringModuleServer(
    ocpiVersion = VersionNumber.V2_1_1,
    moduleID = ModuleID.credentials,
    versionsRepository = versionsRepository,
    basePathOverride = basePathOverride,
) {

    override suspend fun doRegisterOn(transportServer: TransportServer) {
        transportServer.handle(
            method = HttpMethod.GET,
            path = basePathSegments,
        ) { req ->
            req.respondObject(timeProvider.now()) {
                service.get(token = req.parseAuthorizationHeader())
            }
        }

        transportServer.handle(
            method = HttpMethod.POST,
            path = basePathSegments,
        ) { req ->
            req.respondObject(timeProvider.now()) {
                service.post(
                    token = req.parseAuthorizationHeader(),
                    credentials = mapper.deserializeObject<Credentials>(req.body!!),
                    debugHeaders = req.getDebugHeaders(),
                )
            }
        }

        transportServer.handle(
            method = HttpMethod.PUT,
            path = basePathSegments,
        ) { req ->
            req.respondObject(timeProvider.now()) {
                service.put(
                    token = req.parseAuthorizationHeader(),
                    credentials = mapper.deserializeObject<Credentials>(req.body!!),
                    debugHeaders = req.getDebugHeaders(),
                )
            }
        }

        transportServer.handle(
            method = HttpMethod.DELETE,
            path = basePathSegments,
        ) { req ->
            req.respondNothing(timeProvider.now()) {
                service.delete(token = req.parseAuthorizationHeader())
            }
        }
    }
}
