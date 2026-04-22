package com.izivia.ocpi.toolkit211.modules.tokens.services

import com.izivia.ocpi.toolkit211.common.validation.*
import com.izivia.ocpi.toolkit211.modules.tokens.domain.*
import org.valiktor.DefaultConstraintViolation
import org.valiktor.validate

fun AuthorizationInfoPartial.validate(): AuthorizationInfoPartial = validate(this) {
    // allowed: nothing to validate
    location?.validate()
}

fun TokenPartial.validate(): TokenPartial = validate(this) {
    validate(TokenPartial::uid).isPrintableAscii().hasMaxLengthOf(36)
    // type: nothing to validate
    validate(TokenPartial::authId).isPrintableAscii().hasMaxLengthOf(36)
    validate(TokenPartial::visualNumber).isPrintableUtf8().hasMaxLengthOf(64)
    validate(TokenPartial::issuer).isPrintableUtf8().hasMaxLengthOf(64)
    // valid: nothing to validate
    // whitelist: nothing to validate
    validate(TokenPartial::language).isPrintableAscii().hasMaxLengthOf(2)
    // lastUpdated: nothing to validate
}

fun LocationReferencesPartial.validate(): LocationReferencesPartial = validate(this) {
    validate(LocationReferencesPartial::locationId).isPrintableAscii().hasMaxLengthOf(36)
    evseUids?.forEach { evseUid ->
        if (evseUid.length > 36) {
            constraintViolations.add(
                DefaultConstraintViolation(
                    property = "evseUids",
                    constraint = MaxLengthContraint(36),
                ),
            )
        }
        if (!evseUid.isPrintableAscii()) {
            constraintViolations.add(
                DefaultConstraintViolation(
                    property = "evseUids",
                    constraint = PrintableAsciiConstraint(),
                ),
            )
        }
    }
}

fun AuthorizationInfo.validate(): AuthorizationInfo = validate(this) {
    toPartial().validate()
}

fun Token.validate(): Token = validate(this) {
    toPartial().validate()
}

fun LocationReferences.validate(): LocationReferences = validate(this) {
    toPartial().validate()
}
