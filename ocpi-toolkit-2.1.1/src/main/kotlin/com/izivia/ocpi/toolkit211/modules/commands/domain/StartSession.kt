package com.izivia.ocpi.toolkit211.modules.commands.domain

import com.izivia.ocpi.toolkit211.common.CiString
import com.izivia.ocpi.toolkit211.modules.tokens.domain.Token

data class StartSession(
    val token: Token,
    val locationId: CiString,
    val evseUid: CiString?,
    val connectorId: CiString?,
)
