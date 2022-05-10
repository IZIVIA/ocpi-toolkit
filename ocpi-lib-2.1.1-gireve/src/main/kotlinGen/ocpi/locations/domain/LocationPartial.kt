//  ----------
//  - WARNING -
//  ----------
//  This code is generated AND MUST NOT BE EDITED
//  ----------
package ocpi.locations.domain

import java.time.Instant
import kotlin.Boolean
import kotlin.String
import kotlin.collections.List

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
  public val coordinates: GeoLocation?,
  public val related_locations: List<AdditionalGeoLocation>?,
  public val evses: List<EvsePartial>?,
  public val directions: List<DisplayText>?,
  public val `operator`: BusinessDetails?,
  public val suboperator: BusinessDetails?,
  public val owner: BusinessDetails?,
  public val facilities: List<Facility>?,
  public val time_zone: String?,
  public val opening_times: HoursPartial?,
  public val charging_when_closed: Boolean?,
  public val images: List<Image>?,
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
    coordinates = coordinates,
    related_locations = related_locations,
    evses = evses?.toPartial(),
    directions = directions,
    operator = operator,
    suboperator = suboperator,
    owner = owner,
    facilities = facilities,
    time_zone = time_zone,
    opening_times = opening_times?.toPartial(),
    charging_when_closed = charging_when_closed,
    images = images,
    energy_mix = energy_mix?.toPartial(),
    last_updated = last_updated
   )
}

public fun List<Location>.toPartial(): List<LocationPartial> {
   return mapNotNull { it.toPartial() }
}
