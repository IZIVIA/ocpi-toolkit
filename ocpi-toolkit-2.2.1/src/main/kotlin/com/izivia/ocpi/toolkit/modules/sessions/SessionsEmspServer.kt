package com.izivia.ocpi.toolkit.modules.sessions

import com.izivia.ocpi.toolkit.common.OcpiModuleServer
import com.izivia.ocpi.toolkit.common.httpResponse
import com.izivia.ocpi.toolkit.common.mapper
import com.izivia.ocpi.toolkit.modules.sessions.domain.Session
import com.izivia.ocpi.toolkit.transport.TransportServer
import com.izivia.ocpi.toolkit.transport.domain.HttpMethod
import com.izivia.ocpi.toolkit.transport.domain.VariablePathSegment

/**
 * Receives calls from a CPO
 * @property transportServer
 */
class SessionsEmspServer(
    private val service: SessionsEmspInterface,
    basePath: String = "/2.2.1/sessions"
) : OcpiModuleServer(basePath) {
    override fun registerOn(transportServer: TransportServer) {
        transportServer.handle(
            method = HttpMethod.GET,
            path = basePathSegments + listOf(
                VariablePathSegment("countryCode"),
                VariablePathSegment("partyId"),
                VariablePathSegment("sessionId")
            )
        ) { req ->
            req.httpResponse {
                service
                    .getSession(
                        countryCode = req.pathParams["countryCode"]!!,
                        partyId = req.pathParams["partyId"]!!,
                        sessionId = req.pathParams["sessionId"]!!
                    )
            }
        }

        transportServer.handle(
            method = HttpMethod.PUT,
            path = basePathSegments + listOf(
                VariablePathSegment("countryCode"),
                VariablePathSegment("partyId"),
                VariablePathSegment("sessionId")
            )
        ) { req ->
            req.httpResponse {
                service
                    .putSession(
                        countryCode = req.pathParams["countryCode"]!!,
                        partyId = req.pathParams["partyId"]!!,
                        sessionId = req.pathParams["sessionId"]!!,
                        session = mapper.readValue(req.body, Session::class.java)
                    )
            }
        }

        transportServer.handle(
            method = HttpMethod.PATCH,
            path = basePathSegments + listOf(
                VariablePathSegment("countryCode"),
                VariablePathSegment("partyId"),
                VariablePathSegment("sessionId")
            )
        ) { req ->
            req.httpResponse {
                service
                    .patchSession(
                        countryCode = req.pathParams["countryCode"]!!,
                        partyId = req.pathParams["partyId"]!!,
                        sessionId = req.pathParams["sessionId"]!!,
                        session = mapper.readValue(req.body, Session::class.java)
                    )
            }
        }
    }
}
