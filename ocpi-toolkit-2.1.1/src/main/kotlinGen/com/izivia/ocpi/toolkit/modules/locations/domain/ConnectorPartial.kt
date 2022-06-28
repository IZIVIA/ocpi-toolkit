//  ----------
//  - WARNING -
//  ----------
//  This code is generated AND MUST NOT BE EDITED
//  ----------
package com.izivia.ocpi.toolkit.modules.locations.domain

import java.time.Instant
import kotlin.Int
import kotlin.String
import kotlin.collections.List

/**
 * Partial representation of [com.izivia.ocpi.toolkit.modules.locations.domain.Connector]
 */
public data class ConnectorPartial(
  public val id: String?,
  public val standard: ConnectorType?,
  public val format: ConnectorFormat?,
  public val power_type: PowerType?,
  public val voltage: Int?,
  public val amperage: Int?,
  public val tariff_id: String?,
  public val terms_and_conditions: String?,
  public val last_updated: Instant?,
)

public fun Connector.toPartial(): ConnectorPartial {
   return ConnectorPartial(
     id = id,
    standard = standard,
    format = format,
    power_type = power_type,
    voltage = voltage,
    amperage = amperage,
    tariff_id = tariff_id,
    terms_and_conditions = terms_and_conditions,
    last_updated = last_updated
   )
}

public fun List<Connector>.toPartial(): List<ConnectorPartial> {
   return mapNotNull { it.toPartial() }
}
