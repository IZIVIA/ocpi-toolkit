package com.izivia.ocpi.toolkit.modules.tokens.domain

import com.izivia.ocpi.toolkit.modules.types.DisplayText

/**
 * Updated by Gireve: added authorization_id
 *
 * @property allowed Status of the Token, and whether charging is allowed at the optionally given location.
 * @property location Optional reference to the location if it was included in the request, and if the EV driver is
 * allowed to charge at that location. Only the EVSEs/Connectors the EV driver is allowed to charge at are returned.
 * @property info Optional display text, additional information to the EV driver.
 * @property authorization_id Unique identifier of the authorization within the eMSP platform. The CPO must store this
 * information to send it in Sessions and CDRs related to this Authorization. Please refer to paragraph 2.3.2 New
 * attribute « authorization_id »
 */
data class AuthorizationInfo(
    val allowed: Allowed,
    val location: LocationReferences?,
    val info: DisplayText?,
    val authorization_id: String
)
