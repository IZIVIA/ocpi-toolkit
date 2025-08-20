package com.izivia.ocpi.toolkit.modules.commands

import com.izivia.ocpi.toolkit.modules.commands.domain.*

interface CommandCpoInterface {
    suspend fun postStartSession(
        partnerId: String,
        startSession: StartSession,
    ): CommandResponse

    suspend fun postStopSession(
        partnerId: String,
        stopSession: StopSession,
    ): CommandResponse

    suspend fun postReserveNow(
        partnerId: String,
        reserveNow: ReserveNow,
    ): CommandResponse

    suspend fun postCancelReservation(
        partnerId: String,
        cancelReservation: CancelReservation,
    ): CommandResponse

    suspend fun postUnlockConnector(
        partnerId: String,
        unlockConnector: UnlockConnector,
    ): CommandResponse
}
