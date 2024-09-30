package com.izivia.ocpi.toolkit.modules.commands.domain

import com.izivia.ocpi.toolkit.modules.types.DisplayText

/**
 * @property result Response from the CPO on the command request.
 *
 * @property timeout Timeout for this command in seconds. When the Result is not received within this timeout, the eMSP
 * can assume that the message might never be send.
 *
 * @property message Human-readable description of the result (if one can be provided), multiple languages can be
 * provided.
 */
data class CommandResponse(
    val result: CommandResponseType,
    val timeout: Int,
    val message: List<DisplayText>,
)
