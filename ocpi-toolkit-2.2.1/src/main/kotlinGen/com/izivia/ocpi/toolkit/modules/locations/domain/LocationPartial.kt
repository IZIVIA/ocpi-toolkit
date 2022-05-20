//  ----------
//  - WARNING -
//  ----------
//  This code is generated AND MUST NOT BE EDITED
//  ----------
package com.izivia.ocpi.toolkit.modules.locations.domain

import com.izivia.ocpi.toolkit.common.CiString
import java.time.Instant

/**
 * Partial representation of [com.izivia.ocpi.toolkit.modules.locations.domain.Location]
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
  public val coordinates: GeoLocationPartial?,
  public val related_locations: List<AdditionalGeoLocationPartial>?,
  public val parking_type: ParkingType?,
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
     country_code = country_code,
    party_id = party_id,
    id = id,
    publish = publish,
    publish_allowed_to = publish_allowed_to?.toPartial(),
    name = name,
    address = address,
    city = city,
    postal_code = postal_code,
    state = state,
    country = country,
    coordinates = coordinates.toPartial(),
    related_locations = related_locations?.toPartial(),
    parking_type = parking_type,
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
