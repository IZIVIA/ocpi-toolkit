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
