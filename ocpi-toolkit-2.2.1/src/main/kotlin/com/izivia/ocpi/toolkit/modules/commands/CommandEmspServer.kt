package com.izivia.ocpi.toolkit.modules.commands

import com.izivia.ocpi.toolkit.common.*
import com.izivia.ocpi.toolkit.modules.commands.domain.CommandResult
import com.izivia.ocpi.toolkit.modules.versions.domain.InterfaceRole
import com.izivia.ocpi.toolkit.modules.versions.domain.ModuleID
import com.izivia.ocpi.toolkit.modules.versions.domain.VersionNumber
import com.izivia.ocpi.toolkit.modules.versions.repositories.MutableVersionsRepository
import com.izivia.ocpi.toolkit.transport.TransportServer
import com.izivia.ocpi.toolkit.transport.domain.FixedPathSegment
import com.izivia.ocpi.toolkit.transport.domain.HttpMethod
import com.izivia.ocpi.toolkit.transport.domain.VariablePathSegment
import java.time.Instant

class CommandEmspServer(
    private val service: CommandEmspInterface,
    private val timeProvider: TimeProvider = TimeProvider { Instant.now() },
    versionsRepository: MutableVersionsRepository? = null,
    basePathOverride: String? = null,
) : OcpiSelfRegisteringModuleServer(
    ocpiVersion = VersionNumber.V2_2_1,
    moduleID = ModuleID.commands,
    interfaceRole = InterfaceRole.SENDER,
    versionsRepository = versionsRepository,
    basePathOverride = basePathOverride,
) {

    override suspend fun doRegisterOn(transportServer: TransportServer) {
        transportServer.handle(
            method = HttpMethod.POST,
            path = basePathSegments + listOf(
                FixedPathSegment("START_SESSION/callback"),
                VariablePathSegment("callbackReference"),
            ),
        ) { req ->
            req.respondNothing(timeProvider.now()) {
                service.postCallbackStartSession(
                    req.pathParam("callbackReference"),
                    result = mapper.readValue(req.body, CommandResult::class.java),
                )
            }
        }

        transportServer.handle(
            method = HttpMethod.POST,
            path = basePathSegments + listOf(
                FixedPathSegment("STOP_SESSION/callback"),
                VariablePathSegment("callbackReference"),
            ),
        ) { req ->
            req.respondNothing(timeProvider.now()) {
                service.postCallbackStopSession(
                    req.pathParam("callbackReference"),
                    result = mapper.readValue(req.body, CommandResult::class.java),
                )
            }
        }

        transportServer.handle(
            method = HttpMethod.POST,
            path = basePathSegments + listOf(
                FixedPathSegment("RESERVE_NOW/callback"),
                VariablePathSegment("callbackReference"),
            ),
        ) { req ->
            req.respondNothing(timeProvider.now()) {
                service.postCallbackReserveNow(
                    req.pathParam("callbackReference"),
                    result = mapper.readValue(req.body, CommandResult::class.java),
                )
            }
        }

        transportServer.handle(
            method = HttpMethod.POST,
            path = basePathSegments + listOf(
                FixedPathSegment("CANCEL_RESERVATION/callback"),
                VariablePathSegment("callbackReference"),
            ),
        ) { req ->
            req.respondNothing(timeProvider.now()) {
                service.postCallbackCancelReservation(
                    req.pathParam("callbackReference"),
                    result = mapper.readValue(req.body, CommandResult::class.java),
                )
            }
        }

        transportServer.handle(
            method = HttpMethod.POST,
            path = basePathSegments + listOf(
                FixedPathSegment("UNLOCK_CONNECTOR/callback"),
                VariablePathSegment("callbackReference"),
            ),
        ) { req ->
            req.respondNothing(timeProvider.now()) {
                service.postCallbackUnlockConnector(
                    req.pathParam("callbackReference"),
                    result = mapper.readValue(req.body, CommandResult::class.java),
                )
            }
        }
    }
}
