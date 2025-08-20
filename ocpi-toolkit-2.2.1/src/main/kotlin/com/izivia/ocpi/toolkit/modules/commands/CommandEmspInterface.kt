package com.izivia.ocpi.toolkit.modules.commands

import com.izivia.ocpi.toolkit.modules.commands.domain.CommandResult

interface CommandEmspInterface {
    suspend fun postCallbackStartSession(
        authorizationReference: String,
        result: CommandResult,
    )

    suspend fun postCallbackStopSession(
        sessionId: String,
        result: CommandResult,
    )

    suspend fun postCallbackReserveNow(
        reservationId: String,
        result: CommandResult,
    )

    suspend fun postCallbackCancelReservation(
        reservationId: String,
        result: CommandResult,
    )

    suspend fun postCallbackUnlockConnector(
        locationId: String,
        evseId: String,
        connectorId: String,
        result: CommandResult,
    )
}
