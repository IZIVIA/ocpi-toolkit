//  ----------
//  - WARNING -
//  ----------
//  This code is generated AND MUST NOT BE EDITED
//  ----------
package com.izivia.ocpi.toolkit.modules.locations.domain

/**
 * Partial representation of
 * [com.izivia.ocpi.toolkit.modules.locations.domain.AdditionalGeoLocation]
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
