package com.izivia.ocpi.toolkit.modules.locations.domain

import com.izivia.ocpi.toolkit.annotations.Partial

/**
 * This class defines the geolocation of the Charge Point. The geodetic system to be used is WGS 84.
 *
 * @property latitude (max-length=10) Latitude of the point in decimal degree. Example: 50.770774. Decimal
 * separator: "." Regex: -?[0-9]{1,2}\.[0-9]{5,7}
 * @property longitude (max-length=11) Longitude of the point in decimal degree. Example: -126.104965. Decimal
 * separator: "." Regex: -?[0-9]{1,3}\.[0-9]{5,7}
 */
@Partial
data class GeoLocation(
    val latitude: String,
    val longitude: String,
)
