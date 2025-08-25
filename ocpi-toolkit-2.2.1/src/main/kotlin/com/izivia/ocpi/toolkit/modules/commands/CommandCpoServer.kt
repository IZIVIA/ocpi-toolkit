package com.izivia.ocpi.toolkit.modules.commands

import com.izivia.ocpi.toolkit.common.*
import com.izivia.ocpi.toolkit.modules.commands.domain.*
import com.izivia.ocpi.toolkit.modules.versions.domain.InterfaceRole
import com.izivia.ocpi.toolkit.modules.versions.domain.ModuleID
import com.izivia.ocpi.toolkit.modules.versions.domain.VersionNumber
import com.izivia.ocpi.toolkit.modules.versions.repositories.MutableVersionsRepository
import com.izivia.ocpi.toolkit.transport.TransportServer
import com.izivia.ocpi.toolkit.transport.domain.FixedPathSegment
import com.izivia.ocpi.toolkit.transport.domain.HttpMethod
import java.time.Instant

class CommandCpoServer(
    private val httpAuth: HttpAuthInterface,
    private val service: CommandCpoInterface,
    private val timeProvider: TimeProvider = TimeProvider { Instant.now() },
    versionsRepository: MutableVersionsRepository? = null,
    basePathOverride: String? = null,
) : OcpiSelfRegisteringModuleServer(
    ocpiVersion = VersionNumber.V2_2_1,
    moduleID = ModuleID.commands,
    interfaceRole = InterfaceRole.RECEIVER,
    versionsRepository = versionsRepository,
    basePathOverride = basePathOverride,
) {

    override suspend fun doRegisterOn(transportServer: TransportServer) {
        transportServer.handle(
            method = HttpMethod.POST,
            path = basePathSegments + FixedPathSegment("START_SESSION"),
        ) { req ->
            val senderPlatformId = httpAuth.partnerIdFromRequest(req)
            val startSession = mapper.readValue(req.body, StartSession::class.java)

            req.respondObject(timeProvider.now()) {
                service.postStartSession(senderPlatformId, startSession)
            }
        }

        transportServer.handle(
            method = HttpMethod.POST,
            path = basePathSegments + FixedPathSegment("STOP_SESSION"),
        ) { req ->
            val senderPlatformUrl = httpAuth.partnerIdFromRequest(req)
            val stopSession = mapper.readValue(req.body, StopSession::class.java)

            req.respondObject(timeProvider.now()) {
                service.postStopSession(senderPlatformUrl, stopSession = stopSession)
            }
        }

        transportServer.handle(
            method = HttpMethod.POST,
            path = basePathSegments + FixedPathSegment("RESERVE_NOW"),
        ) { req ->
            val senderPlatformUrl = httpAuth.partnerIdFromRequest(req)
            val reserveNow = mapper.readValue(req.body, ReserveNow::class.java)

            req.respondObject(timeProvider.now()) {
                service.postReserveNow(senderPlatformUrl, reserveNow)
            }
        }

        transportServer.handle(
            method = HttpMethod.POST,
            path = basePathSegments + FixedPathSegment("CANCEL_RESERVATION"),
        ) { req ->
            val senderPlatformUrl = httpAuth.partnerIdFromRequest(req)
            val cancelReservation = mapper.readValue(req.body, CancelReservation::class.java)

            req.respondObject(timeProvider.now()) {
                service.postCancelReservation(senderPlatformUrl, cancelReservation)
            }
        }

        transportServer.handle(
            method = HttpMethod.POST,
            path = basePathSegments + FixedPathSegment("UNLOCK_CONNECTOR"),
        ) { req ->
            val senderPlatformUrl = httpAuth.partnerIdFromRequest(req)
            val unlockConnector = mapper.readValue(req.body, UnlockConnector::class.java)

            req.respondObject(timeProvider.now()) {
                service.postUnlockConnector(senderPlatformUrl, unlockConnector)
            }
        }
    }
}
