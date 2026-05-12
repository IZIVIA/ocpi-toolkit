package com.izivia.ocpi.toolkit211.modules.commands.domain

import com.izivia.ocpi.toolkit211.modules.types.DisplayText

data class CommandResponse(
    val result: CommandResponseType,
    val timeout: Int,
    val message: List<DisplayText>?,
)
