package com.izivia.ocpi.toolkit211.modules.locations.domain

import com.izivia.ocpi.toolkit.annotations.Partial
import com.izivia.ocpi.toolkit211.common.CiString
import com.izivia.ocpi.toolkit211.modules.types.DisplayText
import java.time.Instant

/**
 * The Location object describes the location and its properties where a group of EVSEs that belong together are
 * installed.
 *
 * @property id (max-length=39) Uniquely identifies the location within the CPOs platform.
 * @property name (max-length=255) Display name of the location.
 * @property address (max-length=45) Street/block name and house number if available.
 * @property city (max-length=45) City or town.
 * @property postalCode (max-length=10) Postal code of the location.
 * @property country (max-length=3) ISO 3166-1 alpha-3 code for the country of this location.
 * @property coordinates Coordinates of the location.
 * @property relatedLocations Geographical location of related points relevant to the user.
 * @property evses List of EVSEs that belong to this Location.
 * @property directions Human-readable directions on how to reach the location.
 * @property operator Information of the operator.
 * @property suboperator Information of the suboperator if available.
 * @property owner Information of the owner if available.
 * @property facilities Optional list of facilities this charge location directly belongs to.
 * @property timeZone (max-length=255) One of IANA tzdata's TZ-values representing the time zone of the location.
 * @property openingTimes The times when the EVSEs at the location can be accessed for charging.
 * @property chargingWhenClosed Indicates if the EVSEs are still charging outside the opening hours of the location.
 * @property images Links to images related to the location.
 * @property energyMix Details on the energy supplied at this location.
 * @property lastUpdated Timestamp when this Location or one of its EVSEs or Connectors were last updated (or created).
 */
@Partial
data class Location(
    val id: CiString,
    val name: String? = null,
    val address: String,
    val city: String,
    val postalCode: String,
    val country: String,
    val coordinates: GeoLocation,
    val relatedLocations: List<AdditionalGeoLocation>? = null,
    val evses: List<Evse>? = null,
    val directions: List<DisplayText>? = null,
    val operator: BusinessDetails? = null,
    val suboperator: BusinessDetails? = null,
    val owner: BusinessDetails? = null,
    val facilities: List<Facility>? = null,
    val timeZone: String? = null,
    val openingTimes: Hours? = null,
    val chargingWhenClosed: Boolean? = null,
    val images: List<Image>? = null,
    val energyMix: EnergyMix? = null,
    val lastUpdated: Instant,
)
