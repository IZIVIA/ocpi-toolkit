package com.izivia.ocpi.toolkit.modules.tokens.mock

import com.izivia.ocpi.toolkit.modules.sessions.domain.ProfileType
import com.izivia.ocpi.toolkit.modules.tokens.domain.*
import com.izivia.ocpi.toolkit.modules.types.DisplayText
import java.time.Instant

val validEnergyContract = EnergyContract(
    supplierName = "Greenpeace Energy eG",
    contractId = "0123456789",
)

val validDisplayText = DisplayText(
    language = "fr",
    text = "abcdef",
)

val validLocationReference = LocationReferences(
    locationId = "LOC1",
    evseUids = listOf("BE*BEC*E041503001", "BE*BEC*E041503002"),
)

val validTokenAppUser = Token(
    countryCode = "DE",
    partyId = "TNM",
    uid = "bdf21bce-fc97-11e8-8eb2-f2801f1b9fd1",
    type = TokenType.APP_USER,
    contractId = "DE8ACC12E46L89",
    issuer = "TheNewMotion",
    valid = true,
    whitelist = WhitelistType.ALLOWED,
    lastUpdated = Instant.parse("2018-12-10T17:16:15Z"),
)

val validTokenFullRfid = Token(
    countryCode = "DE",
    partyId = "TNM",
    uid = "12345678905880",
    type = TokenType.RFID,
    contractId = "DE8ACC12E46L89",
    visualNumber = "DF000-2001-8999-1",
    issuer = "TheNewMotion",
    groupId = "DF000-2001-8999",
    valid = true,
    whitelist = WhitelistType.ALLOWED,
    language = "it",
    defaultProfileType = ProfileType.GREEN,
    energyContract = validEnergyContract,
    lastUpdated = Instant.now(),
)

val validAuthorizationInfo = AuthorizationInfo(
    allowed = AllowedType.ALLOWED,
    token = validTokenFullRfid,
    location = validLocationReference,
    authorizationReference = "567890",
    info = validDisplayText,
)
