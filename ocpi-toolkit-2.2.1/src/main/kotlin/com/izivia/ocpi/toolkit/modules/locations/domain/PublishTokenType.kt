package com.izivia.ocpi.toolkit.modules.locations.domain

import com.izivia.ocpi.toolkit.annotations.Partial
import com.izivia.ocpi.toolkit.common.CiString
import com.izivia.ocpi.toolkit.modules.tokens.domain.TokenType

/**
 * Defines the set of values that identify a token to which a Location might be published.
 *
 * - At least one of the following fields SHALL be set: uid, visual_number, or group_id.
 * - When uid is set, type SHALL also be set.
 * - When visual_number is set, issuer SHALL also be set.
 *
 * @property uid (max-length=36) Unique ID by which this Token can be identified.
 * @property type Type of the token.
 * @property visualNumber (max-length=64) Visual readable number/identification as printed on the Token (RFID card).
 * @property issuer (max-length=64) Issuing company, most of the times the name of the company printed on the token
 * (RFID card), not necessarily the eMSP.
 * @property groupId (max-length=36) This ID groups a couple of tokens. This can be used to make two or more tokens
 * work as one
 * @constructor
 */
@Partial
data class PublishTokenType(
    val uid: CiString?,
    val type: TokenType?,
    val visualNumber: String?,
    val issuer: String?,
    val groupId: CiString?,
)
