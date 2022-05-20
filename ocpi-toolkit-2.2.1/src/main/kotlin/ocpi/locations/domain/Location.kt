package ocpi.locations.domain

import common.CiString
import io.github.quatresh.annotations.Partial
import java.time.Instant

/**
 * The Location object describes the location and its properties where a group of EVSEs that belong together are
 * installed. Typically, the Location object is the exact location of the group of EVSEs, but it can also be the entrance
 * of a parking garage which contains these EVSEs. The exact way to reach each EVSE can be further specified by its own
 * properties.
 *
 * @property country_code (max-length=2) ISO-3166 alpha-2 country code of the CPO that 'owns' this Location.
 * @property party_id (max-length=3) ID of the CPO that 'owns' this Location (following the ISO-15118 standard).
 * @property id (max-length=36) Uniquely identifies the location within the CPOs platform (and suboperator platforms).
 * This field can never be changed, modified or renamed.
 * @property publish Defines if a Location may be published on a website or app etc. When this is set to false, only
 * tokens identified in the field: publish_allowed_to are allowed to be shown this Location. When the same location has
 * EVSEs that may be published and may not be published, two 'Locations' should be created.
 * @property publish_allowed_to This field may only be used when the publish field is set to false. Only owners of
 * Tokens that match all the set fields of one PublishToken in the list are allowed to be shown this location.
 * @property name (max-length=255) Display name of the location.
 * @property address (max-length=45) Street/block name and house number if available.
 * @property city (max-length=45) City or town.
 * @property postal_code (max-length=10) Postal code of the location, may only be omitted when the location has no
 * postal code: in some countries charging locations at highways don’t have postal codes.
 * @property state (max-length=20) State or province of the location, only to be used when relevant.
 * @property country (max-length=3) ISO 3166-1 alpha-3 code for the country of this location.
 * @property coordinates Coordinates of the location.
 * @property related_locations Geographical location of related points relevant to the user.
 * @property parking_type The general type of parking at the charge point location.
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
@Partial
data class Location(
    val country_code: CiString,
    val party_id: CiString,
    val id: CiString,
    val publish: Boolean,
    val publish_allowed_to: List<PublishTokenType>?,
    val name: String?,
    val address: String,
    val city: String,
    val postal_code: String?,
    val state: String?,
    val country: String,
    val coordinates: GeoLocation,
    val related_locations: List<AdditionalGeoLocation>?,
    val parking_type: ParkingType?,
    val evses: List<Evse>?,
    val directions: List<DisplayText>?,
    val operator: BusinessDetails?,
    val suboperator: BusinessDetails?,
    val owner: BusinessDetails?,
    val facilities: List<Facility>?,
    val time_zone: String,
    val opening_times: Hours?,
    val charging_when_closed: Boolean?,
    val images: List<Image>?,
    val energy_mix: EnergyMix?,
    val last_updated: Instant
)