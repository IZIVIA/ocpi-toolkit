package com.izivia.ocpi.toolkit.modules.commands.domain

import com.izivia.ocpi.toolkit.modules.types.DisplayText

/**
 * @property result Result of the command request as sent by the Charge Point to the CPO.
 *
 * @property message Human-readable description of the reason (if one can be provided), multiple languages can be
 * provided.
 */
data class CommandResult(
    val result: CommandResultType,
    val message: List<DisplayText>,
)
