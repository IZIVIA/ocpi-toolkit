//  ----------
//  - WARNING -
//  ----------
//  This code is generated AND MUST NOT BE EDITED
//  ----------
package com.izivia.ocpi.toolkit.modules.locations.domain

import kotlin.String
import kotlin.collections.List

/**
 * Partial representation of [com.izivia.ocpi.toolkit.modules.locations.domain.BusinessDetails]
 */
public data class BusinessDetailsPartial(
  public val name: String?,
  public val website: String?,
  public val logo: ImagePartial?,
)

public fun BusinessDetails.toPartial(): BusinessDetailsPartial {
   return BusinessDetailsPartial(
     name = name,
    website = website,
    logo = logo?.toPartial()
   )
}

public fun List<BusinessDetails>.toPartial(): List<BusinessDetailsPartial> {
   return mapNotNull { it.toPartial() }
}
