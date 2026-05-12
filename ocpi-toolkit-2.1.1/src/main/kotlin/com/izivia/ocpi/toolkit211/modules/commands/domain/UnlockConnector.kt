package com.izivia.ocpi.toolkit211.modules.commands.domain

import com.izivia.ocpi.toolkit211.common.CiString

data class UnlockConnector(
    val locationId: CiString,
    val evseUid: CiString,
    val connectorId: CiString,
)
