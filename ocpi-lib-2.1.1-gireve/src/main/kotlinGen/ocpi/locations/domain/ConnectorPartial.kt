//  ----------
//  - WARNING -
//  ----------
//  This code is generated AND MUST NOT BE EDITED
//  ----------
package ocpi.locations.domain

import java.time.Instant

/**
 * Partial representation of [ocpi.locations.domain.Connector]
 */
data class ConnectorPartial(
  val id: String?,
  val standard: ConnectorType?,
  val format: ConnectorFormat?,
  val power_type: PowerType?,
  val voltage: Int?,
  val amperage: Int?,
  val tariff_id: String?,
  val terms_and_conditions: String?,
  val last_updated: Instant?,
)

fun Connector.toPartial(): ConnectorPartial {
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

fun List<Connector>.toPartial(): List<ConnectorPartial> {
   return mapNotNull { it.toPartial() }
}
