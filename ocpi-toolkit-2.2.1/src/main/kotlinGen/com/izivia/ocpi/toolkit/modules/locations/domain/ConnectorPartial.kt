//  ----------
//  - WARNING -
//  ----------
//  This code is generated AND MUST NOT BE EDITED
//  ----------
package com.izivia.ocpi.toolkit.modules.locations.domain

import com.izivia.ocpi.toolkit.common.CiString
import java.time.Instant

/**
 * Partial representation of [com.izivia.ocpi.toolkit.modules.locations.domain.Connector]
 */
public data class ConnectorPartial(
  public val id: CiString?,
  public val standard: ConnectorType?,
  public val format: ConnectorFormat?,
  public val power_type: PowerType?,
  public val max_voltage: Int?,
  public val max_amperage: Int?,
  public val max_electric_power: Int?,
  public val tariff_ids: List<CiString>?,
  public val terms_and_conditions: String?,
  public val last_updated: Instant?,
)

public fun Connector.toPartial(): ConnectorPartial {
   return ConnectorPartial(
     id = id,
    standard = standard,
    format = format,
    power_type = power_type,
    max_voltage = max_voltage,
    max_amperage = max_amperage,
    max_electric_power = max_electric_power,
    tariff_ids = tariff_ids,
    terms_and_conditions = terms_and_conditions,
    last_updated = last_updated
   )
}

public fun List<Connector>.toPartial(): List<ConnectorPartial> {
   return mapNotNull { it.toPartial() }
}
