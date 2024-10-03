package com.izivia.ocpi.toolkit.modules.cdr.services

import com.izivia.ocpi.toolkit.common.validation.*
import com.izivia.ocpi.toolkit.modules.cdr.domain.*
import com.izivia.ocpi.toolkit.modules.locations.services.validate
import com.izivia.ocpi.toolkit.modules.tariff.services.validate
import org.valiktor.functions.isGreaterThanOrEqualTo
import org.valiktor.validate
import java.math.BigDecimal

fun CdrPartial.validate(): CdrPartial =
    validate(this) {
        validate(CdrPartial::countryCode).isCountryCode(caseSensitive = false, alpha2 = true)
        validate(CdrPartial::partyId).isPrintableAscii().hasMaxLengthOf(3)
        validate(CdrPartial::id).isPrintableAscii().hasMaxLengthOf(39)
        // startDateTime nothing to validate
        // endDateTime nothing to validate
        validate(CdrPartial::sessionId).isPrintableAscii().hasMaxLengthOf(36)
        cdrToken?.validate()
        // authMethod: nothing to validate
        validate(CdrPartial::authorizationReference).isPrintableAscii().hasMaxLengthOf(36)
        cdrLocation?.validate()
        validate(CdrPartial::meterId).isPrintableAscii().hasMaxLengthOf(255)
        validate(CdrPartial::currency).isCurrencyCode(false).hasMaxLengthOf(3)
        tariffs?.forEach { tariff -> tariff.validate() }
        chargingPeriods?.forEach { chargingPeriod -> chargingPeriod.validate() }
        signedData?.validate()
        totalCost?.validate()
        totalFixedCost?.validate()
        validate(CdrPartial::totalEnergy).isBigDecimalPositive()
        totalEnergyCost?.validate()
        validate(CdrPartial::totalTime).isBigDecimalPositive()
        totalTimeCost?.validate()
        validate(CdrPartial::totalParkingTime).isBigDecimalPositive()
        totalParkingCost?.validate()
        totalReservationCost?.validate()
        validate(CdrPartial::remark).isPrintableUtf8().hasMaxLengthOf(255)
        validate(CdrPartial::invoiceReferenceId).isPrintableAscii().hasMaxLengthOf(39)
        // credit: nothing to validate
        validate(CdrPartial::creditReferenceId).isPrintableAscii().hasMaxLengthOf(39)
        // homeChargingCompensation: nothing to validate
        // lastUpdated: nothing to validate
    }

private fun CdrLocation.validate() = validate(this) {
    validate(CdrLocation::id).isPrintableAscii().hasMaxLengthOf(36)
    validate(CdrLocation::name).isPrintableUtf8().hasMaxLengthOf(255)
    validate(CdrLocation::address).isPrintableUtf8().hasMaxLengthOf(45)
    validate(CdrLocation::city).isPrintableUtf8().hasMaxLengthOf(45)
    validate(CdrLocation::postalCode).isPrintableUtf8().hasMaxLengthOf(10)
    validate(CdrLocation::state).isPrintableUtf8().hasMaxLengthOf(20)
    validate(CdrLocation::country).isCountryCode(caseSensitive = true, alpha2 = false)
    coordinates.validate()
    validate(CdrLocation::evseUid).isPrintableAscii().hasMaxLengthOf(36)
    validate(CdrLocation::evseId).isPrintableAscii().hasMaxLengthOf(48)
    validate(CdrLocation::connectorId).isPrintableAscii().hasMaxLengthOf(36)
    // connectorStandard: nothing to validate
    // connectorFormat: nothing to validate
    // connectorPowerType: nothing to validate
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

fun SignedDataPartial.validate() = validate(this) {
    validate(SignedDataPartial::encodingMethod).isPrintableAscii().hasMaxLengthOf(36)
    validate(SignedDataPartial::encodingMethodVersion).isIntPositive()
    validate(SignedDataPartial::publicKey).isPrintableAscii().hasMaxLengthOf(512)
    signedValues?.forEach { signedValue -> signedValue.validate() }
    validate(SignedDataPartial::url).isPrintableAscii().hasMaxLengthOf(512)
}

fun SignedValuePartial.validate() = validate(this) {
    validate(SignedValuePartial::nature).isPrintableAscii().hasMaxLengthOf(32)
    validate(SignedValuePartial::plainData).isPrintableAscii().hasMaxLengthOf(512)
    validate(SignedValuePartial::signedData).isPrintableAscii().hasMaxLengthOf(5000)
}

fun Cdr.validate(): Cdr = validate(this) {
    toPartial().validate()
}
