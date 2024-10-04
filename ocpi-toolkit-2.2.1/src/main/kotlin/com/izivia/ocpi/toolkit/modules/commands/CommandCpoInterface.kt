package com.izivia.ocpi.toolkit.modules.commands

import com.izivia.ocpi.toolkit.common.OcpiResponseBody
import com.izivia.ocpi.toolkit.modules.commands.domain.*

interface CommandCpoInterface {
    suspend fun postStartSession(
        partnerId: String,
        startSession: StartSession,
    ): OcpiResponseBody<CommandResponse>

    suspend fun postStopSession(
        partnerId: String,
        stopSession: StopSession,
    ): OcpiResponseBody<CommandResponse>

    suspend fun postReserveNow(
        partnerId: String,
        reserveNow: ReserveNow,
    ): OcpiResponseBody<CommandResponse>

    suspend fun postCancelReservation(
        partnerId: String,
        cancelReservation: CancelReservation,
    ): OcpiResponseBody<CommandResponse>

    suspend fun postUnlockConnector(
        partnerId: String,
        unlockConnector: UnlockConnector,
    ): OcpiResponseBody<CommandResponse>
}
