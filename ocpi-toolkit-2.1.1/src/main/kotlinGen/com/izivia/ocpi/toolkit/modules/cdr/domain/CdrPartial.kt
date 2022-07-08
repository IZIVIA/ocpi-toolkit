//  ----------
//  - WARNING -
//  ----------
//  This code is generated AND MUST NOT BE EDITED
//  ----------
package com.izivia.ocpi.toolkit.modules.cdr.domain

import com.izivia.ocpi.toolkit.modules.tariff.domain.TariffPartial
import com.izivia.ocpi.toolkit.modules.tariff.domain.toPartial
import com.izivia.ocpi.toolkit.modules.types.PricePartial
import com.izivia.ocpi.toolkit.modules.types.toPartial
import java.math.BigDecimal
import java.time.Instant
import kotlin.Boolean
import kotlin.String
import kotlin.collections.List

/**
 * Partial representation of [com.izivia.ocpi.toolkit.modules.cdr.domain.Cdr]
 */
public data class CdrPartial(
  public val id: String?,
  public val country_code: String?,
  public val party_id: String?,
  public val start_date_time: Instant?,
  public val end_date_time: Instant?,
  public val session_id: String?,
  public val cdr_token: CdrTokenPartial?,
  public val auth_method: AuthMethod?,
  public val authorization_reference: String?,
  public val cdr_location: CdrLocation?,
  public val meter_id: String?,
  public val currency: String?,
  public val tariffs: List<TariffPartial>?,
  public val charging_periods: List<ChargingPeriodPartial>?,
  public val signed_data: SignedDataPartial?,
  public val total_cost: PricePartial?,
  public val total_fixed_cost: PricePartial?,
  public val total_energy: BigDecimal?,
  public val total_energy_cost: BigDecimal?,
  public val total_time: BigDecimal?,
  public val total_time_cost: PricePartial?,
  public val total_parking_time: BigDecimal?,
  public val total_parking_cost: PricePartial?,
  public val total_reservation_cost: PricePartial?,
  public val remark: String?,
  public val invoice_reference_id: String?,
  public val credit: Boolean?,
  public val credit_reference_id: String?,
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
    cdr_token = cdr_token.toPartial(),
    auth_method = auth_method,
    authorization_reference = authorization_reference,
    cdr_location = cdr_location,
    meter_id = meter_id,
    currency = currency,
    tariffs = tariffs.toPartial(),
    charging_periods = charging_periods.toPartial(),
    signed_data = signed_data?.toPartial(),
    total_cost = total_cost.toPartial(),
    total_fixed_cost = total_fixed_cost?.toPartial(),
    total_energy = total_energy,
    total_energy_cost = total_energy_cost,
    total_time = total_time,
    total_time_cost = total_time_cost?.toPartial(),
    total_parking_time = total_parking_time,
    total_parking_cost = total_parking_cost?.toPartial(),
    total_reservation_cost = total_reservation_cost?.toPartial(),
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
