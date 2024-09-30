package com.izivia.ocpi.toolkit.modules.cdr.domain

import com.izivia.ocpi.toolkit.common.CiString
import com.izivia.ocpi.toolkit.modules.locations.domain.ConnectorFormat
import com.izivia.ocpi.toolkit.modules.locations.domain.ConnectorType
import com.izivia.ocpi.toolkit.modules.locations.domain.GeoLocation
import com.izivia.ocpi.toolkit.modules.locations.domain.PowerType

/**
 * The CdrLocation class contains only the relevant information from the Location object that is needed in a CDR.
 * @property id Uniquely identifies the location within the CPO’s platform (and suboperator
platforms). This field can never be changed, modified or renamed.
 * @property name Display name of the location.
 * @property address Street/block name and house number if available
 * @property city City or town
 * @property postalCode Postal code of the location, may only be omitted when the location has no postal code: in some
countries charging locations at highways don’t have postal codes.
 * @property state State only to be used when relevant.
 * @property country ISO 3166-1 alpha-3 code for the country of this location.
 * @property coordinates Coordinates of the location.
 * @property evseUid Uniquely identifies the EVSE within the CPO’s platform (and suboperator
platforms). For example a database unique ID or the actual EVSE ID. This field
can never be changed, modified or renamed. This is the technical identification
of the EVSE, not to be used as human readable identification, use the field:
evse_id for that.
 * @property evseId Compliant with the following specification for EVSE ID from "eMI3 standard
version V1.0" (http://emi3group.com/documents-links/) "Part 2: business
objects.".
 * @property connectorId Identifier of the connector within the EVSE.
 * @property connectorStandard The standard of the installed connector.
 * @property connectorFormat The format (socket/cable) of the installed connector.
 *
 */
data class CdrLocation(
    val id: CiString,
    val name: String? = null,
    val address: String,
    val city: String,
    val postalCode: String? = null,
    val state: String? = null,
    val country: String,
    val coordinates: GeoLocation,
    val evseUid: CiString,
    val evseId: CiString,
    val connectorId: CiString,
    val connectorStandard: ConnectorType,
    val connectorFormat: ConnectorFormat,
    val connectorPowerType: PowerType,
)
