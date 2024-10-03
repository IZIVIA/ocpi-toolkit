package com.izivia.ocpi.toolkit.modules.locations.services

import com.izivia.ocpi.toolkit.modules.locations.domain.*
import com.izivia.ocpi.toolkit.samples.common.*
import org.junit.jupiter.api.Test
import strikt.api.expectCatching
import strikt.assertions.isFailure
import strikt.assertions.isSuccess
import java.math.BigDecimal
import kotlin.random.Random

class LocationsValidatorTest {

    private fun generateRandomString(length: Int): String = (('a'..'z') + ('A'..'Z'))
        .let { chars ->
            (0 until length)
                .map { chars[Random.nextInt(chars.size)] }
                .joinToString("")
        }

    @Test
    fun `Location validator`() {
        expectCatching {
            validLocation.validate()
        }.isSuccess()

        expectCatching {
            validLocation.copy(id = generateRandomString(37)).validate()
        }.isFailure()

        expectCatching {
            validLocation.copy(name = generateRandomString(256)).validate()
        }.isFailure()

        expectCatching {
            validLocation.copy(address = generateRandomString(46)).validate()
        }.isFailure()

        expectCatching {
            validLocation.copy(city = generateRandomString(46)).validate()
        }.isFailure()

        expectCatching {
            validLocation.copy(postalCode = generateRandomString(11)).validate()
        }.isFailure()

        expectCatching {
            validLocation.copy(country = generateRandomString(4)).validate()
        }.isFailure()

        expectCatching {
            validLocation.copy(country = "fra").validate()
        }.isFailure()

        expectCatching {
            validLocation.copy(timeZone = generateRandomString(256)).validate()
        }.isFailure()

        expectCatching {
            validLocation.copy(timeZone = "EuropeOslo").validate()
        }.isFailure()
    }

    @Test
    fun `Evse validator`() {
        expectCatching {
            validEvse.validate()
        }.isSuccess()

        expectCatching {
            validEvse.copy(evseId = "FR*A23*E45B*78C").validate()
        }.isSuccess()

        expectCatching {
            validEvse.copy(evseId = "fr*a23*e45b*78c").validate()
        }.isSuccess()

        expectCatching {
            validEvse.copy(evseId = "FRA23E45B78C").validate()
        }.isSuccess()

        expectCatching {
            validEvse.copy(evseId = "FRA23E45B*78C").validate()
        }.isSuccess()

        expectCatching {
            validEvse.copy(evseId = "FR*A23*E${generateRandomString(31)}").validate()
        }.isSuccess()

        expectCatching {
            validEvse.copy(evseId = "F*A23*E45B*78C").validate()
        }.isFailure()

        expectCatching {
            validEvse.copy(evseId = "FR*A2*E45B*78C").validate()
        }.isFailure()

        expectCatching {
            validEvse.copy(evseId = "FR*A23*A45B*78C").validate()
        }.isFailure()

        expectCatching {
            validEvse.copy(evseId = "FR*A23*E").validate()
        }.isFailure()

        expectCatching {
            validEvse.copy(evseId = "FR*A23*E${generateRandomString(32)}").validate()
        }.isFailure()

        expectCatching {
            validEvse.copy(uid = generateRandomString(40)).validate()
        }.isFailure()

        expectCatching {
            validEvse.copy(floorLevel = generateRandomString(5)).validate()
        }.isFailure()

        expectCatching {
            validEvse.copy(physicalReference = generateRandomString(17)).validate()
        }.isFailure()
    }

    @Test
    fun `Connector validator`() {
        expectCatching {
            validConnector.validate()
            validConnector.copy(maxVoltage = 0).validate()
            validConnector.copy(maxAmperage = 0).validate()
        }.isSuccess()

        expectCatching {
            validConnector.copy(id = generateRandomString(37)).validate()
        }.isFailure()

        expectCatching {
            validConnector.copy(maxVoltage = -1).validate()
        }.isFailure()

        expectCatching {
            validConnector.copy(maxAmperage = -1).validate()
        }.isFailure()

        expectCatching {
            validConnector.copy(tariffIds = listOf(generateRandomString(37))).validate()
        }.isFailure()

        expectCatching {
            validConnector.copy(termsAndConditions = "not an url").validate()
        }.isFailure()
    }

