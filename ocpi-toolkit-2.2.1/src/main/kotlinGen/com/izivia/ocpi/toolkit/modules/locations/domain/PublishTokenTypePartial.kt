//  ----------
//  - WARNING -
//  ----------
//  This code is generated AND MUST NOT BE EDITED
//  ----------
package com.izivia.ocpi.toolkit.modules.locations.domain

import com.izivia.ocpi.toolkit.common.CiString
import com.izivia.ocpi.toolkit.modules.tokens.domain.TokenType
import kotlin.String
import kotlin.collections.List

/**
 * Partial representation of [com.izivia.ocpi.toolkit.modules.locations.domain.PublishTokenType]
 */
public data class PublishTokenTypePartial(
  public val uid: CiString?,
  public val type: TokenType?,
  public val visual_number: String?,
  public val issuer: String?,
  public val group_id: CiString?,
)

public fun PublishTokenType.toPartial(): PublishTokenTypePartial {
   return PublishTokenTypePartial(
     uid = uid,
    type = type,
    visual_number = visual_number,
    issuer = issuer,
    group_id = group_id
   )
}

public fun List<PublishTokenType>.toPartial(): List<PublishTokenTypePartial> {
   return mapNotNull { it.toPartial() }
}
