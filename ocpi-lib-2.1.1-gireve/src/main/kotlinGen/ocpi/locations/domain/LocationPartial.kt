//  ----------
//  - WARNING -
//  ----------
//  This code is generated AND MUST NOT BE EDITED
//  ----------
package ocpi.locations.domain

import java.time.Instant

/**
 * Partial representation of [ocpi.locations.domain.Location]
 */
data class LocationPartial(
  val id: String?,
  val type: LocationType?,
  val name: String?,
  val address: String?,
  val city: String?,
  val postal_code: String?,
  val country: String?,
  val coordinates: GeoLocationPartial?,
  val related_locations: List<AdditionalGeoLocationPartial>?,
  val evses: List<EvsePartial>?,
  val directions: List<DisplayTextPartial>?,
  val `operator`: BusinessDetailsPartial?,
  val suboperator: BusinessDetailsPartial?,
  val owner: BusinessDetailsPartial?,
  val facilities: List<Facility>?,
  val time_zone: String?,
  val opening_times: HoursPartial?,
  val charging_when_closed: Boolean?,
  val images: List<ImagePartial>?,
  val energy_mix: EnergyMixPartial?,
  val last_updated: Instant?,
)

fun Location.toPartial(): LocationPartial {
   return LocationPartial(
     id = id,
    type = type,
    name = name,
    address = address,
    city = city,
    postal_code = postal_code,
    country = country,
    coordinates = coordinates.toPartial(),
    related_locations = related_locations.toPartial(),
    evses = evses.toPartial(),
    directions = directions.toPartial(),
    operator = operator?.toPartial(),
    suboperator = suboperator?.toPartial(),
    owner = owner?.toPartial(),
    facilities = facilities,
    time_zone = time_zone,
    opening_times = opening_times?.toPartial(),
    charging_when_closed = charging_when_closed,
    images = images.toPartial(),
    energy_mix = energy_mix?.toPartial(),
    last_updated = last_updated
   )
}

fun List<Location>.toPartial(): List<LocationPartial> {
   return mapNotNull { it.toPartial() }
}
