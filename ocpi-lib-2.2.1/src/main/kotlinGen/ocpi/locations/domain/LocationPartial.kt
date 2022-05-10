//  ----------
//  - WARNING -
//  ----------
//  This code is generated AND MUST NOT BE EDITED
//  ----------
package ocpi.locations.domain

import common.CiString
import java.time.Instant
import kotlin.Boolean
import kotlin.String
import kotlin.collections.List

/**
 * Partial representation of [ocpi.locations.domain.Location]
 */
public data class LocationPartial(
  public val country_code: CiString?,
  public val party_id: CiString?,
  public val id: CiString?,
  public val publish: Boolean?,
  public val publish_allowed_to: List<PublishTokenTypePartial>?,
  public val name: String?,
  public val address: String?,
  public val city: String?,
  public val postal_code: String?,
  public val state: String?,
  public val country: String?,
  public val coordinates: GeoLocation?,
  public val related_locations: List<AdditionalGeoLocation>?,
  public val parking_type: ParkingType?,
  public val evses: List<EvsePartial>?,
  public val directions: List<DisplayText>?,
  public val `operator`: BusinessDetails?,
  public val suboperator: BusinessDetails?,
  public val owner: BusinessDetails?,
  public val facilities: List<Facility>?,
  public val time_zone: String?,
  public val opening_times: Hours?,
  public val charging_when_closed: Boolean?,
  public val images: List<ImagePartial>?,
  public val energy_mix: EnergyMix?,
  public val last_updated: Instant?,
)

public fun Location.toPartial(): LocationPartial {
   return LocationPartial(
     country_code = country_code,
    party_id = party_id,
    id = id,
    publish = publish,
    publish_allowed_to = publish_allowed_to.toPartial(),
    name = name,
    address = address,
    city = city,
    postal_code = postal_code,
    state = state,
    country = country,
    coordinates = coordinates,
    related_locations = related_locations,
    parking_type = parking_type,
    evses = evses.toPartial(),
    directions = directions,
    operator = operator,
    suboperator = suboperator,
    owner = owner,
    facilities = facilities,
    time_zone = time_zone,
    opening_times = opening_times,
    charging_when_closed = charging_when_closed,
    images = images.toPartial(),
    energy_mix = energy_mix,
    last_updated = last_updated
   )
}

public fun List<Location>.toPartial(): List<LocationPartial> {
   return mapNotNull { it.toPartial() }
}
