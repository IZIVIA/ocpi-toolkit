//  ----------
//  - WARNING -
//  ----------
//  This code is generated AND MUST NOT BE EDITED
//  ----------
package ocpi.locations.domain

import java.time.Instant

/**
 * Partial representation of [ocpi.locations.domain.Evse]
 */
data class EvsePartial(
  val uid: String?,
  val evse_id: String?,
  val status: Status?,
  val status_schedule: List<StatusSchedulePartial>?,
  val capabilities: List<Capability>?,
  val connectors: List<ConnectorPartial>?,
  val floor_level: String?,
  val coordinates: GeoLocationPartial?,
  val physical_reference: String?,
  val directions: List<DisplayTextPartial>?,
  val parking_restrictions: List<ParkingRestriction>?,
  val images: List<ImagePartial>?,
  val last_updated: Instant?,
)

fun Evse.toPartial(): EvsePartial {
   return EvsePartial(
     uid = uid,
    evse_id = evse_id,
    status = status,
    status_schedule = status_schedule.toPartial(),
    capabilities = capabilities,
    connectors = connectors.toPartial(),
    floor_level = floor_level,
    coordinates = coordinates?.toPartial(),
    physical_reference = physical_reference,
    directions = directions.toPartial(),
    parking_restrictions = parking_restrictions,
    images = images.toPartial(),
    last_updated = last_updated
   )
}

fun List<Evse>.toPartial(): List<EvsePartial> {
   return mapNotNull { it.toPartial() }
}
