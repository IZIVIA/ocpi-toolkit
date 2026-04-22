package com.izivia.ocpi.toolkit211.modules.tokens.domain

import com.izivia.ocpi.toolkit.annotations.Partial
import com.izivia.ocpi.toolkit211.common.CiString
import java.time.Instant

/**
 * @property uid (max-length 36) Unique ID by which this Token can be identified.
 * @property type Type of the token.
 * @property authId (max-length 36) Uniquely identifies the EV Driver contract token within the eMSP's platform.
 * @property visualNumber (max-length 64) Visual readable number/identification as printed on the Token.
 * @property issuer (max-length 64) Issuing company, name of the company printed on the token.
 * @property valid Is this Token valid.
 * @property whitelist Indicates what type of white-listing is allowed.
 * @property language (max-length 2) Language Code ISO 639-1.
 * @property lastUpdated Timestamp when this Token was last updated (or created).
 */
@Partial
data class Token(
    val uid: CiString,
    val type: TokenType,
    val authId: CiString,
    val visualNumber: String? = null,
    val issuer: String,
    val valid: Boolean,
    val whitelist: WhitelistType,
    val language: String? = null,
    val lastUpdated: Instant,
)
