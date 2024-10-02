package com.izivia.ocpi.toolkit.modules.tokens.domain

import com.izivia.ocpi.toolkit.annotations.Partial

/**
 * Information about a energy contract that belongs to a Token so a driver could use his/her own energy contract when charging at a
 * Charge Point.
 *
 * @property supplierName (max-length 64) Name of the energy supplier for this token.
 * @property contractId (max-length 64) Contract ID at the energy supplier, that belongs to the owner of this token.
 *
 * @constructor
 */
@Partial
data class EnergyContract(
    val supplierName: String,
    val contractId: String?,
)
