//  ----------
//  - WARNING -
//  ----------
//  This code is generated AND MUST NOT BE EDITED
//  ----------
package com.izivia.ocpi.toolkit.modules.tokens.domain

import kotlin.String
import kotlin.collections.List

/**
 * Partial representation of [com.izivia.ocpi.toolkit.modules.tokens.domain.EnergyContract]
 */
public data class EnergyContractPartial(
  public val supplier_name: String?,
  public val contract_id: String?,
)

public fun EnergyContract.toPartial(): EnergyContractPartial {
   return EnergyContractPartial(
     supplier_name = supplier_name,
    contract_id = contract_id
   )
}

public fun List<EnergyContract>.toPartial(): List<EnergyContractPartial> {
   return mapNotNull { it.toPartial() }
}
