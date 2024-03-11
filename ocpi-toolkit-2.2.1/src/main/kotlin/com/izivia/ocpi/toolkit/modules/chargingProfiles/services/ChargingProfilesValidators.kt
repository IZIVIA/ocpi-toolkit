package com.izivia.ocpi.toolkit.modules.chargingProfiles.services

import com.izivia.ocpi.toolkit.common.validation.hasMaxLengthOf
import com.izivia.ocpi.toolkit.modules.chargingProfiles.domain.*
import org.valiktor.functions.isGreaterThan
import org.valiktor.functions.isGreaterThanOrEqualTo
import org.valiktor.validate

fun ActiveChargingProfileResult.validate(): ActiveChargingProfileResult = validate(this) {
    // result: nothing to validate
    profile?.validate()
}

fun ActiveChargingProfile.validate(): ActiveChargingProfile = validate(this) {
    // startDateTime: nothing to validate
    chargingProfile.validate()
}

fun ChargingProfile.validate(): ChargingProfile = validate(this) {
    // startDateTime: nothing to validate
    duration?.also { validate(ChargingProfile::duration).isGreaterThan(0) }
    // chargingRateUnit: nothing to validate
    minChargingRate?.also { validate(ChargingProfile::minChargingRate).isGreaterThanOrEqualTo(0.toBigDecimal()) }
    chargingProfilePeriod?.forEach { it.validate() }
}

fun ChargingProfilePeriod.validate(): ChargingProfilePeriod = validate(this) {
    validate(ChargingProfilePeriod::startPeriod).isGreaterThanOrEqualTo(0)
    validate(ChargingProfilePeriod::limit).isGreaterThanOrEqualTo(0.toBigDecimal())
}

fun ChargingProfileResult.validate(): ChargingProfileResult = validate(this) {
    // result: nothing to validate
}

fun ClearProfileResult.validate(): ClearProfileResult = validate(this) {
    // result: nothing to validate
}

fun SetChargingProfile.validate(): SetChargingProfile = validate(this) {
    chargingProfile.validate()
    validate(SetChargingProfile::responseUrl).hasMaxLengthOf(255)
}
