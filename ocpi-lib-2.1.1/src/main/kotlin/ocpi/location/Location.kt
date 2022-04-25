package ocpi.location

import ocpi.common.*
import ocpi.evse.Evse
import ocpi.common.Image
import ocpi.evse.EvsePatch
import java.time.Instant

/**
 * The Location object describes the location and its properties where a group of EVSEs that belong together are
 * installed. Typically, the Location object is the exact location of the group of EVSEs, but it can also be the entrance
 * of a parking garage which contains these EVSEs. The exact way to reach each EVSE can be further specified by its own
 * properties.
 *
 * @property id (max-length=39) Uniquely identifies the location within the CPOs platform (and suboperator platforms).
 * This field can never be changed, modified or renamed.
 * @property type The general type of the charge point location.
 * @property name (max-length=255) Display name of the location.
 * @property address (max-length=45) Street/block name and house number if available.
 * @property city (max-length=45) City or town.
 * @property postalCode (max-length=10) Postal code of the location.
 * @property country (max-length=3) ISO 3166-1 alpha-3 code for the country of this location.
 * @property coordinates Coordinates of the location.
 * @property relatedLocations Geographical location of related points relevant to the user.
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
data class Location(
    val id: String,
    val type: LocationType,
    val name: String?,
    val address: String,
    val city: String,
    val postalCode: String,
    val country: String,
    val coordinates: GeoLocation,
    val relatedLocations: List<AdditionalGeoLocation>,
    val evses: List<Evse>,
    val directions: List<DisplayText>,
    val operator: BusinessDetails?,
    val suboperator: BusinessDetails?,
    val owner: BusinessDetails?,
    val facilities: List<Facility>,
    val timeZone: String?,
    val openingTimes: Hours?,
    val chargingWhenClosed: Boolean?,
    val images: List<Image>,
    val energyMix: EnergyMix?,
    val lastUpdated: Instant
)

data class LocationPatch(
    val id: String?,
    val type: LocationType?,
    val name: String?,
    val address: String?,
    val city: String?,
    val postalCode: String?,
    val country: String?,
    val coordinates: GeoLocationPatch?,
    val relatedLocations: List<AdditionalGeoLocationPatch>?,
    val evses: List<EvsePatch>?,
    val directions: List<DisplayTextPatch>?,
    val operator: BusinessDetailsPatch?,
    val suboperator: BusinessDetailsPatch?,
    val owner: BusinessDetailsPatch?,
    val facilities: List<Facility>?,
    val timeZone: String?,
    val openingTimes: HoursPatch?,
    val chargingWhenClosed: Boolean?,
    val images: List<ImagePatch>?,
    val energyMix: EnergyMixPatch?,
    val lastUpdated: Instant?
)
