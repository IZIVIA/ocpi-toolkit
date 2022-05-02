//  ----------
//  - WARNING -
//  ----------
//  This code is generated AND MUST NOT BE EDITED
//  ----------
package ocpi.locations.domain

import java.time.Instant
import kotlin.String
import kotlin.collections.List

/**
 * Partial representation of [ocpi.locations.domain.Evse]
 */
public data class EvsePartial(
  public val uid: String?,
  public val evse_id: String?,
  public val status: Status?,
  public val status_schedule: List<StatusSchedulePartial>?,
  public val capabilities: List<Capability>?,
  public val connectors: List<ConnectorPartial>?,
  public val floor_level: String?,
  public val coordinates: GeoLocationPartial?,
  public val physical_reference: String?,
  public val directions: List<DisplayTextPartial>?,
  public val parking_restrictions: List<ParkingRestriction>?,
  public val images: List<ImagePartial>?,
  public val last_updated: Instant?,
)
