package com.izivia.ocpi.toolkit211.modules.commands

import com.izivia.ocpi.toolkit211.modules.commands.domain.*

/**
 * Typically implemented by market roles like: CPO.
 *
 * - POST: Send a command to the CPO, requesting the CPO to perform the command with the following actions possible:
 *   RESERVE_NOW, START_SESSION, STOP_SESSION, UNLOCK_CONNECTOR.
 */
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

    suspend fun postUnlockConnector(
        partnerId: String,
        unlockConnector: UnlockConnector,
    ): CommandResponse
}
