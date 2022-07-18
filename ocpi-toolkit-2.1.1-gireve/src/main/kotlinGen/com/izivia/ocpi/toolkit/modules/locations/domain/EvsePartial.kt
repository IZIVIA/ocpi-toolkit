//  ----------
//  - WARNING -
//  ----------
//  This code is generated AND MUST NOT BE EDITED
//  ----------
package com.izivia.ocpi.toolkit.modules.locations.domain

import com.izivia.ocpi.toolkit.modules.types.DisplayTextPartial
import com.izivia.ocpi.toolkit.modules.types.toPartial
import java.time.Instant

/**
 * Partial representation of [com.izivia.ocpi.toolkit.modules.locations.domain.Evse]
 */
public data class EvsePartial(
  public val uid: String?,
  public val evse_id: String?,
  public val status: Status?,
  public val status_schedule: List<StatusSchedule>?,
  public val capabilities: List<Capability>?,
  public val connectors: List<Connector>?,
  public val floor_level: String?,
  public val coordinates: GeoLocation?,
  public val physical_reference: String?,
  public val directions: List<DisplayTextPartial>?,
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
    connectors = connectors,
    floor_level = floor_level,
    coordinates = coordinates,
    physical_reference = physical_reference,
    directions = directions?.toPartial(),
    parking_restrictions = parking_restrictions,
    images = images,
    last_updated = last_updated
   )
}

public fun List<Evse>.toPartial(): List<EvsePartial> {
   return mapNotNull { it.toPartial() }
}
