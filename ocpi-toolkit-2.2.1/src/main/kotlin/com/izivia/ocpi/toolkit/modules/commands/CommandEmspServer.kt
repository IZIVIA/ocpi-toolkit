package com.izivia.ocpi.toolkit.modules.commands

import com.izivia.ocpi.toolkit.common.OcpiSelfRegisteringModuleServer
import com.izivia.ocpi.toolkit.common.httpResponse
import com.izivia.ocpi.toolkit.common.mapper
import com.izivia.ocpi.toolkit.modules.commands.domain.CommandResult
import com.izivia.ocpi.toolkit.modules.versions.domain.InterfaceRole
import com.izivia.ocpi.toolkit.modules.versions.domain.ModuleID
import com.izivia.ocpi.toolkit.modules.versions.domain.VersionNumber
import com.izivia.ocpi.toolkit.modules.versions.repositories.MutableVersionsRepository
import com.izivia.ocpi.toolkit.transport.TransportServer
import com.izivia.ocpi.toolkit.transport.domain.FixedPathSegment
import com.izivia.ocpi.toolkit.transport.domain.HttpMethod
import com.izivia.ocpi.toolkit.transport.domain.VariablePathSegment

class CommandEmspServer(
    private val service: CommandEmspInterface,
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
                VariablePathSegment("authRef"),
            ),
        ) { req ->
            req.httpResponse {
                service.postCallbackStartSession(
                    req.pathParams["authRef"]!!,
                    result = mapper.readValue(req.body, CommandResult::class.java),
                )
            }
        }

        transportServer.handle(
            method = HttpMethod.POST,
            path = basePathSegments + listOf(
                FixedPathSegment("STOP_SESSION/callback"),
                VariablePathSegment("sessionId"),
            ),
        ) { req ->
            req.httpResponse {
                service.postCallbackStopSession(
                    req.pathParams["sessionId"]!!,
                    result = mapper.readValue(req.body, CommandResult::class.java),
                )
            }
        }

        transportServer.handle(
            method = HttpMethod.POST,
            path = basePathSegments + listOf(
                FixedPathSegment("RESERVE_NOW/callback"),
                VariablePathSegment("reservationId"),
            ),
        ) { req ->
            req.httpResponse {
                service.postCallbackReserveNow(
                    req.pathParams["reservationId"]!!,
                    result = mapper.readValue(req.body, CommandResult::class.java),
                )
            }
        }

        transportServer.handle(
            method = HttpMethod.POST,
            path = basePathSegments + listOf(
                FixedPathSegment("CANCEL_RESERVATION/callback"),
                VariablePathSegment("reservationId"),
            ),
        ) { req ->
            req.httpResponse {
                service.postCallbackCancelReservation(
                    req.pathParams["reservationId"]!!,
                    result = mapper.readValue(req.body, CommandResult::class.java),
                )
            }
        }

        transportServer.handle(
            method = HttpMethod.POST,
            path = basePathSegments + listOf(
                FixedPathSegment("UNLOCK_CONNECTOR/callback"),
                VariablePathSegment("locationId"),
                VariablePathSegment("evseId"),
                VariablePathSegment("connectorId"),
            ),
        ) { req ->
            req.httpResponse {
                service.postCallbackUnlockConnector(
                    req.pathParams["locationId"]!!,
                    req.pathParams["evseId"]!!,
                    req.pathParams["connectorId"]!!,
                    result = mapper.readValue(req.body, CommandResult::class.java),
                )
            }
        }
    }
}
