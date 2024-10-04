package com.izivia.ocpi.toolkit.modules.commands

import com.izivia.ocpi.toolkit.common.HttpAuthInterface
import com.izivia.ocpi.toolkit.common.OcpiSelfRegisteringModuleServer
import com.izivia.ocpi.toolkit.common.httpResponse
import com.izivia.ocpi.toolkit.common.mapper
import com.izivia.ocpi.toolkit.modules.commands.domain.*
import com.izivia.ocpi.toolkit.modules.versions.domain.InterfaceRole
import com.izivia.ocpi.toolkit.modules.versions.domain.ModuleID
import com.izivia.ocpi.toolkit.modules.versions.domain.VersionNumber
import com.izivia.ocpi.toolkit.modules.versions.repositories.MutableVersionsRepository
import com.izivia.ocpi.toolkit.transport.TransportServer
import com.izivia.ocpi.toolkit.transport.domain.FixedPathSegment
import com.izivia.ocpi.toolkit.transport.domain.HttpMethod

class CommandCpoServer(
    private val httpAuth: HttpAuthInterface,
    private val service: CommandCpoInterface,
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

            req.httpResponse {
                service.postStartSession(senderPlatformId, startSession)
            }
        }

        transportServer.handle(
            method = HttpMethod.POST,
            path = basePathSegments + FixedPathSegment("STOP_SESSION"),
        ) { req ->
            val senderPlatformUrl = httpAuth.partnerIdFromRequest(req)
            val stopSession = mapper.readValue(req.body, StopSession::class.java)

            req.httpResponse {
                service.postStopSession(senderPlatformUrl, stopSession = stopSession)
            }
        }

        transportServer.handle(
            method = HttpMethod.POST,
            path = basePathSegments + FixedPathSegment("RESERVE_NOW"),
        ) { req ->
            val senderPlatformUrl = httpAuth.partnerIdFromRequest(req)
            val reserveNow = mapper.readValue(req.body, ReserveNow::class.java)

            req.httpResponse {
                service.postReserveNow(senderPlatformUrl, reserveNow)
            }
        }

        transportServer.handle(
            method = HttpMethod.POST,
            path = basePathSegments + FixedPathSegment("CANCEL_RESERVATION"),
        ) { req ->
            val senderPlatformUrl = httpAuth.partnerIdFromRequest(req)
            val cancelReservation = mapper.readValue(req.body, CancelReservation::class.java)

            req.httpResponse {
                service.postCancelReservation(senderPlatformUrl, cancelReservation)
            }
        }

        transportServer.handle(
            method = HttpMethod.POST,
            path = basePathSegments + FixedPathSegment("UNLOCK_CONNECTOR"),
        ) { req ->
            val senderPlatformUrl = httpAuth.partnerIdFromRequest(req)
            val unlockConnector = mapper.readValue(req.body, UnlockConnector::class.java)

            req.httpResponse {
                service.postUnlockConnector(senderPlatformUrl, unlockConnector)
            }
        }
    }
}
