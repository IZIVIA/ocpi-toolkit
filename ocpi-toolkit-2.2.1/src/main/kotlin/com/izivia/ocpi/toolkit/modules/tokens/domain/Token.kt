package com.izivia.ocpi.toolkit.modules.tokens.domain

import com.izivia.ocpi.toolkit.annotations.Partial
import com.izivia.ocpi.toolkit.common.CiString
import com.izivia.ocpi.toolkit.modules.sessions.domain.ProfileType
import java.time.Instant

/**
 * The combination of uid and type should be unique for every token within the eMSP’s system.
 *
 * NOTE : OCPP supports group_id (or ParentID as it is called in OCPP 1.5/1.6) OCPP 1.5/1.6 only support group ID’s with
 * maximum length of string(20), case insensitive. As long as EV-driver can be expected to charge at an OCPP
 * 1.5/1.6 Charge Point, it is adviced to not used a group_id longer then 20.
 *
 * @property country_code ISO-3166 alpha-2 country code of the MSP that 'owns' this Token.
 * @property party_id ID of the eMSP that 'owns' this Token (following the ISO-15118 standard).
 * @property uid Unique ID by which this Token, combined with the Token type, can be identified.
 * This is the field used by CPO system (RFID reader on the Charge Point) to
 * identify this token.
 * Currently, in most cases: type=RFID, this is the RFID hidden ID as read by the
 * RFID reader, but that is not a requirement.
 * If this is a APP_USER or AD_HOC_USER Token, it will be a uniquely, by the eMSP,
 * generated ID.
 * This field is named uid instead of id to prevent confusion with: contract_id.
 * @property type Type of the token
 * @property contract_id Uniquely identifies the EV Driver contract token within the eMSP’s platform (and
 * suboperator platforms). Recommended to follow the specification for eMA ID
 * from "eMI3 standard version V1.0" (http://emi3group.com/documents-links/)
 * "Part 2: business objects."
 * @property visual_number Visual readable number/identification as printed on the Token (RFID card), might
 * be equal to the contract_id.
 * @property issuer Issuing company, most of the times the name of the company printed on the
 * token (RFID card), not necessarily the eMSP.
 * @property group_id This ID groups a couple of tokens. This can be used to make two or more
 * tokens work as one, so that a session can be started with one token and
 * stopped with another, handy when a card and key-fob are given to the EV-driver.
 * Beware that OCPP 1.5/1.6 only support group_ids (it is called parentId in OCPP
 * 1.5/1.6) with a maximum length of 20.
 * @property valid Is this Token valid
 * @property whitelist Indicates what type of white-listing is allowed.
 * @property language Language Code ISO 639-1. This optional field indicates the Token owner’s
 * preferred interface language. If the language is not provided or not supported
 * then the CPO is free to choose its own language.
 * @property default_profile_type The default Charging Preference. When this is provided, and a charging session
 * is started on an Charge Point that support Preference base Smart Charging and
 * support this ProfileType, the Charge Point can start using this ProfileType,
 * without this having to be set via: Set Charging Preferences.
 * @property energy_contract When the Charge Point supports using your own energy supplier/contract at a
 * Charge Point, information about the energy supplier/contract is needed so the
 * CPO knows which energy supplier to use.
 * NOTE: In a lot of countries it is currently not allowed/possible to use a drivers
 * own energy supplier/contract at a Charge Point.
 * @property last_updated Timestamp when this Token was last updated (or created).
 *
 * @constructor
 */
@Partial
data class Token(
    val country_code: CiString,
    val party_id: CiString,
    val uid: CiString,
    val type: TokenType,
    val contract_id: CiString,
    val visual_number: String?,
    val issuer: String,
    val group_id: CiString?,
    val valid: Boolean,
    val whitelist: WhitelistType,
    val language: String?,
    val default_profile_type: ProfileType?,
    val energy_contract: EnergyContract?,
    val last_updated: Instant
)