    @Test
    fun `GeoLocation validator`() {
        expectCatching {
            GeoLocation(
                "1.55",
                "2.15",
            ).validate()
        }.isFailure()

        expectCatching {
            GeoLocation(
                "1.123456",
                "2111.123456",
            ).validate()
        }.isFailure()

        expectCatching {
            GeoLocation(
                "111.123456",
                "21.123456",
            ).validate()
        }.isFailure()

        expectCatching {
            GeoLocation(
                "-111.123456",
                "21.123456",
            ).validate()
        }.isFailure()

        expectCatching {
            GeoLocation(
                "1.123456",
                ".123456",
            ).validate()
        }.isFailure()

        expectCatching {
            GeoLocation(
                "1.123456",
                "-.123456",
            ).validate()
        }.isFailure()

        expectCatching {
            GeoLocation(
                "1.123456",
                "-1111.123456",
            ).validate()
        }.isFailure()

        expectCatching {
            GeoLocation(
                "101.123456",
                "2.123456",
            ).validate()
        }.isFailure()

        expectCatching {
            validLocation.validate()

            GeoLocation(
                "1.123456",
                "2.123456",
            ).validate()

            GeoLocation(
                "-5.123456",
                "12.123456",
            ).validate()

            GeoLocation(
                "1.123456",
                "-2.123456",
            ).validate()

            GeoLocation(
                "-1.123456",
                "-2.123456",
            ).validate()

            GeoLocation(
                "-1.123456",
                "-211.123456",
            ).validate()
        }.isSuccess()
    }

    @Test
    fun `EnvironmentalImpact validator`() {
        expectCatching {
            EnvironmentalImpact(
                category = EnvironmentalImpactCategory.CARBON_DIOXIDE,
                amount = BigDecimal(0.0),
            ).validate()

            EnvironmentalImpact(
                category = EnvironmentalImpactCategory.CARBON_DIOXIDE,
                amount = BigDecimal(1.0),
            ).validate()

            EnvironmentalImpact(
                category = EnvironmentalImpactCategory.CARBON_DIOXIDE,
                amount = BigDecimal(12.0),
            ).validate()

            EnvironmentalImpact(
                category = EnvironmentalImpactCategory.CARBON_DIOXIDE,
                amount = BigDecimal(12.1234),
            ).validate()
        }.isSuccess()

        expectCatching {
            EnvironmentalImpact(
                category = EnvironmentalImpactCategory.CARBON_DIOXIDE,
                amount = BigDecimal(-0.1),
            ).validate()
        }.isFailure()

        expectCatching {
            EnvironmentalImpact(
                category = EnvironmentalImpactCategory.CARBON_DIOXIDE,
                amount = BigDecimal(-10),
            ).validate()
        }.isFailure()
    }

    @Test
    fun `EnergySource validator`() {
        expectCatching {
            EnergySource(
                source = EnergySourceCategory.NUCLEAR,
                percentage = BigDecimal(0.0),
            ).validate()

            EnergySource(
                source = EnergySourceCategory.NUCLEAR,
                percentage = BigDecimal(1.0),
            ).validate()

            EnergySource(
                source = EnergySourceCategory.NUCLEAR,
                percentage = BigDecimal(12.0),
            ).validate()

            EnergySource(
                source = EnergySourceCategory.NUCLEAR,
                percentage = BigDecimal(12.1234),
            ).validate()

            EnergySource(
                source = EnergySourceCategory.NUCLEAR,
                percentage = BigDecimal(100.00),
            ).validate()
        }.isSuccess()

        expectCatching {
            EnergySource(
                source = EnergySourceCategory.NUCLEAR,
                percentage = BigDecimal(-0.1),
            ).validate()
        }.isFailure()

        expectCatching {
            EnergySource(
                source = EnergySourceCategory.NUCLEAR,
                percentage = BigDecimal(-10),
            ).validate()
        }.isFailure()

        expectCatching {
            EnergySource(
                source = EnergySourceCategory.NUCLEAR,
                percentage = BigDecimal(100.1),
            ).validate()
        }.isFailure()

        expectCatching {
            EnergySource(
                source = EnergySourceCategory.NUCLEAR,
                percentage = BigDecimal(101),
            ).validate()
        }.isFailure()
    }

    @Test
    fun `EnergyMix validator`() {
        expectCatching {
            validEnergyMix.validate()
        }.isSuccess()

        expectCatching {
            validEnergyMix.copy(supplierName = generateRandomString(65)).validate()
        }.isFailure()

        expectCatching {
            validEnergyMix.copy(energyProductName = generateRandomString(65)).validate()
        }.isFailure()
    }

    @Test
    fun `ExceptionalPeriod validator`() {
        expectCatching {
            validExceptionalPeriod.validate()

            validExceptionalPeriod.copy(
                periodBegin = validExceptionalPeriod.periodBegin,
                periodEnd = validExceptionalPeriod.periodBegin,
            ).validate()
        }.isSuccess()

        expectCatching {
            validExceptionalPeriod.copy(
                periodBegin = validExceptionalPeriod.periodEnd,
                periodEnd = validExceptionalPeriod.periodBegin,
            ).validate()
        }.isFailure()
    }

