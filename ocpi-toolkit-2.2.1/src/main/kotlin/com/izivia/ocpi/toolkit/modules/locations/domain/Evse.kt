package com.izivia.ocpi.toolkit.modules.locations.domain

import com.izivia.ocpi.toolkit.annotations.Partial
import com.izivia.ocpi.toolkit.common.CiString
import com.izivia.ocpi.toolkit.modules.types.DisplayText
import java.time.Instant

/**
 * The EVSE object describes the part that controls the power supply to a single EV in a single session. It always
 * belongs to a Location object. It will only contain directions to get from the location to the EVSE (i.e. floor,
 * physical_reference or directions). When these properties are insufficient to reach the EVSE from the Location point,
 * then it typically indicates that this EVSE should be put in a different Location object (sometimes with the same
 * address but with different coordinates/directions).
 *
 * An EVSE object has a list of connectors which can not be used simultaneously: only one connector per EVSE can be used
 * at the time.
 *
 * @property uid (max-length=36) Uniquely identifies the EVSE within the CPOs partner (and suboperator platforms). For
 * example a database unique ID or the "EVSE ID". This field can never be changed, modified or renamed. This is the
 * 'technical' identification of the EVSE, not to be used as 'human readable' identification, use the field: evse_id for
 * that.
 * @property evseId Compliant with the following specification for EVSE ID from "eMI3 standard version V1.0"
 * (http://emi3group.com/documents-links/) "Part 2: business objects." Optional for OCPI because: if an EVSE ID is to be
 * re-used the EVSE ID can be removed from an EVSE that is removed (status: REMOVED). BUT REQUIRED for gireve: required
 * by GIREVE to ensure seamless compatibility with operators connected via other protocols (eMIP, ...). In addition, the
 * eMI3 standard requires that the eMI3 Id of an EVSE begins by the eMI3 Id of the CPO.
 * @property status Indicates the current status of the EVSE.
 * @property statusSchedule Indicates a planned status in the future of the EVSE.
 * @property capabilities List of functionalities that the EVSE is capable of.
 * @property connectors (at least one in the list) List of available connectors on the EVSE.
 * @property floorLevel (max-length=4) Level on which the charging station is located (in garage buildings) in the
 * locally displayed numbering scheme.
 * @property coordinates Coordinates of the EVSE.
 * @property physicalReference (max-length=16) A number/string printed on the outside of the EVSE for visual
 * identification.
 * @property directions Multi-language human-readable directions when more detailed information on how to reach the EVSE
 * from the Location is required.
 * @property parkingRestrictions The restrictions that apply to the parking spot.
 * @property images Links to images related to the EVSE such as photos or logos.
 * @property lastUpdated Instant Timestamp when this EVSE or one of its Connectors was last updated (or created).
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
