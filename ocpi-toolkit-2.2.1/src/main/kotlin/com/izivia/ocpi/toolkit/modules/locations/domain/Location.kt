package com.izivia.ocpi.toolkit.modules.locations.domain

import com.izivia.ocpi.toolkit.annotations.Partial
import com.izivia.ocpi.toolkit.common.CiString
import com.izivia.ocpi.toolkit.modules.types.DisplayText
import java.time.Instant

/**
 * The Location object describes the location and its properties where a group of EVSEs that belong together are
 * installed. Typically, the Location object is the exact location of the group of EVSEs, but it can also be the entrance
 * of a parking garage which contains these EVSEs. The exact way to reach each EVSE can be further specified by its own
 * properties.
 *
 * @property countryCode (max-length=2) ISO-3166 alpha-2 country code of the CPO that 'owns' this Location.
 * @property partyId (max-length=3) ID of the CPO that 'owns' this Location (following the ISO-15118 standard).
 * @property id (max-length=36) Uniquely identifies the location within the CPOs platform (and suboperator platforms).
 * This field can never be changed, modified or renamed.
 * @property publish Defines if a Location may be published on a website or app etc. When this is set to false, only
 * tokens identified in the field: publish_allowed_to are allowed to be shown this Location. When the same location has
 * EVSEs that may be published and may not be published, two 'Locations' should be created.
 * @property publishAllowedTo This field may only be used when the publish field is set to false. Only owners of
 * Tokens that match all the set fields of one PublishToken in the list are allowed to be shown this location.
 * @property name (max-length=255) Display name of the location.
 * @property address (max-length=45) Street/block name and house number if available.
 * @property city (max-length=45) City or town.
 * @property postalCode (max-length=10) Postal code of the location, may only be omitted when the location has no
 * postal code: in some countries charging locations at highways don’t have postal codes.
 * @property state (max-length=20) State or province of the location, only to be used when relevant.
 * @property country (max-length=3) ISO 3166-1 alpha-3 code for the country of this location.
 * @property coordinates Coordinates of the location.
 * @property relatedLocations Geographical location of related points relevant to the user.
 * @property parkingType The general type of parking at the charge point location.
 * @property evses List of EVSEs that belong to this Location.
 * @property directions Human-readable directions on how to reach the location.
 * @property operator Information of the operator. When not specified, the information retrieved from the api_info
 * endpoint should be used instead.
 * @property suboperator Information of the suboperator if available.
 * @property owner Information of the owner if available.
 * @property facilities Optional list of facilities this charge location directly belongs to.
 * @property timeZone (max-length=255) One of IANA tzdata's TZ-values representing the time zone of the location.
 * Examples: "Europe/Oslo", "Europe/Zurich". (http://www.iana.org/time-zones)
 * @property openingTimes The times when the EVSEs at the location can be accessed for charging.
 * @property chargingWhenClosed Indicates if the EVSEs are still charging outside the opening hours of the location.
 * E.g. when the parking garage closes its barriers overnight, is it allowed to charge till the next morning?
 * Default: true
 * @property images Links to images related to the location such as photos or logos.
 * @property energyMix Details on the energy supplied at this location.
 * @property lastUpdated Timestamp when this Location or one of its EVSEs or Connectors were last updated (or created).
 */
@Partial
data class Location(
    val countryCode: CiString,
    val partyId: CiString,
    val id: CiString,
    val publish: Boolean,
    val publishAllowedTo: List<PublishTokenType>? = null,
    val name: String? = null,
    val address: String,
    val city: String,
    val postalCode: String? = null,
    val state: String? = null,
    val country: String,
    val coordinates: GeoLocation,
    val relatedLocations: List<AdditionalGeoLocation>? = null,
    val parkingType: ParkingType? = null,
    val evses: List<Evse>? = null,
    val directions: List<DisplayText>? = null,
    val operator: BusinessDetails? = null,
    val suboperator: BusinessDetails? = null,
    val owner: BusinessDetails? = null,
    val facilities: List<Facility>? = null,
    val timeZone: String,
    val openingTimes: Hours? = null,
    val chargingWhenClosed: Boolean? = null,
    val images: List<Image>? = null,
    val energyMix: EnergyMix? = null,
    val lastUpdated: Instant,
)
