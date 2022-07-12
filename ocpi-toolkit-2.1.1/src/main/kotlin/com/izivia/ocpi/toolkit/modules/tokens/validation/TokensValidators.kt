package com.izivia.ocpi.toolkit.modules.tokens.validation

import com.izivia.ocpi.toolkit.common.validation.hasMaxLengthOf
import com.izivia.ocpi.toolkit.common.validation.isLanguage
import com.izivia.ocpi.toolkit.common.validation.isPrintableAscii
import com.izivia.ocpi.toolkit.modules.tokens.domain.Token
import com.izivia.ocpi.toolkit.modules.tokens.domain.TokenPartial
import com.izivia.ocpi.toolkit.modules.tokens.domain.toPartial
import org.valiktor.validate

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

fun Token.validate(): Token = validate(this) {
    toPartial().validate()
}
