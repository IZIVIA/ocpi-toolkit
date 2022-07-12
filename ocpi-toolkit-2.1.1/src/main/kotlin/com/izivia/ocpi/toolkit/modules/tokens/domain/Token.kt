package com.izivia.ocpi.toolkit.modules.tokens.domain

import com.izivia.ocpi.toolkit.annotations.Partial
import java.time.Instant

/**
 * @property uid (max-length 36) Identification used by CPO system to identify this token. Currently, in most cases,
 * this is the RFID hidden ID as read by the RFID reader.
 * @property type Type of the token
 * @property auth_id (max-length 36) Uniquely identifies the EV Driver contract token within the eMSP's platform (and
 * suboperator platforms). Recommended to follow the specification for eMA ID from "eMI3 standard version V1.0"
 * (http://emi3group.com/documents-links/) "Part 2: business objects."
 * @property visual_number (max-length 64) Visual readable number/identification as printed on the Token (RFID card),
 * might be equal to the auth_id.
 * @property issuer (max-length 64) Issuing company, most of the times the name of the company printed on the token
 * (RFID card), not necessarily the eMSP.
 * @property valid Is this Token valid
 * @property whitelist Indicates what type of white-listing is allowed.
 * @property language (max-length 2) Language Code ISO 639-1. This optional field indicates the Token owner's preferred
 * interface language. If the language is not provided or not supported then the CPO is free to choose its own language.
 * @property last_updated Timestamp when this Token was last updated (or created).
 */
@Partial
data class Token(
    val uid: String,
    val type: TokenType,
    val auth_id: String,
    val visual_number: String?,
    val issuer: String,
    val valid: Boolean,
    val whitelist: WhitelistType,
    val language: String?,
    val last_updated: Instant
)
