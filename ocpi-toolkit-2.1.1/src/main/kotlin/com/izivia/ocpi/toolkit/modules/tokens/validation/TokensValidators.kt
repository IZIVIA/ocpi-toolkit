package com.izivia.ocpi.toolkit.modules.tokens.validation

import com.izivia.ocpi.toolkit.common.validation.hasMaxLengthOf
import com.izivia.ocpi.toolkit.common.validation.isLanguage
import com.izivia.ocpi.toolkit.common.validation.isPrintableAscii
import com.izivia.ocpi.toolkit.modules.locations.validation.validate
import com.izivia.ocpi.toolkit.modules.tokens.domain.*
import org.valiktor.validate

private data class StringWrapper(val str: String)

fun TokenPartial.validate(): TokenPartial = validate(this) {
    validate(TokenPartial::uid).isPrintableAscii().hasMaxLengthOf(36)
    // type: nothing to validate
    validate(TokenPartial::auth_id).isPrintableAscii().hasMaxLengthOf(36)
    validate(TokenPartial::visual_number).isPrintableAscii().hasMaxLengthOf(64)
    validate(TokenPartial::issuer).isPrintableAscii().hasMaxLengthOf(64)
    // valid: nothing to validate
    // whitelist: nothing to validate
    validate(TokenPartial::language).isLanguage()
    // last_updated: : nothing to validate
}

fun LocationReferences.validate(): LocationReferences = validate(this) {
    validate(LocationReferences::location_id)
    evse_uids
        .map { StringWrapper(it) }
        .forEach {
            validate(it) {
                validate(StringWrapper::str).isPrintableAscii().hasMaxLengthOf(39)
            }
        }
    connector_ids
        .map { StringWrapper(it) }
        .forEach {
            validate(it) {
                validate(StringWrapper::str).isPrintableAscii().hasMaxLengthOf(39)
            }
        }
}

fun AuthorizationInfo.validate(): AuthorizationInfo = validate(this) {
    // allowed: nothing to validate
    location?.validate()
    info?.validate()
}

fun Token.validate(): Token = validate(this) {
    toPartial().validate()
}
