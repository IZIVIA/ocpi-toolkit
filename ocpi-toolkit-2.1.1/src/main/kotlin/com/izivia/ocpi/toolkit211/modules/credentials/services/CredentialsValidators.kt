package com.izivia.ocpi.toolkit211.modules.credentials.services

import com.izivia.ocpi.toolkit211.common.validation.hasMaxLengthOf
import com.izivia.ocpi.toolkit211.common.validation.isCountryCode
import com.izivia.ocpi.toolkit211.common.validation.isPrintableAscii
import com.izivia.ocpi.toolkit211.common.validation.isUrl
import com.izivia.ocpi.toolkit211.modules.credentials.domain.Credentials
import com.izivia.ocpi.toolkit211.modules.locations.services.validate
import org.valiktor.validate

fun Credentials.validate(): Credentials = validate(this) {
    validate(Credentials::token).isPrintableAscii().hasMaxLengthOf(64)
    validate(Credentials::url).isUrl()
    businessDetails.validate()
    validate(Credentials::partyId).isPrintableAscii().hasMaxLengthOf(3)
    validate(Credentials::countryCode).isCountryCode(caseSensitive = true, alpha2 = true)
}
