package com.izivia.ocpi.toolkit211.modules.cdr.services

import com.izivia.ocpi.toolkit211.common.validation.*
import com.izivia.ocpi.toolkit211.modules.cdr.domain.*
import com.izivia.ocpi.toolkit211.modules.tariff.services.validate
import com.izivia.ocpi.toolkit211.modules.types.Price
import com.izivia.ocpi.toolkit211.modules.types.toPartial
import org.valiktor.functions.isGreaterThanOrEqualTo
import org.valiktor.validate
import java.math.BigDecimal

fun CdrPartial.validate(): CdrPartial = validate(this) {
    validate(CdrPartial::id).isPrintableAscii().hasMaxLengthOf(39)
    // startDateTime nothing to validate
    // endDateTime nothing to validate
    validate(CdrPartial::authId).isPrintableAscii().hasMaxLengthOf(36)
    // authMethod: nothing to validate
    validate(CdrPartial::locationId).isPrintableAscii().hasMaxLengthOf(36)
    validate(CdrPartial::evseId).isPrintableAscii().hasMaxLengthOf(48)
    validate(CdrPartial::connectorId).isPrintableAscii().hasMaxLengthOf(36)
    validate(CdrPartial::meterId).isPrintableAscii().hasMaxLengthOf(255)
    validate(CdrPartial::currency).isCurrencyCode(false).hasMaxLengthOf(3)
    tariffs?.forEach { tariff -> tariff.validate() }
    chargingPeriods?.forEach { chargingPeriod -> chargingPeriod.validate() }
    totalCost?.validate()
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
    validate(ChargingPeriodPartial::tariffId).isPrintableAscii().hasMaxLengthOf(36)
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

fun Price.validate(): Price = validate(this) {
    toPartial().validate()
}
