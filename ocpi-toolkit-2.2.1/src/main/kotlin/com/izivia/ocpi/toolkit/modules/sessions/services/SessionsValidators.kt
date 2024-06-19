package com.izivia.ocpi.toolkit.modules.sessions.services

import com.izivia.ocpi.toolkit.common.validation.*
import com.izivia.ocpi.toolkit.modules.cdr.domain.CdrDimension
import com.izivia.ocpi.toolkit.modules.cdr.domain.CdrToken
import com.izivia.ocpi.toolkit.modules.cdr.domain.ChargingPeriod
import com.izivia.ocpi.toolkit.modules.cdr.domain.toPartial
import com.izivia.ocpi.toolkit.modules.cdr.services.validate
import com.izivia.ocpi.toolkit.modules.sessions.domain.*
import com.izivia.ocpi.toolkit.modules.types.Price
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
    validate(SessionPartial::kwh).isGreaterThanOrEqualTo(BigDecimal.ZERO)
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
