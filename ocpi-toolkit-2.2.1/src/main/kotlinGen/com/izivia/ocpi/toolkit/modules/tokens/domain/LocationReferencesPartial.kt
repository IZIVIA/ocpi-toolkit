//  ----------
//  - WARNING -
//  ----------
//  This code is generated AND MUST NOT BE EDITED
//  ----------
package com.izivia.ocpi.toolkit.modules.tokens.domain

import com.izivia.ocpi.toolkit.common.CiString
import kotlin.collections.List

/**
 * Partial representation of [com.izivia.ocpi.toolkit.modules.tokens.domain.LocationReferences]
 */
public data class LocationReferencesPartial(
  public val location_id: CiString?,
  public val evse_uids: List<CiString>?,
)

public fun LocationReferences.toPartial(): LocationReferencesPartial {
   return LocationReferencesPartial(
     location_id = location_id,
    evse_uids = evse_uids
   )
}

public fun List<LocationReferences>.toPartial(): List<LocationReferencesPartial> {
   return mapNotNull { it.toPartial() }
}
