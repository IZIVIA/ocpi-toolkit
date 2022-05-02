//  ----------
//  - WARNING -
//  ----------
//  This code is generated AND MUST NOT BE EDITED
//  ----------
package ocpi.locations.domain

import java.time.Instant
import kotlin.Int
import kotlin.String

/**
 * Partial representation of [ocpi.locations.domain.Connector]
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
