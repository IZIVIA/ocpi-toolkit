package com.izivia.ocpi.toolkit.modules.tokens.domain

/**
 * References to location details.
 * @property location_id (max-length 39) Unique identifier for the location.
 * @property evse_uids (list of string of max-length 39) Unique identifier for EVSEs within the CPO's platform for the
 * EVSE within the the given location.
 * @property connector_ids (list of string of max-length 39) Identifies the connectors within the given EVSEs.
 * @constructor
 */
data class LocationReferences(
    val location_id: String,
    val evse_uids: List<String>,
    val connector_ids: List<String>
)
