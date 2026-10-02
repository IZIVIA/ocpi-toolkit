package com.izivia.ocpi.toolkit.modules.tokens.domain

import com.izivia.ocpi.toolkit.annotations.Partial
import com.izivia.ocpi.toolkit.common.CiString

/**
 * References to location details.
 *
 * @property locationId (max-length 36) Unique identifier for the location.
 * @property evseUids (max-length 36) Unique identifiers for EVSEs within the CPO’s platform for the EVSE within the
 * given location. Cardinality `*`: a CPO may omit it, which reads as `null`.
 * @constructor
 */
@Partial
data class LocationReferences(
    val locationId: CiString,
    val evseUids: List<CiString>? = null,
)
