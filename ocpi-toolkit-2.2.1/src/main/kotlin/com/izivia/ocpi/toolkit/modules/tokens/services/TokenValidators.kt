package com.izivia.ocpi.toolkit.modules.tokens.services

import com.izivia.ocpi.toolkit.common.validation.*
import com.izivia.ocpi.toolkit.modules.tokens.domain.*
import com.izivia.ocpi.toolkit.modules.types.DisplayText
import com.izivia.ocpi.toolkit.modules.types.DisplayTextPartial
import com.izivia.ocpi.toolkit.modules.types.toPartial
import org.valiktor.DefaultConstraintViolation
import org.valiktor.validate

fun AuthorizationInfoPartial.validate(): AuthorizationInfoPartial = validate(this) {
    // allowed : nothing to validate
    token?.validate()
    location?.validate()
    validate(AuthorizationInfoPartial::authorization_reference).isPrintableAscii().hasMaxLengthOf(36)
    info?.validate()
}

fun TokenPartial.validate(): TokenPartial = validate(this) {
    validate(TokenPartial::country_code).isCountryCode(caseSensitive = false, alpha2 = true)
    validate(TokenPartial::party_id).isPrintableAscii().hasMaxLengthOf(3)
    validate(TokenPartial::uid).isPrintableAscii().hasMaxLengthOf(36)
    //type : nothing to validate
    validate(TokenPartial::contract_id).isPrintableAscii().hasMaxLengthOf(36)
    validate(TokenPartial::visual_number).isPrintableAscii().hasMaxLengthOf(64)
    validate(TokenPartial::issuer).isPrintableAscii().hasMaxLengthOf(64)
    validate(TokenPartial::group_id).isPrintableAscii().hasMaxLengthOf(36)
    // valid : nothing to validate
    // whitelist : nothing to validate
    validate(TokenPartial::language).isPrintableAscii().hasMaxLengthOf(2)
    // default_profile_type : nothing to validate
    energy_contract?.validate()
    // last_updated: nothing to validate
}

fun LocationReferencesPartial.validate(): LocationReferencesPartial = validate(this) {
    validate(LocationReferencesPartial::location_id).isPrintableAscii().hasMaxLengthOf(36)
    evse_uids?.forEach { evse_uid ->
        if (evse_uid.length > 36) {
            constraintViolations.add(
                DefaultConstraintViolation(
                    property = "evse_uids",
                    constraint = MaxLengthContraint(36)
                )
            )
        }
        if (!evse_uid.isPrintableAscii()) {
            constraintViolations.add(
                DefaultConstraintViolation(
                    property = "evse_uids",
                    constraint = PrintableAsciiConstraint()
                )
            )
        }
    }
}

fun EnergyContractPartial.validate(): EnergyContractPartial = validate(this) {
    validate(EnergyContractPartial::supplier_name).isPrintableAscii().hasMaxLengthOf(64)
    validate(EnergyContractPartial::contract_id).isPrintableAscii().hasMaxLengthOf(64)
}

fun DisplayTextPartial.validate(): DisplayTextPartial = validate(this) {
    validate(DisplayTextPartial::language).isLanguage()
    validate(DisplayTextPartial::text)
        .isPrintableAscii()
        .hasNoHtml()
        .hasMaxLengthOf(512)
}

fun DisplayText.validate(): DisplayText = org.valiktor.validate(this) {
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

