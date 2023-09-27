package com.izivia.ocpi.toolkit.modules.sessions

import com.izivia.ocpi.toolkit.common.OcpiModuleServer
import com.izivia.ocpi.toolkit.common.httpResponse
import com.izivia.ocpi.toolkit.common.mapper
import com.izivia.ocpi.toolkit.modules.sessions.domain.ChargingPreferences
import com.izivia.ocpi.toolkit.transport.TransportServer
import com.izivia.ocpi.toolkit.transport.domain.FixedPathSegment
import com.izivia.ocpi.toolkit.transport.domain.HttpMethod
import com.izivia.ocpi.toolkit.transport.domain.VariablePathSegment
import java.time.Instant

/**
 * Receives calls from a CPO
 * @property transportServer
 */
class SessionsCpoServer(
    private val service: SessionsCpoInterface,
    basePath: String = "/2.2.1/sessions"
) : OcpiModuleServer(basePath) {
    override suspend fun registerOn(transportServer: TransportServer) {
        transportServer.handle(
            method = HttpMethod.GET,
            path = basePathSegments,
            queryParams = listOf("date_from", "date_to", "offset", "limit")
        ) { req ->
            req.httpResponse {
                val dateFrom = req.queryParams["date_from"]
                val dateTo = req.queryParams["date_to"]

                service
                    .getSessions(
                        dateFrom = dateFrom?.let { Instant.parse(it) },
                        dateTo = dateTo?.let { Instant.parse(it) },
                        offset = req.queryParams["offset"]?.toInt() ?: 0,
                        limit = req.queryParams["limit"]?.toInt()
                    )
            }
        }

        transportServer.handle(
            method = HttpMethod.PUT,
            path = basePathSegments + listOf(
                VariablePathSegment("sessionId"),
                FixedPathSegment("charging_preferences")
            )
        ) { req ->
            req.httpResponse {
                service
                    .putChargingPreferences(
                        sessionId = req.pathParams["sessionId"]!!,
                        chargingPreferences = mapper.readValue(req.body, ChargingPreferences::class.java)
                    )
            }
        }
    }
}
