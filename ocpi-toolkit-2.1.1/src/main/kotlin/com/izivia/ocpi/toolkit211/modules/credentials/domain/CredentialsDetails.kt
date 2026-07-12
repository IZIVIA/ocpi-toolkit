package com.izivia.ocpi.toolkit211.modules.credentials.domain

import com.izivia.ocpi.toolkit211.common.CiString
import com.izivia.ocpi.toolkit211.modules.locations.domain.BusinessDetails

data class CredentialsDetails(
    val businessDetails: BusinessDetails,
    val partyId: CiString,
    val countryCode: CiString,
)

fun Credentials.toDetails(): CredentialsDetails = CredentialsDetails(
    businessDetails = businessDetails,
    partyId = partyId,
    countryCode = countryCode,
)
