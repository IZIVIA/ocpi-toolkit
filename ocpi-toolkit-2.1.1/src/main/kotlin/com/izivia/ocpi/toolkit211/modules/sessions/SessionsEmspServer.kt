package com.izivia.ocpi.toolkit211.modules.sessions

import com.izivia.ocpi.toolkit.transport.TransportServer
import com.izivia.ocpi.toolkit.transport.domain.HttpMethod
import com.izivia.ocpi.toolkit.transport.domain.VariablePathSegment
import com.izivia.ocpi.toolkit211.common.*
import com.izivia.ocpi.toolkit211.modules.sessions.domain.Session
import com.izivia.ocpi.toolkit211.modules.sessions.domain.SessionPartial
import com.izivia.ocpi.toolkit211.modules.versions.domain.ModuleID
import com.izivia.ocpi.toolkit211.modules.versions.domain.VersionNumber
import com.izivia.ocpi.toolkit211.modules.versions.repositories.MutableVersionsRepository
import com.izivia.ocpi.toolkit211.serialization.deserializeObject
import com.izivia.ocpi.toolkit211.serialization.mapper
import java.time.Instant

class SessionsEmspServer(
    private val service: SessionsEmspInterface,
    private val timeProvider: TimeProvider = TimeProvider { Instant.now() },
    versionsRepository: MutableVersionsRepository? = null,
    basePathOverride: String? = null,
) : OcpiSelfRegisteringModuleServer(
    ocpiVersion = VersionNumber.V2_1_1,
    moduleID = ModuleID.sessions,
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
                service.getSession(
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
                service.putSession(
                    countryCode = req.pathParam("countryCode"),
                    partyId = req.pathParam("partyId"),
                    sessionId = req.pathParam("sessionId"),
                    session = mapper.deserializeObject<Session>(req.body),
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
                service.patchSession(
                    countryCode = req.pathParam("countryCode"),
                    partyId = req.pathParam("partyId"),
                    sessionId = req.pathParam("sessionId"),
                    session = mapper.deserializeObject<SessionPartial>(req.body),
                )
            }
        }
    }
}
