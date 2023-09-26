package com.izivia.ocpi.toolkit.modules.tokens.mock

import com.izivia.ocpi.toolkit.modules.sessions.domain.ProfileType
import com.izivia.ocpi.toolkit.modules.tokens.domain.*
import com.izivia.ocpi.toolkit.modules.types.DisplayText
import java.time.Instant

val validEnergyContract = EnergyContract(
    supplier_name = "Greenpeace Energy eG",
    contract_id = "0123456789"
)

val validDisplayText = DisplayText(
    language = "fr",
    text = "abcdef"
)

val validLocationReference = LocationReferences(
    location_id = "LOC1",
    evse_uids = listOf("BE*BEC*E041503001", "BE*BEC*E041503002")
)

val validTokenAppUser = Token(
    country_code = "DE",
    party_id = "TNM",
    uid = "bdf21bce-fc97-11e8-8eb2-f2801f1b9fd1",
    type = TokenType.APP_USER,
    contract_id = "DE8ACC12E46L89",
    issuer = "TheNewMotion",
    valid = true,
    whitelist = WhitelistType.ALLOWED,
    last_updated = Instant.parse("2018-12-10T17:16:15Z")
)

val validTokenFullRfid = Token(
    country_code = "DE",
    party_id = "TNM",
    uid = "12345678905880",
    type = TokenType.RFID,
    contract_id = "DE8ACC12E46L89",
    visual_number = "DF000-2001-8999-1",
    issuer = "TheNewMotion",
    group_id = "DF000-2001-8999",
    valid = true,
    whitelist = WhitelistType.ALLOWED,
    language = "it",
    default_profile_type = ProfileType.GREEN,
    energy_contract = validEnergyContract,
    last_updated = Instant.now()
)

val validAuthorizationInfo = AuthorizationInfo(
    allowed = AllowedType.ALLOWED,
    token = validTokenFullRfid,
    location = validLocationReference,
    authorization_reference = "567890",
    info = validDisplayText
)
