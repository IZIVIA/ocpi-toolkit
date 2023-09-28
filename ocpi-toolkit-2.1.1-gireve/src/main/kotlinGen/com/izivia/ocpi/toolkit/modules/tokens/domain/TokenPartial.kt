//  ----------
//  - WARNING -
//  ----------
//  This code is generated AND MUST NOT BE EDITED
//  ----------
package com.izivia.ocpi.toolkit.modules.tokens.domain

import java.time.Instant
import kotlin.Boolean
import kotlin.String
import kotlin.collections.List

/**
 * Partial representation of [com.izivia.ocpi.toolkit.modules.tokens.domain.Token]
 */
public data class TokenPartial(
  public val uid: String?,
  public val type: TokenType?,
  public val auth_id: String?,
  public val visual_number: String?,
  public val issuer: String?,
  public val valid: Boolean?,
  public val whitelist: WhitelistType?,
  public val language: String?,
  public val last_updated: Instant?,
)

public fun Token.toPartial(): TokenPartial {
   return TokenPartial(
     uid = uid,
    type = type,
    auth_id = auth_id,
    visual_number = visual_number,
    issuer = issuer,
    valid = valid,
    whitelist = whitelist,
    language = language,
    last_updated = last_updated
   )
}

public fun List<Token>.toPartial(): List<TokenPartial> {
   return mapNotNull { it.toPartial() }
}
