package ocpi.credentials.domain

import common.CiString

/**
 * @property role Type of role
 * @property business_details Details of this party
 * @property party_id CPO, eMSP (or other role) ID of this party (following the ISO-15118 standard).
 * @property country_code ISO-3166 alpha-2 country code of the country this party is operating in.
 */
data class CredentialRole(
    val role: Role,
    val business_details: BusinessDetails,
    val party_id: CiString,
    val country_code: CiString
)
