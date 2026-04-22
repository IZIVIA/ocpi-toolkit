package com.izivia.ocpi.toolkit211.modules.sessions.domain

import com.izivia.ocpi.toolkit.annotations.Partial
import com.izivia.ocpi.toolkit211.common.CiString
import com.izivia.ocpi.toolkit211.modules.cdr.domain.AuthMethod
import com.izivia.ocpi.toolkit211.modules.cdr.domain.ChargingPeriod
import com.izivia.ocpi.toolkit211.modules.types.Price
import java.math.BigDecimal
import java.time.Instant

@Partial
data class Session(
    val id: CiString,
    val startDateTime: Instant,
    val endDateTime: Instant? = null,
    val kwh: BigDecimal,
    val authId: CiString,
    val authMethod: AuthMethod,
    val locationId: CiString,
    val evseUid: CiString,
    val connectorId: CiString,
    val meterId: String? = null,
    val currency: CiString,
    val chargingPeriods: List<ChargingPeriod>? = null,
    val totalCost: Price? = null,
    val status: SessionStatusType,
    val lastUpdated: Instant,
)
