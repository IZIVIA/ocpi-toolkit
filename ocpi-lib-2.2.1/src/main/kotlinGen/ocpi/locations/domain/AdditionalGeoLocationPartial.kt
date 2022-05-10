//  ----------
//  - WARNING -
//  ----------
//  This code is generated AND MUST NOT BE EDITED
//  ----------
package ocpi.locations.domain

import kotlin.String
import kotlin.collections.List

/**
 * Partial representation of [ocpi.locations.domain.AdditionalGeoLocation]
 */
public data class AdditionalGeoLocationPartial(
  public val latitude: String?,
  public val longitude: String?,
  public val name: DisplayTextPartial?,
)

public fun AdditionalGeoLocation.toPartial(): AdditionalGeoLocationPartial {
   return AdditionalGeoLocationPartial(
     latitude = latitude,
    longitude = longitude,
    name = name?.toPartial()
   )
}

public fun List<AdditionalGeoLocation>.toPartial(): List<AdditionalGeoLocationPartial> {
   return mapNotNull { it.toPartial() }
}
