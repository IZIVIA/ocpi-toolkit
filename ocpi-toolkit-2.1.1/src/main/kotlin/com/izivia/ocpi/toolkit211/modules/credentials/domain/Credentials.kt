package com.izivia.ocpi.toolkit211.modules.credentials.domain

import com.izivia.ocpi.toolkit211.common.CiString
import com.izivia.ocpi.toolkit211.modules.locations.domain.BusinessDetails

/**
 * @property token Case Sensitive, ASCII only. The credentials token for the other party to authenticate in your system.
 * Not encoded in Base64 or any other encoding.
 * @property url The URL to your API versions endpoint.
 * @property businessDetails Details of this party.
 * @property partyId CPO or eMSP ID of this party.
 * @property countryCode Country code of the country this party is operating in.
 */
data class Credentials(
    val token: String,
    val url: String,
    val businessDetails: BusinessDetails,
    val partyId: CiString,
    val countryCode: CiString,
)
