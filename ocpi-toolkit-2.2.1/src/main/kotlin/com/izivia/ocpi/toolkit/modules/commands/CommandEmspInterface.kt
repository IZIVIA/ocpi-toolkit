package com.izivia.ocpi.toolkit.modules.commands

import com.izivia.ocpi.toolkit.modules.commands.domain.CommandResult

interface CommandEmspInterface {
    suspend fun postCallbackStartSession(
        callbackReference: String,
        result: CommandResult,
    )

    suspend fun postCallbackStopSession(
        callbackReference: String,
        result: CommandResult,
    )

    suspend fun postCallbackReserveNow(
        callbackReference: String,
        result: CommandResult,
    )

    suspend fun postCallbackCancelReservation(
        callbackReference: String,
        result: CommandResult,
    )

    suspend fun postCallbackUnlockConnector(
        callbackReference: String,
        result: CommandResult,
    )
}
