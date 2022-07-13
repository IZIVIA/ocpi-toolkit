//  ----------
//  - WARNING -
//  ----------
//  This code is generated AND MUST NOT BE EDITED
//  ----------
package com.izivia.ocpi.toolkit.modules.cdr.domain

import com.izivia.ocpi.toolkit.common.CiString
import kotlin.collections.List

/**
 * Partial representation of [com.izivia.ocpi.toolkit.modules.cdr.domain.SignedValue]
 */
public data class SignedValuePartial(
  public val nature: CiString?,
  public val plain_data: CiString?,
  public val signed_data: CiString?,
)

public fun SignedValue.toPartial(): SignedValuePartial {
   return SignedValuePartial(
     nature = nature,
    plain_data = plain_data,
    signed_data = signed_data
   )
}

public fun List<SignedValue>.toPartial(): List<SignedValuePartial> {
   return mapNotNull { it.toPartial() }
}
