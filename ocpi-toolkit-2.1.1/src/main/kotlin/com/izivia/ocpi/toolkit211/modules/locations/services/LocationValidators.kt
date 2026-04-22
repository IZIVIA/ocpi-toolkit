package com.izivia.ocpi.toolkit211.modules.locations.services

import com.izivia.ocpi.toolkit211.common.validation.*
import com.izivia.ocpi.toolkit211.modules.locations.domain.*
import com.izivia.ocpi.toolkit211.modules.types.DisplayText
import com.izivia.ocpi.toolkit211.modules.types.toPartial
import org.valiktor.DefaultConstraintViolation
import org.valiktor.constraints.Greater
import org.valiktor.functions.*
import org.valiktor.validate
import java.math.BigDecimal

fun LocationPartial.validate(): LocationPartial = validate(this) {
    validate(LocationPartial::id).isPrintableAscii().hasMaxLengthOf(39)
    validate(LocationPartial::name).isPrintableUtf8().hasMaxLengthOf(255)
    validate(LocationPartial::address).isPrintableUtf8().hasMaxLengthOf(45)
    validate(LocationPartial::city).isPrintableUtf8().hasMaxLengthOf(45)
    validate(LocationPartial::postalCode).isPrintableUtf8().hasMaxLengthOf(10)
    validate(LocationPartial::country).isCountryCode(caseSensitive = true, alpha2 = false)
    coordinates?.validate()
    relatedLocations?.forEach { it.validate() }
    evses?.forEach { it.validate() }
    directions?.forEach { it.validate() }
    operator?.validate()
    suboperator?.validate()
    owner?.validate()
    validate(LocationPartial::facilities).doesNotContain(Facility.OTHER)
    validate(LocationPartial::timeZone).isTimeZone()
    openingTimes?.validate()
    images?.forEach { it.validate() }
    energyMix?.validate()
}

fun EnvironmentalImpactPartial.validate(): EnvironmentalImpactPartial = validate(this) {
    validate(EnvironmentalImpactPartial::amount)
        .isGreaterThanOrEqualTo(BigDecimal.valueOf(0))
}

fun EnergySourcePartial.validate(): EnergySourcePartial = validate(this) {
    validate(EnergySourcePartial::percentage)
        .isGreaterThanOrEqualTo(BigDecimal.valueOf(0))
        .isLessThanOrEqualTo(BigDecimal.valueOf(100))
}

fun EnergyMixPartial.validate(): EnergyMixPartial = validate(this) {
    energySources?.forEach { it.validate() }
    environImpact?.forEach { it.validate() }
    validate(EnergyMixPartial::supplierName).isPrintableUtf8().hasMaxLengthOf(64)
    validate(EnergyMixPartial::energyProductName).isPrintableUtf8().hasMaxLengthOf(64)
}

fun ExceptionalPeriodPartial.validate(): ExceptionalPeriodPartial = validate(this) {
    if (it.periodEnd != null) {
        validate(ExceptionalPeriodPartial::periodBegin).isLessThanOrEqualTo(it.periodEnd)
    }
}

fun RegularHoursPartial.validate(): RegularHoursPartial = validate(this) { regularHours ->
    validate(RegularHoursPartial::weekday).isGreaterThanOrEqualTo(1).isLessThanOrEqualTo(7)
    validate(RegularHoursPartial::periodBegin).isTime()
    validate(RegularHoursPartial::periodEnd).isTime()

    val beginInMinutes = regularHours.periodBegin?.split(":")
        ?.mapIndexed { index, time -> time.toInt() * (1 - index) * 60 }
        ?.sum()
    val endInMinutes = regularHours.periodEnd?.split(":")
        ?.mapIndexed { index, time -> time.toInt() * (1 - index) * 60 }
        ?.sum()

    if (beginInMinutes != null && endInMinutes != null && beginInMinutes > endInMinutes) {
        constraintViolations.add(
            DefaultConstraintViolation(
                property = "periodBegin ($periodBegin) is after periodEnd ($periodEnd)",
                constraint = Greater(endInMinutes),
            ),
        )
    }
}

fun HoursPartial.validate(): HoursPartial = validate(this) { hours ->
    if (hours.regularHours == null && hours.twentyfourseven == false) {
        constraintViolations.add(
            DefaultConstraintViolation(
                property = "regularHours is not set whereas twentyFourSeven is false",
                constraint = RegularHoursSetWhenNotTwentyFourSevenConstraint(),
            ),
        )
    } else if (hours.regularHours != null && hours.twentyfourseven == true) {
        constraintViolations.add(
            DefaultConstraintViolation(
                property = "twentyfourseven is set to true whereas regularHours are set",
                constraint = RegularHoursSetAtTheSameTimeAsTwentyFourSevenConstraint(),
            ),
        )
    }

    regularHours?.forEach { it.validate() }
    exceptionalOpenings?.forEach { it.validate() }
    exceptionalClosings?.forEach { it.validate() }
}

