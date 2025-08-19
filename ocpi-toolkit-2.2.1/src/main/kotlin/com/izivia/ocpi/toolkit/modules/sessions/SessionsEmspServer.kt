package com.izivia.ocpi.toolkit.modules.sessions

import com.izivia.ocpi.toolkit.common.*
import com.izivia.ocpi.toolkit.modules.sessions.domain.Session
import com.izivia.ocpi.toolkit.modules.sessions.domain.SessionPartial
import com.izivia.ocpi.toolkit.modules.versions.domain.InterfaceRole
import com.izivia.ocpi.toolkit.modules.versions.domain.ModuleID
import com.izivia.ocpi.toolkit.modules.versions.domain.VersionNumber
import com.izivia.ocpi.toolkit.modules.versions.repositories.MutableVersionsRepository
import com.izivia.ocpi.toolkit.transport.TransportServer
import com.izivia.ocpi.toolkit.transport.domain.HttpMethod
import com.izivia.ocpi.toolkit.transport.domain.VariablePathSegment
import java.time.Instant

class SessionsEmspServer(
    private val service: SessionsEmspInterface,
    private val timeProvider: TimeProvider = TimeProvider { Instant.now() },
    versionsRepository: MutableVersionsRepository? = null,
    basePathOverride: String? = null,
) : OcpiSelfRegisteringModuleServer(
    ocpiVersion = VersionNumber.V2_2_1,
    moduleID = ModuleID.sessions,
    interfaceRole = InterfaceRole.RECEIVER,
    versionsRepository = versionsRepository,
    basePathOverride = basePathOverride,
) {

    override suspend fun doRegisterOn(transportServer: TransportServer) {
        transportServer.handle(
            method = HttpMethod.GET,
            path = basePathSegments + listOf(
                VariablePathSegment("countryCode"),
                VariablePathSegment("partyId"),
                VariablePathSegment("sessionId"),
            ),
        ) { req ->
            req.respondObject(timeProvider.now()) {
                service
                    .getSession(
                        countryCode = req.pathParam("countryCode"),
                        partyId = req.pathParam("partyId"),
                        sessionId = req.pathParam("sessionId"),
                    )
            }
        }

        transportServer.handle(
            method = HttpMethod.PUT,
            path = basePathSegments + listOf(
                VariablePathSegment("countryCode"),
                VariablePathSegment("partyId"),
                VariablePathSegment("sessionId"),
            ),
        ) { req ->
            req.respondObject(timeProvider.now()) {
                service
                    .putSession(
                        countryCode = req.pathParam("countryCode"),
                        partyId = req.pathParam("partyId"),
                        sessionId = req.pathParam("sessionId"),
                        session = mapper.readValue(req.body, Session::class.java),
                    )
            }
        }

        transportServer.handle(
            method = HttpMethod.PATCH,
            path = basePathSegments + listOf(
                VariablePathSegment("countryCode"),
                VariablePathSegment("partyId"),
                VariablePathSegment("sessionId"),
            ),
        ) { req ->
            req.respondObject(timeProvider.now()) {
                service
                    .patchSession(
                        countryCode = req.pathParam("countryCode"),
                        partyId = req.pathParam("partyId"),
                        sessionId = req.pathParam("sessionId"),
                        session = mapper.readValue(req.body, SessionPartial::class.java),
                    )
            }
        }
    }
}