    @Test
    fun `RegularHours validator`() {
        expectCatching {
            validRegularHours.validate()

            validRegularHours.copy(
                periodBegin = validRegularHours.periodBegin,
                periodEnd = validRegularHours.periodBegin,
            ).validate()
        }.isSuccess()

        expectCatching {
            validRegularHours.copy(
                periodBegin = validRegularHours.periodEnd,
                periodEnd = validRegularHours.periodBegin,
            ).validate()
        }.isFailure()

        expectCatching {
            validRegularHours.copy(
                weekday = 0,
            ).validate()
        }.isFailure()

        expectCatching {
            validRegularHours.copy(
                weekday = 8,
            ).validate()
        }.isFailure()

        expectCatching {
            validRegularHours.copy(
                periodBegin = "1:00",
            ).validate()
        }.isFailure()

        expectCatching {
            validRegularHours.copy(
                periodBegin = "00:1",
            ).validate()
        }.isFailure()

        expectCatching {
            validRegularHours.copy(
                periodBegin = "0010",
            ).validate()
        }.isFailure()
    }

    @Test
    fun `Hours validator`() {
        expectCatching {
            validHours.validate()

            validHours.copy(
                regularHours = null,
                twentyfourseven = true,
            ).validate()

            validHours.copy(
                exceptionalOpenings = emptyList(),
                exceptionalClosings = emptyList(),
            ).validate()
        }.isSuccess()

        expectCatching {
            validHours.copy(
                regularHours = listOf(validRegularHours),
                twentyfourseven = true,
            ).toPartial().validate()
        }.isFailure()

        expectCatching {
            validHours.copy(
                regularHours = listOf(validRegularHours),
                twentyfourseven = true,
            ).validate()
        }.isFailure()

        expectCatching {
            validHours.copy(
                regularHours = null,
                twentyfourseven = false,
            ).validate()
        }.isFailure()
    }

    @Test
    fun `Image validator`() {
        expectCatching {
            validImage.validate()
        }.isSuccess()

        expectCatching {
            validImage.copy(width = 123456).validate()
        }.isFailure()

        expectCatching {
            validImage.copy(height = 123456).validate()
        }.isFailure()

        expectCatching {
            validImage.copy(type = "abcde").validate()
        }.isFailure()

        expectCatching {
            validImage.copy(url = "not an url").validate()
        }.isFailure()

        expectCatching {
            validImage.copy(thumbnail = "not an url").validate()
        }.isFailure()
    }

    @Test
    fun `BusinessDetails validator`() {
        expectCatching {
            validBusinessDetails.validate()
            validBusinessDetails.copy(name = validUtf8String).validate()
            validBusinessDetails.copy(website = null).validate()
            validBusinessDetails.copy(logo = null).validate()
        }.isSuccess()

        expectCatching {
            validBusinessDetails.copy(name = generateRandomString(101)).validate()
        }.isFailure()

        expectCatching {
            validBusinessDetails.copy(name = "with non-printable: \n").validate()
        }.isFailure()

        expectCatching {
            validBusinessDetails.copy(website = "not an url").validate()
        }.isFailure()
    }

    @Test
    fun `AdditionalGeoLocation validator`() {
        expectCatching {
            validAdditionalGeoLocation.validate()
            validAdditionalGeoLocation.copy(longitude = "123.123456").validate()
            validAdditionalGeoLocation.copy(longitude = "-123.123456").validate()
            validAdditionalGeoLocation.copy(latitude = "-13.123456").validate()
            validAdditionalGeoLocation.copy(latitude = "13.123456").validate()
        }.isSuccess()

        expectCatching {
            validAdditionalGeoLocation.copy(latitude = "1.55").validate()
        }.isFailure()

        expectCatching {
            validAdditionalGeoLocation.copy(longitude = "1.55").validate()
        }.isFailure()

        expectCatching {
            validAdditionalGeoLocation.copy(longitude = "1111.55").validate()
        }.isFailure()

        expectCatching {
            validAdditionalGeoLocation.copy(latitude = "111.55").validate()
        }.isFailure()
    }

    @Test
    fun `DisplayText validator`() {
        expectCatching {
            validDisplayText.validate()
        }.isSuccess()

        expectCatching {
            validDisplayText.copy(language = "fra").validate()
        }.isFailure()

        expectCatching {
            validDisplayText.copy(text = generateRandomString(513)).validate()
        }.isFailure()

        expectCatching {
            validDisplayText.copy(text = "<h>no html!").validate()
        }.isFailure()
    }

    @Test
    fun `StatusSchedule validator`() {
        expectCatching {
            validStatusSchedule.validate()
            validStatusSchedule.copy(
                periodBegin = validStatusSchedule.periodBegin,
                periodEnd = validStatusSchedule.periodBegin,
            ).validate()
            validStatusSchedule.copy(periodEnd = null).validate()
        }.isSuccess()

        expectCatching {
            validStatusSchedule.copy(
                periodBegin = validStatusSchedule.periodEnd!!,
                periodEnd = validStatusSchedule.periodBegin,
            ).validate()
        }.isFailure()
    }
}
