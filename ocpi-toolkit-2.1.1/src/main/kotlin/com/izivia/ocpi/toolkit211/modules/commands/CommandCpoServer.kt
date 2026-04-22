package com.izivia.ocpi.toolkit211.modules.commands

import com.izivia.ocpi.toolkit.transport.TransportServer
import com.izivia.ocpi.toolkit.transport.domain.FixedPathSegment
import com.izivia.ocpi.toolkit.transport.domain.HttpMethod
import com.izivia.ocpi.toolkit211.common.*
import com.izivia.ocpi.toolkit211.modules.commands.domain.*
import com.izivia.ocpi.toolkit211.modules.versions.domain.ModuleID
import com.izivia.ocpi.toolkit211.modules.versions.domain.VersionNumber
import com.izivia.ocpi.toolkit211.modules.versions.repositories.MutableVersionsRepository
import com.izivia.ocpi.toolkit211.serialization.deserializeObject
import com.izivia.ocpi.toolkit211.serialization.mapper
import java.time.Instant

class CommandCpoServer(
    private val httpAuth: HttpAuthInterface,
    private val service: CommandCpoInterface,
    private val timeProvider: TimeProvider = TimeProvider { Instant.now() },
    versionsRepository: MutableVersionsRepository? = null,
    basePathOverride: String? = null,
) : OcpiSelfRegisteringModuleServer(
    ocpiVersion = VersionNumber.V2_1_1,
    moduleID = ModuleID.commands,
    versionsRepository = versionsRepository,
    basePathOverride = basePathOverride,
) {

    override suspend fun doRegisterOn(transportServer: TransportServer) {
        transportServer.handle(
            method = HttpMethod.POST,
            path = basePathSegments + FixedPathSegment("START_SESSION"),
        ) { req ->
            val senderPlatformId = httpAuth.partnerIdFromRequest(req)
            val startSession = mapper.deserializeObject<StartSession>(req.body)
            req.respondObject(timeProvider.now()) {
                service.postStartSession(senderPlatformId, startSession)
            }
        }

        transportServer.handle(
            method = HttpMethod.POST,
            path = basePathSegments + FixedPathSegment("STOP_SESSION"),
        ) { req ->
            val senderPlatformId = httpAuth.partnerIdFromRequest(req)
            val stopSession = mapper.deserializeObject<StopSession>(req.body)
            req.respondObject(timeProvider.now()) {
                service.postStopSession(senderPlatformId, stopSession)
            }
        }

        transportServer.handle(
            method = HttpMethod.POST,
            path = basePathSegments + FixedPathSegment("RESERVE_NOW"),
        ) { req ->
            val senderPlatformId = httpAuth.partnerIdFromRequest(req)
            val reserveNow = mapper.deserializeObject<ReserveNow>(req.body)
            req.respondObject(timeProvider.now()) {
                service.postReserveNow(senderPlatformId, reserveNow)
            }
        }

        transportServer.handle(
            method = HttpMethod.POST,
            path = basePathSegments + FixedPathSegment("UNLOCK_CONNECTOR"),
        ) { req ->
            val senderPlatformId = httpAuth.partnerIdFromRequest(req)
            val unlockConnector = mapper.deserializeObject<UnlockConnector>(req.body)
            req.respondObject(timeProvider.now()) {
                service.postUnlockConnector(senderPlatformId, unlockConnector)
            }
        }
    }
}
