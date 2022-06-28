//  ----------
//  - WARNING -
//  ----------
//  This code is generated AND MUST NOT BE EDITED
//  ----------
package com.izivia.ocpi.toolkit.modules.locations.domain

import kotlin.String
import kotlin.collections.List

/**
 * Partial representation of [com.izivia.ocpi.toolkit.modules.locations.domain.GeoLocation]
 */
public data class GeoLocationPartial(
  public val latitude: String?,
  public val longitude: String?,
)

public fun GeoLocation.toPartial(): GeoLocationPartial {
   return GeoLocationPartial(
     latitude = latitude,
    longitude = longitude
   )
}

public fun List<GeoLocation>.toPartial(): List<GeoLocationPartial> {
   return mapNotNull { it.toPartial() }
}
