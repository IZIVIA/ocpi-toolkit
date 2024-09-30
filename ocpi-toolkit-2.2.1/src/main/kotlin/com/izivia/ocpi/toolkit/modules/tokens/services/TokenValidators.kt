package com.izivia.ocpi.toolkit.modules.tokens.services

import com.izivia.ocpi.toolkit.common.validation.*
import com.izivia.ocpi.toolkit.modules.tokens.domain.*
import com.izivia.ocpi.toolkit.modules.types.DisplayText
import com.izivia.ocpi.toolkit.modules.types.toPartial
import org.valiktor.DefaultConstraintViolation
import org.valiktor.validate

fun AuthorizationInfoPartial.validate(): AuthorizationInfoPartial = validate(this) {
    // allowed : nothing to validate
    token?.validate()
    location?.validate()
    validate(AuthorizationInfoPartial::authorizationReference).isPrintableAscii().hasMaxLengthOf(36)
    info?.validate()
}

fun TokenPartial.validate(): TokenPartial = validate(this) {
    validate(TokenPartial::countryCode).isCountryCode(caseSensitive = false, alpha2 = true)
    validate(TokenPartial::partyId).isPrintableAscii().hasMaxLengthOf(3)
    validate(TokenPartial::uid).isPrintableAscii().hasMaxLengthOf(36)
    // type : nothing to validate
    validate(TokenPartial::contractId).isPrintableAscii().hasMaxLengthOf(36)
    validate(TokenPartial::visualNumber).isPrintableUtf8().hasMaxLengthOf(64)
    validate(TokenPartial::issuer).isPrintableUtf8().hasMaxLengthOf(64)
    validate(TokenPartial::groupId).isPrintableAscii().hasMaxLengthOf(36)
    // valid : nothing to validate
    // whitelist : nothing to validate
    validate(TokenPartial::language).isPrintableAscii().hasMaxLengthOf(2)
    // defaultProfileType : nothing to validate
    energyContract?.validate()
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

fun EnergyContractPartial.validate(): EnergyContractPartial = validate(this) {
    validate(EnergyContractPartial::supplierName).isPrintableUtf8().hasMaxLengthOf(64)
    validate(EnergyContractPartial::contractId).isPrintableAscii().hasMaxLengthOf(64)
}

fun DisplayText.validate(): DisplayText = validate(this) {
    toPartial().validate()
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

fun EnergyContract.validate(): EnergyContract = validate(this) {
    toPartial().validate()
}
