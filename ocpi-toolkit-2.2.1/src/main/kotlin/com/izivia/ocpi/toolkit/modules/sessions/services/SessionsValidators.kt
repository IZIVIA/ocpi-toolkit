package com.izivia.ocpi.toolkit.modules.sessions.services

import com.izivia.ocpi.toolkit.common.validation.hasMaxLengthOf
import com.izivia.ocpi.toolkit.common.validation.isCountryCode
import com.izivia.ocpi.toolkit.common.validation.isCurrencyCode
import com.izivia.ocpi.toolkit.common.validation.isPrintableAscii
import com.izivia.ocpi.toolkit.modules.cdr.domain.*
import com.izivia.ocpi.toolkit.modules.sessions.domain.*
import com.izivia.ocpi.toolkit.modules.types.Price
import com.izivia.ocpi.toolkit.modules.types.PricePartial
import com.izivia.ocpi.toolkit.modules.types.toPartial
import org.valiktor.functions.isGreaterThanOrEqualTo
import org.valiktor.validate
import java.math.BigDecimal

fun SessionPartial.validate(): SessionPartial = validate(this) {
    validate(SessionPartial::countryCode).isCountryCode(caseSensitive = false, alpha2 = true)
    validate(SessionPartial::partyId).isPrintableAscii().hasMaxLengthOf(3)
    validate(SessionPartial::id).isPrintableAscii().hasMaxLengthOf(36)
    // startDateTime nothing to validate
    // endDateTime nothing to validate
    validate(SessionPartial::kwh).isGreaterThanOrEqualTo(0)
    cdrToken?.validate()
    // authMethod: nothing to validate
    validate(SessionPartial::authorizationReference).isPrintableAscii().hasMaxLengthOf(36)
    validate(SessionPartial::locationId).isPrintableAscii().hasMaxLengthOf(36)
    validate(SessionPartial::evseUid).isPrintableAscii().hasMaxLengthOf(36)
    validate(SessionPartial::connectorId).isPrintableAscii().hasMaxLengthOf(36)
    validate(SessionPartial::meterId).isPrintableAscii().hasMaxLengthOf(255)
    validate(SessionPartial::currency).isCurrencyCode(false)
    chargingPeriods?.forEach { chargingPeriod -> chargingPeriod.validate() }
    totalCost?.validate()
    // status: nothing to validate
    // lastUpdated: nothing to validate
}

fun ChargingPreferencesPartial.validate(): ChargingPreferencesPartial = validate(this) {
    // profileType nothing to validate
    // departureTime nothing to validate
    validate(ChargingPreferencesPartial::energyNeed).isGreaterThanOrEqualTo(0)
    // dischargeAllowed nothing to validate
}

fun CdrTokenPartial.validate(): CdrTokenPartial = validate(this) {
    validate(CdrTokenPartial::countryCode).isCountryCode(caseSensitive = false, alpha2 = true)
    validate(CdrTokenPartial::partyId).isPrintableAscii().hasMaxLengthOf(3)
    validate(CdrTokenPartial::uid).isPrintableAscii().hasMaxLengthOf(36)
    // tokenType : nothing to validate
    validate(CdrTokenPartial::contractId).isPrintableAscii().hasMaxLengthOf(36)
}

fun ChargingPeriodPartial.validate(): ChargingPeriodPartial = validate(this) {
    // startDateTime nothing to validate
    dimensions?.forEach { dimension -> dimension.validate() }
    validate(ChargingPeriodPartial::tariffId).isPrintableAscii().hasMaxLengthOf(36)
}

fun CdrDimensionPartial.validate(): CdrDimensionPartial = validate(this) {
    // cdrDimensionType : nothing to validate
    validate(CdrDimensionPartial::volume).isGreaterThanOrEqualTo(BigDecimal.ZERO)
}

fun PricePartial.validate(): PricePartial = validate(this) {
    validate(PricePartial::exclVat).isGreaterThanOrEqualTo(BigDecimal.ZERO)
    validate(PricePartial::inclVat).isGreaterThanOrEqualTo(BigDecimal.ZERO)
}

fun Session.validate(): Session = validate(this) {
    toPartial().validate()
}

fun ChargingPreferences.validate(): ChargingPreferences = validate(this) {
    toPartial().validate()
}

fun CdrToken.validate(): CdrToken = validate(this) {
    toPartial().validate()
}

fun ChargingPeriod.validate(): ChargingPeriod = validate(this) {
    toPartial().validate()
}

fun CdrDimension.validate(): CdrDimension = validate(this) {
    toPartial().validate()
}

fun Price.validate(): Price = validate(this) {
    toPartial().validate()
}
