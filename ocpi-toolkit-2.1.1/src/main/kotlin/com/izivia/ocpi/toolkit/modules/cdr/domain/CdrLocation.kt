package com.izivia.ocpi.toolkit.modules.cdr.domain

import com.izivia.ocpi.toolkit.modules.locations.domain.ConnectorFormat
import com.izivia.ocpi.toolkit.modules.locations.domain.ConnectorType
import com.izivia.ocpi.toolkit.modules.locations.domain.PowerType


/**
 * The CdrLocation class contains only the relevant information from the Location object that is needed in a CDR.
 * @property id Uniquely identifies the location within the CPO’s platform (and suboperator
platforms). This field can never be changed, modified or renamed.
 * @property name Display name of the location.
 * @property address Street/block name and house number if available
 * @property city City or town
 * @property postal_code Postal code of the location.
 * @property country ISO 3166-1 alpha-3 code for the country of this location.
 * @property coordinates Coordinates of the location.
 * @property evse_uid Uniquely identifies the EVSE within the CPO’s platform (and suboperator
platforms). For example a database unique ID or the actual EVSE ID. This field
can never be changed, modified or renamed. This is the technical identification
of the EVSE, not to be used as human readable identification, use the field:
evse_id for that.
 * @property evse_id Compliant with the following specification for EVSE ID from "eMI3 standard
version V1.0" (http://emi3group.com/documents-links/) "Part 2: business
objects.".
 * @property connector_id Identifier of the connector within the EVSE.
 * @property connector_standard The standard of the installed connector.
 * @property connector_format The format (socket/cable) of the installed connector.
 *
 */
data class CdrLocation(
    val id: String,
    val name: String? = null,
    val address: String,
    val city: String,
    val postal_code: String,
    val country: String,
    val coordinates: String,
    val evse_uid: String,
    val evse_id: String,
    val connector_id: String,
    val connector_standard: ConnectorType,
    val connector_format: ConnectorFormat,
    val connector_power_type: PowerType,
)
