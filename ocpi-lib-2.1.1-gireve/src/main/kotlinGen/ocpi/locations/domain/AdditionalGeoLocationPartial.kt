//  ----------
//  - WARNING -
//  ----------
//  This code is generated AND MUST NOT BE EDITED
//  ----------
package ocpi.locations.domain

/**
 * Partial representation of [ocpi.locations.domain.AdditionalGeoLocation]
 */
data class AdditionalGeoLocationPartial(
  val latitude: String?,
  val longitude: String?,
  val name: DisplayTextPartial?,
)

fun AdditionalGeoLocation.toPartial(): AdditionalGeoLocationPartial {
   return AdditionalGeoLocationPartial(
     latitude = latitude,
    longitude = longitude,
    name = name?.toPartial()
   )
}

fun List<AdditionalGeoLocation>.toPartial(): List<AdditionalGeoLocationPartial> {
   return mapNotNull { it.toPartial() }
}
