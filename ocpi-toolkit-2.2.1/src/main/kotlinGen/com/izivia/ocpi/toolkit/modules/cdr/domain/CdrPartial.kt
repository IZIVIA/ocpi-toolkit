//  ----------
//  - WARNING -
//  ----------
//  This code is generated AND MUST NOT BE EDITED
//  ----------
package com.izivia.ocpi.toolkit.modules.cdr.domain

import com.izivia.ocpi.toolkit.common.CiString
import com.izivia.ocpi.toolkit.modules.tariff.domain.Tariff
import com.izivia.ocpi.toolkit.modules.types.Price
import java.math.BigDecimal
import java.time.Instant
import kotlin.Boolean
import kotlin.String
import kotlin.collections.List

/**
 * Partial representation of [com.izivia.ocpi.toolkit.modules.cdr.domain.Cdr]
 */
public data class CdrPartial(
  public val id: CiString?,
  public val country_code: CiString?,
  public val party_id: CiString?,
  public val start_date_time: Instant?,
  public val end_date_time: Instant?,
  public val session_id: CiString?,
  public val cdr_token: CdrToken?,
  public val auth_method: AuthMethod?,
  public val authorization_reference: String?,
  public val cdr_location: CdrLocation?,
  public val meter_id: String?,
  public val currency: String?,
  public val tariffs: List<Tariff>?,
  public val charging_periods: List<ChargingPeriod>?,
  public val signed_data: SignedData?,
  public val total_cost: Price?,
  public val total_fixed_cost: Price?,
  public val total_energy: BigDecimal?,
  public val total_energy_cost: Price?,
  public val total_time: BigDecimal?,
  public val total_time_cost: Price?,
  public val total_parking_time: BigDecimal?,
  public val total_parking_cost: Price?,
  public val total_reservation_cost: Price?,
  public val remark: String?,
  public val invoice_reference_id: CiString?,
  public val credit: Boolean?,
  public val credit_reference_id: CiString?,
  public val last_updated: Instant?,
)

public fun Cdr.toPartial(): CdrPartial {
   return CdrPartial(
     id = id,
    country_code = country_code,
    party_id = party_id,
    start_date_time = start_date_time,
    end_date_time = end_date_time,
    session_id = session_id,
    cdr_token = cdr_token,
    auth_method = auth_method,
    authorization_reference = authorization_reference,
    cdr_location = cdr_location,
    meter_id = meter_id,
    currency = currency,
    tariffs = tariffs,
    charging_periods = charging_periods,
    signed_data = signed_data,
    total_cost = total_cost,
    total_fixed_cost = total_fixed_cost,
    total_energy = total_energy,
    total_energy_cost = total_energy_cost,
    total_time = total_time,
    total_time_cost = total_time_cost,
    total_parking_time = total_parking_time,
    total_parking_cost = total_parking_cost,
    total_reservation_cost = total_reservation_cost,
    remark = remark,
    invoice_reference_id = invoice_reference_id,
    credit = credit,
    credit_reference_id = credit_reference_id,
    last_updated = last_updated
   )
}

public fun List<Cdr>.toPartial(): List<CdrPartial> {
   return mapNotNull { it.toPartial() }
}
