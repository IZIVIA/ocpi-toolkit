package com.izivia.ocpi.toolkit211.modules.locations.domain

import com.izivia.ocpi.toolkit.annotations.Partial
import com.izivia.ocpi.toolkit211.common.CiString
import com.izivia.ocpi.toolkit211.modules.types.DisplayText
import java.time.Instant

/**
 * The EVSE object describes the part that controls the power supply to a single EV in a single session.
 *
 * @property uid (max-length=39) Uniquely identifies the EVSE within the CPOs platform.
 * @property evseId Compliant with eMI3 standard version V1.0 for EVSE ID.
 * @property status Indicates the current status of the EVSE.
 * @property statusSchedule Indicates a planned status in the future of the EVSE.
 * @property capabilities List of functionalities that the EVSE is capable of.
 * @property connectors (at least one in the list) List of available connectors on the EVSE.
 * @property floorLevel (max-length=4) Level on which the charging station is located.
 * @property coordinates Coordinates of the EVSE.
 * @property physicalReference (max-length=16) A number/string printed on the outside of the EVSE.
 * @property directions Multi-language human-readable directions.
 * @property parkingRestrictions The restrictions that apply to the parking spot.
 * @property images Links to images related to the EVSE.
 * @property lastUpdated Timestamp when this EVSE or one of its Connectors was last updated (or created).
 */
@Partial
data class Evse(
    val uid: CiString,
    val evseId: CiString?,
    val status: Status,
    val statusSchedule: List<StatusSchedule>? = null,
    val capabilities: List<Capability>? = null,
    val connectors: List<Connector>,
    val floorLevel: String? = null,
    val coordinates: GeoLocation? = null,
    val physicalReference: String? = null,
    val directions: List<DisplayText>? = null,
    val parkingRestrictions: List<ParkingRestriction>? = null,
    val images: List<Image>? = null,
    val lastUpdated: Instant,
)
