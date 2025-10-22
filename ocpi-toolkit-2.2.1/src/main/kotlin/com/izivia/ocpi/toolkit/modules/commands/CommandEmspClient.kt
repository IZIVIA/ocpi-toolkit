package com.izivia.ocpi.toolkit.modules.commands

import com.izivia.ocpi.toolkit.common.*
import com.izivia.ocpi.toolkit.modules.commands.domain.*
import com.izivia.ocpi.toolkit.modules.credentials.repositories.PartnerRepository
import com.izivia.ocpi.toolkit.modules.tokens.domain.Token
import com.izivia.ocpi.toolkit.modules.versions.domain.ModuleID
import com.izivia.ocpi.toolkit.serialization.mapper
import com.izivia.ocpi.toolkit.serialization.serializeObject
import com.izivia.ocpi.toolkit.transport.TransportClient
import com.izivia.ocpi.toolkit.transport.TransportClientBuilder
import com.izivia.ocpi.toolkit.transport.domain.HttpMethod
import com.izivia.ocpi.toolkit.transport.domain.HttpRequest
import java.time.Instant

class CommandEmspClient(
    private val transportClientBuilder: TransportClientBuilder,
    private val partnerId: String,
    private val partnerRepository: PartnerRepository,
    private val callbackBaseUrl: String,
) {

    private suspend fun buildTransport(): TransportClient = transportClientBuilder
        .buildFor(
            module = ModuleID.commands,
            partnerId = partnerId,
            partnerRepository = partnerRepository,
        )

    suspend fun postStartSession(
        token: Token,
        locationId: CiString,
        evseId: CiString?,
        connectorId: CiString?,
        authorizationReference: CiString,
        callbackReference: String = authorizationReference,
    ): CommandResponse =
        with(buildTransport()) {
            send(
                HttpRequest(
                    method = HttpMethod.POST,
                    path = "/START_SESSION",
                    body = mapper.serializeObject(
                        StartSession(
                            responseUrl = "$callbackBaseUrl/START_SESSION/callback/$callbackReference",
                            token = token,
                            locationId = locationId,
                            evseUid = evseId,
                            connectorId = connectorId,
                            authorizationReference = authorizationReference,
                        ),
                    ),
                )
                    .withRequiredHeaders(
                        requestId = generateUUIDv4Token(),
                        correlationId = generateUUIDv4Token(),
                    )
                    .authenticate(partnerRepository = partnerRepository, partnerId = partnerId),
            )
                .parseResult()
        }

    suspend fun postStopSession(
        sessionId: CiString,
        callbackReference: String = sessionId,
    ): CommandResponse =
        with(buildTransport()) {
            send(
                HttpRequest(
                    method = HttpMethod.POST,
                    path = "/STOP_SESSION",
                    body = mapper.serializeObject(
                        StopSession(
                            responseUrl = "$callbackBaseUrl/STOP_SESSION/callback/$callbackReference",
                            sessionId = sessionId,
                        ),
                    ),
                )
                    .withRequiredHeaders(
                        requestId = generateUUIDv4Token(),
                        correlationId = generateUUIDv4Token(),
                    )
                    .authenticate(partnerRepository = partnerRepository, partnerId = partnerId),
            )
                .parseResult()
        }

    suspend fun postReserveNow(
        token: Token,
        expiryDate: Instant,
        reservationId: CiString,
        locationId: CiString,
        evseUid: CiString?,
        authorizationReference: CiString?,
        callbackReference: String = reservationId,
    ): CommandResponse =
        with(buildTransport()) {
            send(
                HttpRequest(
                    method = HttpMethod.POST,
                    path = "/RESERVE_NOW",
                    body = mapper.serializeObject(
                        ReserveNow(
                            responseUrl = "$callbackBaseUrl/RESERVE_NOW/callback/$callbackReference",
                            token = token,
                            expiryDate = expiryDate,
                            reservationId = reservationId,
                            locationId = locationId,
                            evseUid = evseUid,
                            authorizationReference = authorizationReference,
                        ),
                    ),
                )
                    .withRequiredHeaders(
                        requestId = generateUUIDv4Token(),
                        correlationId = generateUUIDv4Token(),
                    )
                    .authenticate(partnerRepository = partnerRepository, partnerId = partnerId),
            )
                .parseResult()
        }

    suspend fun postCancelReservation(
        reservationId: CiString,
        callbackReference: String = reservationId,
    ): CommandResponse =
        with(buildTransport()) {
            send(
                HttpRequest(
                    method = HttpMethod.POST,
                    path = "/CANCEL_RESERVATION",
                    body = mapper.serializeObject(
                        CancelReservation(
                            responseUrl = "$callbackBaseUrl/CANCEL_RESERVATION/callback/$callbackReference",
                            reservationId = reservationId,
                        ),
                    ),
                )
                    .withRequiredHeaders(
                        requestId = generateUUIDv4Token(),
                        correlationId = generateUUIDv4Token(),
                    )
                    .authenticate(partnerRepository = partnerRepository, partnerId = partnerId),
            )
                .parseResult()
        }

    suspend fun postUnlockConnector(
        locationId: CiString,
        evseUid: CiString,
        connectorId: CiString,
        callbackReference: String,
    ): CommandResponse =
        with(buildTransport()) {
            send(
                HttpRequest(
                    method = HttpMethod.POST,
                    path = "/UNLOCK_CONNECTOR",
                    body = mapper.serializeObject(
                        UnlockConnector(
                            responseUrl = "$callbackBaseUrl/UNLOCK_CONNECTOR/callback/$callbackReference",
                            locationId = locationId,
                            evseUid = evseUid,
                            connectorId = connectorId,
                        ),
                    ),
                )
                    .withRequiredHeaders(
                        requestId = generateUUIDv4Token(),
                        correlationId = generateUUIDv4Token(),
                    )
                    .authenticate(partnerRepository = partnerRepository, partnerId = partnerId),
            )
                .parseResult()
        }
}
