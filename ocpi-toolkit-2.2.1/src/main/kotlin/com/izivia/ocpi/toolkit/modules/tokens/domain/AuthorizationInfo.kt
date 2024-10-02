package com.izivia.ocpi.toolkit.modules.tokens.domain

import com.izivia.ocpi.toolkit.annotations.Partial
import com.izivia.ocpi.toolkit.common.CiString
import com.izivia.ocpi.toolkit.modules.types.DisplayText

/**
 * @property allowed Status of the Token, and whether charging is allowed at the optionally
 * given location.
 * @property token The complete Token object for which this authorization was requested.
 * @property location Optional reference to the location if it was included in the request, and if
 * the EV driver is allowed to charge at that location. Only the EVSEs the
 * EV driver is allowed to charge at are returned.
 * @property authorizationReference (max-length 36) Reference to the authorization given by the eMSP, when given, this
 * reference will be provided in the relevant Session and/or CDR.
 * @property info Optional display text, additional information to the EV driver.
 *
 * @constructor
 */
@Partial
data class AuthorizationInfo(
    val allowed: AllowedType,
    val token: Token,
    val location: LocationReferences? = null,
    val authorizationReference: CiString? = null,
    val info: DisplayText? = null,
)
