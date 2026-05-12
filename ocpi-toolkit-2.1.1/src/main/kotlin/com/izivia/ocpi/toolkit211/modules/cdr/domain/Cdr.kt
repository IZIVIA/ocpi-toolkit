package com.izivia.ocpi.toolkit211.modules.cdr.domain

import com.izivia.ocpi.toolkit.annotations.Partial
import com.izivia.ocpi.toolkit211.common.CiString
import com.izivia.ocpi.toolkit211.modules.tariff.domain.Tariff
import com.izivia.ocpi.toolkit211.modules.types.Price
import java.math.BigDecimal
import java.time.Instant

@Partial
data class Cdr(
    val id: CiString,
    val startDateTime: Instant,
    val endDateTime: Instant,
    val authId: CiString,
    val authMethod: AuthMethod,
    val locationId: CiString,
    val evseId: CiString,
    val connectorId: CiString,
    val meterId: String? = null,
    val currency: String,
    val tariffs: List<Tariff>?,
    val chargingPeriods: List<ChargingPeriod>,
    val totalCost: Price,
    val totalEnergy: BigDecimal,
    val totalTime: BigDecimal,
    val totalParkingTime: BigDecimal? = null,
    val remark: String? = null,
    val lastUpdated: Instant,
)
