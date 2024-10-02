package com.izivia.ocpi.toolkit.modules.locations.domain

import com.izivia.ocpi.toolkit.annotations.Partial
import com.izivia.ocpi.toolkit.modules.types.DisplayText

/**
 * This class defines an additional geolocation that is relevant for the Charge Point. The geodetic system to be used
 * is WGS 84.
 *
 * @property latitude String (max-length=10) Latitude of the point in decimal degree. Example: 50.770774. Decimal
 * separator: "." Regex: -?[0-9]{1,2}\.[0-9]{5,7}
 * @property longitude String (max-length=11) Longitude of the point in decimal degree. Example: -126.104965. Decimal
 * separator: "." Regex: -?[0-9]{1,3}\.[0-9]{5,7}
 * @property name DisplayText? Name of the point in local language or as written at the location. For example the street
 * name of a parking lot entrance, or its number.
 */
@Partial
data class AdditionalGeoLocation(
    val latitude: String,
    val longitude: String,
    val name: DisplayText?,
)
