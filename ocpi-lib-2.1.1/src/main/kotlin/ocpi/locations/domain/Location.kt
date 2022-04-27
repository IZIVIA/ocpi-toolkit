package ocpi.locations.domain

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
 * @property postal_code (max-length=10) Postal code of the location.
 * @property country (max-length=3) ISO 3166-1 alpha-3 code for the country of this location.
 * @property coordinates Coordinates of the location.
 * @property related_locations Geographical location of related points relevant to the user.
 * @property evses List of EVSEs that belong to this Location.
 * @property directions Human-readable directions on how to reach the location.
 * @property operator Information of the operator. When not specified, the information retrieved from the api_info
 * endpoint should be used instead.
 * @property suboperator Information of the suboperator if available.
 * @property owner Information of the owner if available.
 * @property facilities Optional list of facilities this charge location directly belongs to.
 * @property time_zone (max-length=255) One of IANA tzdata's TZ-values representing the time zone of the location.
 * Examples: "Europe/Oslo", "Europe/Zurich". (http://www.iana.org/time-zones)
 * @property opening_times The times when the EVSEs at the location can be accessed for charging.
 * @property charging_when_closed Indicates if the EVSEs are still charging outside the opening hours of the location.
 * E.g. when the parking garage closes its barriers overnight, is it allowed to charge till the next morning?
 * Default: true
 * @property images Links to images related to the location such as photos or logos.
 * @property energy_mix Details on the energy supplied at this location.
 * @property last_updated Timestamp when this Location or one of its EVSEs or Connectors were last updated (or created).
 */
data class Location(
    val id: String,
    val type: LocationType,
    val name: String?,
    val address: String,
    val city: String,
    val postal_code: String,
    val country: String,
    val coordinates: GeoLocation,
    val related_locations: List<AdditionalGeoLocation>,
    val evses: List<Evse>,
    val directions: List<DisplayText>,
    val operator: BusinessDetails?,
    val suboperator: BusinessDetails?,
    val owner: BusinessDetails?,
    val facilities: List<Facility>,
    val time_zone: String?,
    val opening_times: Hours?,
    val charging_when_closed: Boolean?,
    val images: List<Image>,
    val energy_mix: EnergyMix?,
    val last_updated: Instant
)

data class LocationPatch(
    val id: String?,
    val type: LocationType?,
    val name: String?,
    val address: String?,
    val city: String?,
    val postal_code: String?,
    val country: String?,
    val coordinates: GeoLocationPatch?,
    val related_locations: List<AdditionalGeoLocationPatch>?,
    val evses: List<EvsePatch>?,
    val directions: List<DisplayTextPatch>?,
    val operator: BusinessDetailsPatch?,
    val suboperator: BusinessDetailsPatch?,
    val owner: BusinessDetailsPatch?,
    val facilities: List<Facility>?,
    val time_zone: String?,
    val opening_times: HoursPatch?,
    val charging_when_closed: Boolean?,
    val images: List<ImagePatch>?,
    val energy_mix: EnergyMixPatch?,
    val last_updated: Instant?
)
