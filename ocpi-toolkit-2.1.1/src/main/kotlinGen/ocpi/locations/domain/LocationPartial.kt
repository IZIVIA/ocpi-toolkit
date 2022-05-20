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
public data class LocationPartial(
  public val id: String?,
  public val type: LocationType?,
  public val name: String?,
  public val address: String?,
  public val city: String?,
  public val postal_code: String?,
  public val country: String?,
  public val coordinates: GeoLocationPartial?,
  public val related_locations: List<AdditionalGeoLocationPartial>?,
  public val evses: List<EvsePartial>?,
  public val directions: List<DisplayTextPartial>?,
  public val `operator`: BusinessDetailsPartial?,
  public val suboperator: BusinessDetailsPartial?,
  public val owner: BusinessDetailsPartial?,
  public val facilities: List<Facility>?,
  public val time_zone: String?,
  public val opening_times: HoursPartial?,
  public val charging_when_closed: Boolean?,
  public val images: List<ImagePartial>?,
  public val energy_mix: EnergyMixPartial?,
  public val last_updated: Instant?,
)

public fun Location.toPartial(): LocationPartial {
   return LocationPartial(
     id = id,
    type = type,
    name = name,
    address = address,
    city = city,
    postal_code = postal_code,
    country = country,
    coordinates = coordinates.toPartial(),
    related_locations = related_locations?.toPartial(),
    evses = evses?.toPartial(),
    directions = directions?.toPartial(),
    operator = operator?.toPartial(),
    suboperator = suboperator?.toPartial(),
    owner = owner?.toPartial(),
    facilities = facilities,
    time_zone = time_zone,
    opening_times = opening_times?.toPartial(),
    charging_when_closed = charging_when_closed,
    images = images?.toPartial(),
    energy_mix = energy_mix?.toPartial(),
    last_updated = last_updated
   )
}

public fun List<Location>.toPartial(): List<LocationPartial> {
   return mapNotNull { it.toPartial() }
}
