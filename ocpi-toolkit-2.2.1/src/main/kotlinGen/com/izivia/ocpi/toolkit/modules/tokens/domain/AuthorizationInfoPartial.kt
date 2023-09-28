//  ----------
//  - WARNING -
//  ----------
//  This code is generated AND MUST NOT BE EDITED
//  ----------
package com.izivia.ocpi.toolkit.modules.tokens.domain

import com.izivia.ocpi.toolkit.common.CiString
import com.izivia.ocpi.toolkit.modules.types.DisplayTextPartial
import com.izivia.ocpi.toolkit.modules.types.toPartial
import kotlin.collections.List

/**
 * Partial representation of [com.izivia.ocpi.toolkit.modules.tokens.domain.AuthorizationInfo]
 */
public data class AuthorizationInfoPartial(
  public val allowed: AllowedType?,
  public val token: TokenPartial?,
  public val location: LocationReferencesPartial?,
  public val authorization_reference: CiString?,
  public val info: DisplayTextPartial?,
)

public fun AuthorizationInfo.toPartial(): AuthorizationInfoPartial {
   return AuthorizationInfoPartial(
     allowed = allowed,
    token = token.toPartial(),
    location = location?.toPartial(),
    authorization_reference = authorization_reference,
    info = info?.toPartial()
   )
}

public fun List<AuthorizationInfo>.toPartial(): List<AuthorizationInfoPartial> {
   return mapNotNull { it.toPartial() }
}
