package com.izivia.ocpi.toolkit.modules.credentials.domain

import com.izivia.ocpi.toolkit.common.CiString
import com.izivia.ocpi.toolkit.modules.locations.domain.BusinessDetails

/**
 * @property role Type of role
 * @property businessDetails Details of this party
 * @property partyId CPO, eMSP (or other role) ID of this party (following the ISO-15118 standard).
 * @property countryCode ISO-3166 alpha-2 country code of the country this party is operating in.
 */
data class CredentialRole(
    val role: Role,
    val businessDetails: BusinessDetails,
    val partyId: CiString,
    val countryCode: CiString,
)
