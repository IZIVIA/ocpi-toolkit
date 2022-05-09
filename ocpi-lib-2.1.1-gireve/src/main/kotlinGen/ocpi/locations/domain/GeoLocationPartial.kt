//  ----------
//  - WARNING -
//  ----------
//  This code is generated AND MUST NOT BE EDITED
//  ----------
package ocpi.locations.domain

/**
 * Partial representation of [ocpi.locations.domain.GeoLocation]
 */
data class GeoLocationPartial(
  val latitude: String?,
  val longitude: String?,
)

fun GeoLocation.toPartial(): GeoLocationPartial {
   return GeoLocationPartial(
     latitude = latitude,
    longitude = longitude
   )
}

fun List<GeoLocation>.toPartial(): List<GeoLocationPartial> {
   return mapNotNull { it.toPartial() }
}
