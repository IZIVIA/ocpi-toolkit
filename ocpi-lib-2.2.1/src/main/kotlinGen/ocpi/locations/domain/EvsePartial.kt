//  ----------
//  - WARNING -
//  ----------
//  This code is generated AND MUST NOT BE EDITED
//  ----------
package ocpi.locations.domain

import common.CiString
import java.time.Instant
import kotlin.String
import kotlin.collections.List

/**
 * Partial representation of [ocpi.locations.domain.Evse]
 */
public data class EvsePartial(
  public val uid: CiString?,
  public val evse_id: CiString?,
  public val status: Status?,
  public val status_schedule: List<StatusSchedule>?,
  public val capabilities: List<Capability>?,
  public val connectors: List<ConnectorPartial>?,
  public val floor_level: String?,
  public val coordinates: GeoLocation?,
  public val physical_reference: String?,
  public val directions: List<DisplayText>?,
  public val parking_restrictions: List<ParkingRestriction>?,
  public val images: List<Image>?,
  public val last_updated: Instant?,
)

public fun Evse.toPartial(): EvsePartial {
   return EvsePartial(
     uid = uid,
    evse_id = evse_id,
    status = status,
    status_schedule = status_schedule,
    capabilities = capabilities,
    connectors = connectors.toPartial(),
    floor_level = floor_level,
    coordinates = coordinates,
    physical_reference = physical_reference,
    directions = directions,
    parking_restrictions = parking_restrictions,
    images = images,
    last_updated = last_updated
   )
}

public fun List<Evse>.toPartial(): List<EvsePartial> {
   return mapNotNull { it.toPartial() }
}
