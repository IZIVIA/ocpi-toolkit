//  ----------
//  - WARNING -
//  ----------
//  This code is generated AND MUST NOT BE EDITED
//  ----------
package com.izivia.ocpi.toolkit.modules.cdr.domain

import kotlin.String
import kotlin.collections.List

/**
 * Partial representation of [com.izivia.ocpi.toolkit.modules.cdr.domain.SignedValue]
 */
public data class SignedValuePartial(
  public val nature: String?,
  public val plain_data: String?,
  public val signed_data: String?,
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
