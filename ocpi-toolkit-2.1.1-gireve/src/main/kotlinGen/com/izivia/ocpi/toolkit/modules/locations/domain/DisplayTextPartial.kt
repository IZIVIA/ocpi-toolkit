//  ----------
//  - WARNING -
//  ----------
//  This code is generated AND MUST NOT BE EDITED
//  ----------
package com.izivia.ocpi.toolkit.modules.locations.domain

/**
 * Partial representation of [com.izivia.ocpi.toolkit.modules.locations.domain.DisplayText]
 */
public data class DisplayTextPartial(
  public val language: String?,
  public val text: String?,
)

public fun DisplayText.toPartial(): DisplayTextPartial {
   return DisplayTextPartial(
     language = language,
    text = text
   )
}

public fun List<DisplayText>.toPartial(): List<DisplayTextPartial> {
   return mapNotNull { it.toPartial() }
}
