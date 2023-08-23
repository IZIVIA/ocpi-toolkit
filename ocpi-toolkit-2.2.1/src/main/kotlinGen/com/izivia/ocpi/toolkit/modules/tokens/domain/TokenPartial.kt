//  ----------
//  - WARNING -
//  ----------
//  This code is generated AND MUST NOT BE EDITED
//  ----------
package com.izivia.ocpi.toolkit.modules.tokens.domain

import com.izivia.ocpi.toolkit.common.CiString
import com.izivia.ocpi.toolkit.modules.sessions.domain.ProfileType
import java.time.Instant
import kotlin.Boolean
import kotlin.String
import kotlin.collections.List

/**
 * Partial representation of [com.izivia.ocpi.toolkit.modules.tokens.domain.Token]
 */
public data class TokenPartial(
  public val country_code: CiString?,
  public val party_id: CiString?,
  public val uid: CiString?,
  public val type: TokenType?,
  public val contract_id: CiString?,
  public val visual_number: String?,
  public val issuer: String?,
  public val group_id: CiString?,
  public val valid: Boolean?,
  public val whitelist: WhitelistType?,
  public val language: String?,
  public val default_profile_type: ProfileType?,
  public val energy_contract: EnergyContractPartial?,
  public val last_updated: Instant?,
)

public fun Token.toPartial(): TokenPartial {
   return TokenPartial(
     country_code = country_code,
    party_id = party_id,
    uid = uid,
    type = type,
    contract_id = contract_id,
    visual_number = visual_number,
    issuer = issuer,
    group_id = group_id,
    valid = valid,
    whitelist = whitelist,
    language = language,
    default_profile_type = default_profile_type,
    energy_contract = energy_contract?.toPartial(),
    last_updated = last_updated
   )
}

public fun List<Token>.toPartial(): List<TokenPartial> {
   return mapNotNull { it.toPartial() }
}
