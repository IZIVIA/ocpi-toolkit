//  ----------
//  - WARNING -
//  ----------
//  This code is generated AND MUST NOT BE EDITED
//  ----------
package com.izivia.ocpi.toolkit.modules.sessions.domain

import com.izivia.ocpi.toolkit.common.CiString
import com.izivia.ocpi.toolkit.modules.cdr.domain.AuthMethod
import com.izivia.ocpi.toolkit.modules.cdr.domain.CdrTokenPartial
import com.izivia.ocpi.toolkit.modules.cdr.domain.ChargingPeriod
import com.izivia.ocpi.toolkit.modules.cdr.domain.toPartial
import com.izivia.ocpi.toolkit.modules.types.Price
import java.time.Instant
import kotlin.Int
import kotlin.String
import kotlin.collections.List

/**
 * Partial representation of [com.izivia.ocpi.toolkit.modules.sessions.domain.Session]
 */
public data class SessionPartial(
  public val country_code: CiString?,
  public val party_id: CiString?,
  public val id: CiString?,
  public val start_date_time: Instant?,
  public val end_date_time: Instant?,
  public val kwh: Int?,
  public val cdr_token: CdrTokenPartial?,
  public val auth_method: AuthMethod?,
  public val authorization_reference: CiString?,
  public val location_id: CiString?,
  public val evse_uid: CiString?,
  public val connector_id: CiString?,
  public val meter_id: String?,
  public val currency: CiString?,
  public val charging_periods: List<ChargingPeriod>?,
  public val total_cost: Price?,
  public val status: SessionStatusType?,
  public val last_updated: Instant?,
)

public fun Session.toPartial(): SessionPartial {
   return SessionPartial(
     country_code = country_code,
    party_id = party_id,
    id = id,
    start_date_time = start_date_time,
    end_date_time = end_date_time,
    kwh = kwh,
    cdr_token = cdr_token.toPartial(),
    auth_method = auth_method,
    authorization_reference = authorization_reference,
    location_id = location_id,
    evse_uid = evse_uid,
    connector_id = connector_id,
    meter_id = meter_id,
    currency = currency,
    charging_periods = charging_periods,
    total_cost = total_cost,
    status = status,
    last_updated = last_updated
   )
}

public fun List<Session>.toPartial(): List<SessionPartial> {
   return mapNotNull { it.toPartial() }
}
