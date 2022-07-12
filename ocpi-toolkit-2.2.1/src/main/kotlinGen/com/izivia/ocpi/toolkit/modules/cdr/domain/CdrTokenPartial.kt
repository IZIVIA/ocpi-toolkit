//  ----------
//  - WARNING -
//  ----------
//  This code is generated AND MUST NOT BE EDITED
//  ----------
package com.izivia.ocpi.toolkit.modules.cdr.domain

import com.izivia.ocpi.toolkit.common.CiString
import kotlin.collections.List

/**
 * Partial representation of [com.izivia.ocpi.toolkit.modules.cdr.domain.CdrToken]
 */
public data class CdrTokenPartial(
  public val uid: CiString?,
  public val type: TokenType?,
  public val contract_id: CiString?,
)

public fun CdrToken.toPartial(): CdrTokenPartial {
   return CdrTokenPartial(
     uid = uid,
    type = type,
    contract_id = contract_id
   )
}

public fun List<CdrToken>.toPartial(): List<CdrTokenPartial> {
   return mapNotNull { it.toPartial() }
}
