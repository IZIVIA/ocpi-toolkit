package com.izivia.ocpi.toolkit211.modules.commands

import com.izivia.ocpi.toolkit.transport.TransportClient
import com.izivia.ocpi.toolkit.transport.domain.HttpMethod
import com.izivia.ocpi.toolkit.transport.domain.HttpRequest
import com.izivia.ocpi.toolkit211.common.CiString
import com.izivia.ocpi.toolkit211.common.TransportClientBuilder
import com.izivia.ocpi.toolkit211.common.parseResult
import com.izivia.ocpi.toolkit211.modules.commands.domain.*
import com.izivia.ocpi.toolkit211.modules.tokens.domain.Token
import com.izivia.ocpi.toolkit211.modules.versions.domain.ModuleID
import com.izivia.ocpi.toolkit211.serialization.mapper
import com.izivia.ocpi.toolkit211.serialization.serializeObject
import java.time.Instant

/**
 * Sends commands to the CPO server
 */
class CommandEmspClient(
    private val transportClientBuilder: TransportClientBuilder,
    private val partnerId: String,
) {

    private suspend fun buildTransport(): TransportClient = transportClientBuilder
        .buildFor(
            partnerId = partnerId,
            module = ModuleID.commands,
        )

    suspend fun postStartSession(
        token: Token,
        locationId: CiString,
        evseUid: CiString?,
        connectorId: CiString?,
    ): CommandResponse = with(buildTransport()) {
        send(
            HttpRequest(
                method = HttpMethod.POST,
                path = "/START_SESSION",
                body = mapper.serializeObject(
                    StartSession(
                        token = token,
                        locationId = locationId,
                        evseUid = evseUid,
                        connectorId = connectorId,
                    ),
                ),
            ),
        ).parseResult()
    }

    suspend fun postStopSession(
        sessionId: CiString,
    ): CommandResponse = with(buildTransport()) {
        send(
            HttpRequest(
                method = HttpMethod.POST,
                path = "/STOP_SESSION",
                body = mapper.serializeObject(StopSession(sessionId = sessionId)),
            ),
        ).parseResult()
    }

    suspend fun postReserveNow(
        token: Token,
        expiryDate: Instant,
        reservationId: CiString,
        locationId: CiString,
        evseUid: CiString?,
    ): CommandResponse = with(buildTransport()) {
        send(
            HttpRequest(
                method = HttpMethod.POST,
                path = "/RESERVE_NOW",
                body = mapper.serializeObject(
                    ReserveNow(
                        token = token,
                        expiryDate = expiryDate,
                        reservationId = reservationId,
                        locationId = locationId,
                        evseUid = evseUid,
                    ),
                ),
            ),
        ).parseResult()
    }

    suspend fun postUnlockConnector(
        locationId: CiString,
        evseUid: CiString,
        connectorId: CiString,
    ): CommandResponse = with(buildTransport()) {
        send(
            HttpRequest(
                method = HttpMethod.POST,
                path = "/UNLOCK_CONNECTOR",
                body = mapper.serializeObject(
                    UnlockConnector(
                        locationId = locationId,
                        evseUid = evseUid,
                        connectorId = connectorId,
                    ),
                ),
            ),
        ).parseResult()
    }
}
