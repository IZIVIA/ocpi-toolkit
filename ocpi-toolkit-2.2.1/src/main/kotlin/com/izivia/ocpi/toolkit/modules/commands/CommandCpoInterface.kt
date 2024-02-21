package com.izivia.ocpi.toolkit.modules.commands

import com.izivia.ocpi.toolkit.common.OcpiResponseBody
import com.izivia.ocpi.toolkit.modules.commands.domain.*

interface CommandCpoInterface {
    suspend fun postStartSession(
        partnerUrl: String,
        startSession: StartSession,
    ): OcpiResponseBody<CommandResponse>

    suspend fun postStopSession(
        partnerUrl: String,
        stopSession: StopSession,
    ): OcpiResponseBody<CommandResponse>

    suspend fun postReserveNow(
        partnerUrl: String,
        reserveNow: ReserveNow,
    ): OcpiResponseBody<CommandResponse>

    suspend fun postCancelReservation(
        partnerUrl: String,
        cancelReservation: CancelReservation,
    ): OcpiResponseBody<CommandResponse>

    suspend fun postUnlockConnector(
        partnerUrl: String,
        unlockConnector: UnlockConnector,
    ): OcpiResponseBody<CommandResponse>
}
