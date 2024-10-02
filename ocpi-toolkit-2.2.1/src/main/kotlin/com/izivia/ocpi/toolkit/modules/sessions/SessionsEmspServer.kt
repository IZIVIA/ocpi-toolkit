package com.izivia.ocpi.toolkit.modules.sessions

import com.izivia.ocpi.toolkit.common.OcpiSelfRegisteringModuleServer
import com.izivia.ocpi.toolkit.common.httpResponse
import com.izivia.ocpi.toolkit.common.mapper
import com.izivia.ocpi.toolkit.modules.sessions.domain.Session
import com.izivia.ocpi.toolkit.modules.sessions.domain.SessionPartial
import com.izivia.ocpi.toolkit.modules.versions.domain.InterfaceRole
import com.izivia.ocpi.toolkit.modules.versions.domain.ModuleID
import com.izivia.ocpi.toolkit.modules.versions.domain.VersionNumber
import com.izivia.ocpi.toolkit.modules.versions.repositories.MutableVersionsRepository
import com.izivia.ocpi.toolkit.transport.TransportServer
import com.izivia.ocpi.toolkit.transport.domain.HttpMethod
import com.izivia.ocpi.toolkit.transport.domain.VariablePathSegment

class SessionsEmspServer(
    private val service: SessionsEmspInterface,
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
            req.httpResponse {
                service
                    .getSession(
                        countryCode = req.pathParams["countryCode"]!!,
                        partyId = req.pathParams["partyId"]!!,
                        sessionId = req.pathParams["sessionId"]!!,
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
            req.httpResponse {
                service
                    .putSession(
                        countryCode = req.pathParams["countryCode"]!!,
                        partyId = req.pathParams["partyId"]!!,
                        sessionId = req.pathParams["sessionId"]!!,
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
            req.httpResponse {
                service
                    .patchSession(
                        countryCode = req.pathParams["countryCode"]!!,
                        partyId = req.pathParams["partyId"]!!,
                        sessionId = req.pathParams["sessionId"]!!,
                        session = mapper.readValue(req.body, SessionPartial::class.java),
                    )
            }
        }
    }
}
