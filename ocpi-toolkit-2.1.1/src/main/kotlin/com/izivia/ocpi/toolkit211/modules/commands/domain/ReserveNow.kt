package com.izivia.ocpi.toolkit211.modules.commands.domain

import com.izivia.ocpi.toolkit211.common.CiString
import com.izivia.ocpi.toolkit211.modules.tokens.domain.Token
import java.time.Instant

data class ReserveNow(
    val token: Token,
    val expiryDate: Instant,
    val reservationId: CiString,
    val locationId: CiString,
    val evseUid: CiString?,
)
