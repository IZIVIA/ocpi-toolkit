package com.izivia.ocpi.toolkit.modules.locations.services

import com.izivia.ocpi.toolkit.common.validation.*
import com.izivia.ocpi.toolkit.modules.locations.domain.*
import com.izivia.ocpi.toolkit.modules.types.DisplayText
import com.izivia.ocpi.toolkit.modules.types.toPartial
import org.valiktor.DefaultConstraintViolation
import org.valiktor.constraints.Greater
import org.valiktor.functions.isGreaterThanOrEqualTo
import org.valiktor.functions.isLessThanOrEqualTo
import org.valiktor.functions.isNotEmpty
import org.valiktor.validate
import java.math.BigDecimal

fun LocationPartial.validate(): LocationPartial = validate(this) {
    validate(LocationPartial::countryCode).isCountryCode(caseSensitive = false, alpha2 = true)
    validate(LocationPartial::partyId).isPrintableAscii().hasMaxLengthOf(3)
    validate(LocationPartial::id).isPrintableAscii().hasMaxLengthOf(36)
    // publish: nothing to validate
    // publishAllowedTo: nothing to validate
    validate(LocationPartial::name).isPrintableUtf8().hasMaxLengthOf(255)
    validate(LocationPartial::address).isPrintableUtf8().hasMaxLengthOf(45)
    validate(LocationPartial::city).isPrintableUtf8().hasMaxLengthOf(45)
    validate(LocationPartial::postalCode).isPrintableUtf8().hasMaxLengthOf(10)
    validate(LocationPartial::state).isPrintableUtf8().hasMaxLengthOf(20)
    validate(LocationPartial::country).isCountryCode(caseSensitive = true, alpha2 = false)
    coordinates?.validate()
    relatedLocations?.forEach { it.validate() }
    // parkingType: nothing to validate
    evses?.forEach { it.validate() }
    directions?.forEach { it.validate() }
    operator?.validate()
    suboperator?.validate()
    owner?.validate()
    // facilities: nothing to validate
    validate(LocationPartial::timeZone).isTimeZone()
    openingTimes?.validate()
    // chargingWhenClosed: nothing to validate
    images?.forEach { it.validate() }
    energyMix?.validate()
    // lastUpdated: nothing to validate
}

fun EnvironmentalImpactPartial.validate(): EnvironmentalImpactPartial = validate(this) {
    // source: nothing to validate
    validate(EnvironmentalImpactPartial::amount)
        .isGreaterThanOrEqualTo(BigDecimal.valueOf(0))
}

fun EnergySourcePartial.validate(): EnergySourcePartial = validate(this) {
    // source: nothing to validate
    validate(EnergySourcePartial::percentage)
        .isGreaterThanOrEqualTo(BigDecimal.valueOf(0))
        .isLessThanOrEqualTo(BigDecimal.valueOf(100))
}

fun EnergyMixPartial.validate(): EnergyMixPartial = validate(this) {
    // isGreenEnergy: nothing to validate
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
    // twentyfourseven: nothing to validate
    exceptionalOpenings?.forEach { it.validate() }
    exceptionalClosings?.forEach { it.validate() }
}

fun ImagePartial.validate(): ImagePartial = validate(this) {
    validate(ImagePartial::url).isUrl()
    validate(ImagePartial::thumbnail).isUrl()
    // category: nothing to validate
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
    // status: nothing to validate
    statusSchedule?.forEach { it.validate() }
    // capabilities: nothing to validate
    validate(EvsePartial::connectors).isNotEmpty()
    connectors?.forEach { it.validate() }
    validate(EvsePartial::floorLevel).isPrintableUtf8().hasMaxLengthOf(4)
    coordinates?.validate()
    validate(EvsePartial::physicalReference).isPrintableUtf8().hasMaxLengthOf(16)
    directions?.forEach { it.validate() }
    // parkingRestrictions: nothing to validate
    images?.forEach { it.validate() }
    // lastUpdated: nothing to validate
}

fun ConnectorPartial.validate(): ConnectorPartial = validate(this) {
    validate(ConnectorPartial::id).isPrintableAscii().hasMaxLengthOf(36)
    // standard: nothing to validate
    // format: nothing to validate
    // powerType: nothing to validate
    validate(ConnectorPartial::maxVoltage).isGreaterThanOrEqualTo(0)
    validate(ConnectorPartial::maxAmperage).isGreaterThanOrEqualTo(0)
    validate(ConnectorPartial::maxElectricPower).isGreaterThanOrEqualTo(0)
    tariffIds?.forEach { tariffId ->
        if (tariffId.length > 36) {
            constraintViolations.add(
                DefaultConstraintViolation(
                    property = "tariffIds",
                    constraint = MaxLengthContraint(36),
                ),
            )
        }
        if (!tariffId.isPrintableAscii()) {
            constraintViolations.add(
                DefaultConstraintViolation(
                    property = "tariffIds",
                    constraint = PrintableAsciiConstraint(),
                ),
            )
        }
    }
    validate(ConnectorPartial::termsAndConditions).isUrl()
    // lastUpdated: nothing to validate
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
