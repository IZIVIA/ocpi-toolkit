package com.izivia.ocpi.toolkit211.modules.tokens.domain

import com.izivia.ocpi.toolkit.annotations.Partial

/**
 * @property allowed Status of the Token, and whether charging is allowed at the optionally given location.
 * @property location Optional reference to the location if it was included in the request.
 */
@Partial
data class AuthorizationInfo(
    val allowed: AllowedType,
    val location: LocationReferences? = null,
)
