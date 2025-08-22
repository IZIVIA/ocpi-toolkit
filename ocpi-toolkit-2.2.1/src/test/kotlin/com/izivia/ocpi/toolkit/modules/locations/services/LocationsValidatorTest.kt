package com.izivia.ocpi.toolkit.modules.locations.services

import com.izivia.ocpi.toolkit.modules.locations.domain.*
import com.izivia.ocpi.toolkit.modules.types.DisplayText
import com.izivia.ocpi.toolkit.samples.common.*
import org.junit.jupiter.api.DynamicTest
import org.junit.jupiter.api.TestFactory
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

    data class TestCase<T>(
        val name: String,
        val objectProvider: () -> T,
        val shouldSucceed: Boolean,
    )

    private fun <T> goodTC(name: String, objectProvider: () -> T) = TestCase(name, objectProvider, true)
    private fun <T> failTC(name: String, objectProvider: () -> T) = TestCase(name, objectProvider, false)

    private inline fun <reified T> createValidationTests(testCases: List<TestCase<T>>): List<DynamicTest> {
        return testCases.map { testCase ->
            DynamicTest.dynamicTest(testCase.name) {
                val result = expectCatching {
                    when (val obj = testCase.objectProvider()) {
                        is Location -> obj.validate()
                        is Evse -> obj.validate()
                        is Connector -> obj.validate()
                        is GeoLocation -> obj.validate()
                        is EnvironmentalImpact -> obj.validate()
                        is EnergySource -> obj.validate()
                        is EnergyMix -> obj.validate()
                        is ExceptionalPeriod -> obj.validate()
                        is RegularHours -> obj.validate()
                        is Hours -> obj.validate()
                        is Image -> obj.validate()
                        is BusinessDetails -> obj.validate()
                        is AdditionalGeoLocation -> obj.validate()
                        is DisplayText -> obj.validate()
                        is StatusSchedule -> obj.validate()
                        else -> throw IllegalArgumentException("Unsupported type: ${T::class.java}")
                    }
                }
                if (testCase.shouldSucceed) {
                    result.isSuccess()
                } else {
                    result.isFailure()
                }
            }
        }
    }

    @TestFactory
    fun `Location validation tests`() = createValidationTests(
        listOf(
            goodTC("valid location") { validLocation },
            failTC("id too long") { validLocation.copy(id = generateRandomString(37)) },
            failTC("name too long") { validLocation.copy(name = generateRandomString(256)) },
            failTC("address too long") { validLocation.copy(address = generateRandomString(46)) },
            failTC("city too long") { validLocation.copy(city = generateRandomString(46)) },
            failTC("postal code too long") { validLocation.copy(postalCode = generateRandomString(11)) },
            failTC("country too long") { validLocation.copy(country = generateRandomString(4)) },
            failTC("country invalid format") { validLocation.copy(country = "fra") },
            failTC("timezone too long") { validLocation.copy(timeZone = generateRandomString(256)) },
            failTC("timezone invalid format") { validLocation.copy(timeZone = "EuropeOslo") },
            failTC("parking type invalid") { validLocation.copy(parkingType = ParkingType.OTHER) },
            failTC("facilities contains invalid") { validLocation.copy(facilities = listOf(Facility.OTHER)) },
        ),
    )

    @TestFactory
    fun `EVSE validation tests`() = createValidationTests(
        listOf(
            goodTC("valid EVSE") { validEvse },
            goodTC("valid evseId format 1") { validEvse.copy(evseId = "FR*A23*E45B*78C") },
            goodTC("valid evseId format 2") { validEvse.copy(evseId = "fr*a23*e45b*78c") },
            goodTC("valid evseId format 3") { validEvse.copy(evseId = "FRA23E45B78C") },
            goodTC("valid evseId format 4") { validEvse.copy(evseId = "FRA23E45B*78C") },
            goodTC("valid evseId max length") { validEvse.copy(evseId = "FR*A23*E${generateRandomString(31)}") },
            failTC("evseId country too short") { validEvse.copy(evseId = "F*A23*E45B*78C") },
            failTC("evseId party too short") { validEvse.copy(evseId = "FR*A2*E45B*78C") },
            failTC("evseId invalid power outlet type") { validEvse.copy(evseId = "FR*A23*A45B*78C") },
            failTC("evseId too short") { validEvse.copy(evseId = "FR*A23*E") },
            failTC("evseId too long") { validEvse.copy(evseId = "FR*A23*E${generateRandomString(32)}") },
            failTC("uid too long") { validEvse.copy(uid = generateRandomString(40)) },
            failTC("floor level too long") { validEvse.copy(floorLevel = generateRandomString(5)) },
            failTC("physical reference too long") { validEvse.copy(physicalReference = generateRandomString(17)) },
            failTC("capabilities contains invalid") { validEvse.copy(capabilities = listOf(Capability.OTHER)) },
            failTC("parkingRestrictions invalid") {
                validEvse.copy(parkingRestrictions = listOf(ParkingRestriction.OTHER))
            },
        ),
    )

    @TestFactory
    fun `Connector validation tests`() = createValidationTests(
        listOf(
            goodTC("valid connector") { validConnector },
            goodTC("max voltage zero") { validConnector.copy(maxVoltage = 0) },
            goodTC("max amperage zero") { validConnector.copy(maxAmperage = 0) },
            failTC("id too long") { validConnector.copy(id = generateRandomString(37)) },
            failTC("max voltage negative") { validConnector.copy(maxVoltage = -1) },
            failTC("max amperage negative") { validConnector.copy(maxAmperage = -1) },
            failTC("tariff id too long") { validConnector.copy(tariffIds = listOf(generateRandomString(37))) },
            failTC("invalid terms and conditions URL") { validConnector.copy(termsAndConditions = "not an url") },
            failTC("invalid connector type") { validConnector.copy(standard = ConnectorType.OTHER) },
        ),
    )

    @TestFactory
    fun `GeoLocation validation tests`() = createValidationTests(
        listOf(
            goodTC("valid coordinates 1") { GeoLocation("1.123456", "2.123456") },
            goodTC("valid negative latitude") { GeoLocation("-5.123456", "12.123456") },
            goodTC("valid negative longitude") { GeoLocation("1.123456", "-2.123456") },
            goodTC("valid negative both") { GeoLocation("-1.123456", "-2.123456") },
            goodTC("valid longitude at limit") { GeoLocation("-1.123456", "-211.123456") },
            failTC("insufficient latitude precision") { GeoLocation("1.55", "2.15") },
            failTC("longitude too high") { GeoLocation("1.123456", "2111.123456") },
            failTC("latitude too high") { GeoLocation("111.123456", "21.123456") },
            failTC("latitude negative too high") { GeoLocation("-111.123456", "21.123456") },
            failTC("longitude starts with dot") { GeoLocation("1.123456", ".123456") },
            failTC("longitude negative starts with dot") { GeoLocation("1.123456", "-.123456") },
            failTC("longitude too low") { GeoLocation("1.123456", "-1111.123456") },
            failTC("latitude over 100") { GeoLocation("101.123456", "2.123456") },
        ),
    )

    @TestFactory
    fun `EnvironmentalImpact validation tests`() = createValidationTests(
        listOf(
            goodTC("zero amount") {
                EnvironmentalImpact(EnvironmentalImpactCategory.CARBON_DIOXIDE, BigDecimal(0.0))
            },
            goodTC("positive amount") {
                EnvironmentalImpact(EnvironmentalImpactCategory.CARBON_DIOXIDE, BigDecimal(1.0))
            },
            goodTC("larger positive amount") {
                EnvironmentalImpact(EnvironmentalImpactCategory.CARBON_DIOXIDE, BigDecimal(12.0))
            },
            goodTC("decimal amount") {
                EnvironmentalImpact(EnvironmentalImpactCategory.CARBON_DIOXIDE, BigDecimal(12.1234))
            },
            failTC("negative small amount") {
                EnvironmentalImpact(EnvironmentalImpactCategory.CARBON_DIOXIDE, BigDecimal(-0.1))
            },
            failTC("negative large amount") {
                EnvironmentalImpact(EnvironmentalImpactCategory.CARBON_DIOXIDE, BigDecimal(-10))
            },
        ),
    )

    @TestFactory
    fun `EnergySource validation tests`() = createValidationTests(
        listOf(
            goodTC("zero percentage") { EnergySource(EnergySourceCategory.NUCLEAR, BigDecimal(0.0)) },
            goodTC("small percentage") { EnergySource(EnergySourceCategory.NUCLEAR, BigDecimal(1.0)) },
            goodTC("medium percentage") { EnergySource(EnergySourceCategory.NUCLEAR, BigDecimal(12.0)) },
            goodTC("decimal percentage") { EnergySource(EnergySourceCategory.NUCLEAR, BigDecimal(12.1234)) },
            goodTC("100 percentage") { EnergySource(EnergySourceCategory.NUCLEAR, BigDecimal(100.00)) },
            failTC("negative small percentage") {
                EnergySource(EnergySourceCategory.NUCLEAR, BigDecimal(-0.1))
            },
            failTC("negative large percentage") {
                EnergySource(EnergySourceCategory.NUCLEAR, BigDecimal(-10))
            },
            failTC("over 100 percentage") { EnergySource(EnergySourceCategory.NUCLEAR, BigDecimal(100.1)) },
            failTC("over 100 percentage large") {
                EnergySource(EnergySourceCategory.NUCLEAR, BigDecimal(101))
            },
        ),
    )

    @TestFactory
    fun `EnergyMix validation tests`() = createValidationTests(
        listOf(
            goodTC("valid energy mix") { validEnergyMix },
            failTC("supplier name too long") { validEnergyMix.copy(supplierName = generateRandomString(65)) },
            failTC("energy product name too long") {
                validEnergyMix.copy(energyProductName = generateRandomString(65))
            },
        ),
    )

    @TestFactory
    fun `ExceptionalPeriod validation tests`() = createValidationTests(
        listOf(
            goodTC("valid exceptional period") { validExceptionalPeriod },
            goodTC("same begin and end") {
                validExceptionalPeriod.copy(
                    periodBegin = validExceptionalPeriod.periodBegin,
                    periodEnd = validExceptionalPeriod.periodBegin,
                )
            },
            failTC("end before begin") {
                validExceptionalPeriod.copy(
                    periodBegin = validExceptionalPeriod.periodEnd,
                    periodEnd = validExceptionalPeriod.periodBegin,
                )
            },
        ),
    )

    @TestFactory
    fun `RegularHours validation tests`() = createValidationTests(
        listOf(
            goodTC("valid regular hours") { validRegularHours },
            goodTC("same begin and end") {
                validRegularHours.copy(
                    periodBegin = validRegularHours.periodBegin,
                    periodEnd = validRegularHours.periodBegin,
                )
            },
            failTC("end before begin") {
                validRegularHours.copy(
                    periodBegin = validRegularHours.periodEnd,
                    periodEnd = validRegularHours.periodBegin,
                )
            },
            failTC("weekday zero") { validRegularHours.copy(weekday = 0) },
            failTC("weekday eight") { validRegularHours.copy(weekday = 8) },
            failTC("invalid period begin format 1") { validRegularHours.copy(periodBegin = "1:00") },
            failTC("invalid period begin format 2") { validRegularHours.copy(periodBegin = "00:1") },
            failTC("invalid period begin format 3") { validRegularHours.copy(periodBegin = "0010") },
        ),
    )

    @TestFactory
    fun `Hours validation tests`() = createValidationTests(
        listOf(
            goodTC("valid hours") { validHours },
            goodTC("twenty four seven with null regular hours") {
                validHours.copy(regularHours = null, twentyfourseven = true)
            },
            goodTC("empty exceptional lists") {
                validHours.copy(exceptionalOpenings = emptyList(), exceptionalClosings = emptyList())
            },
            failTC("twenty four seven with regular hours") {
                validHours.copy(regularHours = listOf(validRegularHours), twentyfourseven = true)
            },
            failTC("not twenty four seven with null regular hours") {
                validHours.copy(regularHours = null, twentyfourseven = false)
            },
        ),
    )

    @TestFactory
    fun `Image validation tests`() = createValidationTests(
        listOf(
            goodTC("valid image") { validImage },
            failTC("width too large") { validImage.copy(width = 123456) },
            failTC("height too large") { validImage.copy(height = 123456) },
            failTC("invalid type") { validImage.copy(type = "abcde") },
            failTC("invalid url") { validImage.copy(url = "not an url") },
            failTC("invalid thumbnail") { validImage.copy(thumbnail = "not an url") },
        ),
    )

    @TestFactory
    fun `BusinessDetails validation tests`() = createValidationTests(
        listOf(
            goodTC("valid business details") { validBusinessDetails },
            goodTC("valid UTF-8 name") { validBusinessDetails.copy(name = validUtf8String) },
            goodTC("null website") { validBusinessDetails.copy(website = null) },
            goodTC("null logo") { validBusinessDetails.copy(logo = null) },
            failTC("name too long") { validBusinessDetails.copy(name = generateRandomString(101)) },
            failTC("name with non-printable chars") { validBusinessDetails.copy(name = "with non-printable: \n") },
            failTC("invalid website") { validBusinessDetails.copy(website = "not an url") },
        ),
    )

    @TestFactory
    fun `AdditionalGeoLocation validation tests`() = createValidationTests(
        listOf(
            goodTC("valid additional geo location") { validAdditionalGeoLocation },
            goodTC("valid positive longitude") { validAdditionalGeoLocation.copy(longitude = "123.123456") },
            goodTC("valid negative longitude") { validAdditionalGeoLocation.copy(longitude = "-123.123456") },
            goodTC("valid negative latitude") { validAdditionalGeoLocation.copy(latitude = "-13.123456") },
            goodTC("valid positive latitude") { validAdditionalGeoLocation.copy(latitude = "13.123456") },
            failTC("invalid latitude precision") { validAdditionalGeoLocation.copy(latitude = "1.55") },
            failTC("invalid longitude precision") { validAdditionalGeoLocation.copy(longitude = "1.55") },
            failTC("longitude too high") { validAdditionalGeoLocation.copy(longitude = "1111.55") },
            failTC("latitude too high") { validAdditionalGeoLocation.copy(latitude = "111.55") },
        ),
    )

    @TestFactory
    fun `DisplayText validation tests`() = createValidationTests(
        listOf(
            goodTC("valid display text") { validDisplayText },
            failTC("invalid language") { validDisplayText.copy(language = "fra") },
            failTC("text too long") { validDisplayText.copy(text = generateRandomString(513)) },
            failTC("text contains HTML") { validDisplayText.copy(text = "<h>no html!") },
        ),
    )

    @TestFactory
    fun `StatusSchedule validation tests`() = createValidationTests(
        listOf(
            goodTC("valid status schedule") { validStatusSchedule },
            goodTC("same begin and end") {
                validStatusSchedule.copy(
                    periodBegin = validStatusSchedule.periodBegin,
                    periodEnd = validStatusSchedule.periodBegin,
                )
            },
            goodTC("null period end") { validStatusSchedule.copy(periodEnd = null) },
            failTC("end before begin") {
                validStatusSchedule.copy(
                    periodBegin = validStatusSchedule.periodEnd!!,
                    periodEnd = validStatusSchedule.periodBegin,
                )
            },
        ),
    )
}