fun ImagePartial.validate(): ImagePartial = validate(this) {
    validate(ImagePartial::url).isUrl()
    validate(ImagePartial::thumbnail).isUrl()
    validate(ImagePartial::type).isPrintableAscii().hasMaxLengthOf(4)
    validate(ImagePartial::width).isLessThanOrEqualTo(99999)
    validate(ImagePartial::height).isLessThanOrEqualTo(99999)
}

fun BusinessDetailsPartial.validate(): BusinessDetailsPartial = validate(this) {
    validate(BusinessDetailsPartial::name).isPrintableUtf8().hasMaxLengthOf(100)
    validate(BusinessDetailsPartial::website).isUrl()
    logo?.validate()
}

fun GeoLocationPartial.validate(): GeoLocationPartial = validate(this) {
    validate(GeoLocationPartial::latitude).isLatitude()
    validate(GeoLocationPartial::longitude).isLongitude()
}

fun AdditionalGeoLocationPartial.validate(): AdditionalGeoLocationPartial = validate(this) {
    validate(AdditionalGeoLocationPartial::latitude).isLatitude()
    validate(AdditionalGeoLocationPartial::longitude).isLongitude()
    name?.validate()
}

fun StatusSchedulePartial.validate(): StatusSchedulePartial = validate(this) {
    if (it.periodEnd != null) {
        validate(StatusSchedulePartial::periodBegin).isLessThanOrEqualTo(it.periodEnd)
    }
}

fun EvsePartial.validate(): EvsePartial = validate(this) {
    validate(EvsePartial::uid).isPrintableAscii().hasMaxLengthOf(39)
    validate(EvsePartial::evseId).isEvseId()
    statusSchedule?.forEach { it.validate() }
    validate(EvsePartial::capabilities).doesNotContain(Capability.OTHER)
    validate(EvsePartial::connectors).isNotEmpty()
    connectors?.forEach { it.validate() }
    validate(EvsePartial::floorLevel).isPrintableUtf8().hasMaxLengthOf(4)
    coordinates?.validate()
    validate(EvsePartial::physicalReference).isPrintableUtf8().hasMaxLengthOf(16)
    directions?.forEach { it.validate() }
    validate(EvsePartial::parkingRestrictions).doesNotContain(ParkingRestriction.OTHER)
    images?.forEach { it.validate() }
}

fun ConnectorPartial.validate(): ConnectorPartial = validate(this) {
    validate(ConnectorPartial::id).isPrintableAscii().hasMaxLengthOf(36)
    validate(ConnectorPartial::standard).isNotEqualTo(ConnectorType.OTHER)
    validate(ConnectorPartial::maxVoltage).isGreaterThanOrEqualTo(0)
    validate(ConnectorPartial::maxAmperage).isGreaterThanOrEqualTo(0)
    validate(ConnectorPartial::maxElectricPower).isGreaterThanOrEqualTo(0)
    validate(ConnectorPartial::tariffId).isPrintableAscii().hasMaxLengthOf(36)
    validate(ConnectorPartial::termsAndConditions).isUrl()
}

fun Location.validate(): Location = validate(this) {
    toPartial().validate()
}

fun EnvironmentalImpact.validate(): EnvironmentalImpact = validate(this) {
    toPartial().validate()
}

fun EnergySource.validate(): EnergySource = validate(this) {
    toPartial().validate()
}

fun EnergyMix.validate(): EnergyMix = validate(this) {
    toPartial().validate()
}

fun ExceptionalPeriod.validate(): ExceptionalPeriod = validate(this) {
    toPartial().validate()
}

fun RegularHours.validate(): RegularHours = validate(this) {
    toPartial().validate()
}

fun Hours.validate(): Hours = validate(this) {
    toPartial().validate()
}

fun Image.validate(): Image = validate(this) {
    toPartial().validate()
}

fun BusinessDetails.validate(): BusinessDetails = validate(this) {
    toPartial().validate()
}

fun GeoLocation.validate(): GeoLocation = validate(this) {
    toPartial().validate()
}

fun AdditionalGeoLocation.validate(): AdditionalGeoLocation = validate(this) {
    toPartial().validate()
}

fun DisplayText.validate(): DisplayText = validate(this) {
    toPartial().validate()
}

fun StatusSchedule.validate(): StatusSchedule = validate(this) {
    toPartial().validate()
}

fun Evse.validate(): Evse = validate(this) {
    toPartial().validate()
}

fun Connector.validate(): Connector = validate(this) {
    toPartial().validate()
}
