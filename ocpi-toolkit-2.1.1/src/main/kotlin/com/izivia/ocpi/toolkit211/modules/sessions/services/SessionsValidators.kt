package com.izivia.ocpi.toolkit211.modules.sessions.services

import com.izivia.ocpi.toolkit211.common.validation.*
import com.izivia.ocpi.toolkit211.modules.cdr.services.validate
import com.izivia.ocpi.toolkit211.modules.locations.services.validate
import com.izivia.ocpi.toolkit211.modules.sessions.domain.Session
import com.izivia.ocpi.toolkit211.modules.sessions.domain.SessionPartial
import com.izivia.ocpi.toolkit211.modules.sessions.domain.toPartial
import org.valiktor.functions.isGreaterThanOrEqualTo
import org.valiktor.validate
import java.math.BigDecimal

fun SessionPartial.validate(): SessionPartial = validate(this) {
    validate(SessionPartial::id).isPrintableAscii().hasMaxLengthOf(36)
    // startDatetime nothing to validate
    // endDatetime nothing to validate
    validate(SessionPartial::kwh).isGreaterThanOrEqualTo(BigDecimal.ZERO)
    validate(SessionPartial::authId).isPrintableAscii().hasMaxLengthOf(36)
    // authMethod: nothing to validate
    location?.validate()
    validate(SessionPartial::meterId).isPrintableAscii().hasMaxLengthOf(255)
    validate(SessionPartial::currency).isCurrencyCode(false)
    chargingPeriods?.forEach { chargingPeriod -> chargingPeriod.validate() }
    validate(SessionPartial::totalCost).isBigDecimalPositive()
    // status: nothing to validate
    // lastUpdated: nothing to validate
}

fun Session.validate(): Session = validate(this) {
    toPartial().validate()
}
