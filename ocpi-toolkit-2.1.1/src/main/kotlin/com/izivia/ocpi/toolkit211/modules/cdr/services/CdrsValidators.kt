package com.izivia.ocpi.toolkit211.modules.cdr.services

import com.izivia.ocpi.toolkit211.common.validation.*
import com.izivia.ocpi.toolkit211.modules.cdr.domain.*
import com.izivia.ocpi.toolkit211.modules.locations.services.validate
import com.izivia.ocpi.toolkit211.modules.tariff.services.validate
import org.valiktor.functions.isGreaterThanOrEqualTo
import org.valiktor.validate
import java.math.BigDecimal

fun CdrPartial.validate(): CdrPartial = validate(this) {
    validate(CdrPartial::id).isPrintableAscii().hasMaxLengthOf(36)
    // startDateTime nothing to validate
    // stopDateTime nothing to validate
    validate(CdrPartial::authId).isPrintableAscii().hasMaxLengthOf(36)
    // authMethod: nothing to validate
    location?.validate()
    validate(CdrPartial::meterId).isPrintableAscii().hasMaxLengthOf(255)
    validate(CdrPartial::currency).isCurrencyCode(false).hasMaxLengthOf(3)
    tariffs?.forEach { tariff -> tariff.validate() }
    chargingPeriods?.forEach { chargingPeriod -> chargingPeriod.validate() }
    validate(CdrPartial::totalCost).isBigDecimalPositive()
    validate(CdrPartial::totalEnergy).isBigDecimalPositive()
    validate(CdrPartial::totalTime).isBigDecimalPositive()
    validate(CdrPartial::totalParkingTime).isBigDecimalPositive()
    validate(CdrPartial::remark).isPrintableUtf8().hasMaxLengthOf(255)
    // lastUpdated: nothing to validate
}

fun Cdr.validate(): Cdr = validate(this) {
    toPartial().validate()
}

fun ChargingPeriodPartial.validate(): ChargingPeriodPartial = validate(this) {
    // startDateTime nothing to validate
    dimensions?.forEach { dimension -> dimension.validate() }
}

fun ChargingPeriod.validate(): ChargingPeriod = validate(this) {
    toPartial().validate()
}

fun CdrDimensionPartial.validate(): CdrDimensionPartial = validate(this) {
    // type: nothing to validate
    validate(CdrDimensionPartial::volume).isGreaterThanOrEqualTo(BigDecimal.ZERO)
}

fun CdrDimension.validate(): CdrDimension = validate(this) {
    toPartial().validate()
}
