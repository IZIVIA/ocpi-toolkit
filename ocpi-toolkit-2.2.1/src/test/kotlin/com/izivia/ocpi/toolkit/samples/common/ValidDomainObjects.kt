package com.izivia.ocpi.toolkit.samples.common

import com.izivia.ocpi.toolkit.modules.locations.domain.*
import com.izivia.ocpi.toolkit.modules.tokens.domain.TokenType
import com.izivia.ocpi.toolkit.modules.types.DisplayText
import java.math.BigDecimal
import java.time.Instant

val validUrl = "https://abc.com"
val validLatitude = "-15.234567"
val validLongitude = "23.234567"
val validUtf8String = "abc 1234 èñü 谢谢"

val validStatusSchedule = StatusSchedule(
    periodBegin = Instant.parse("2022-04-28T08:00:00.000Z"),
    periodEnd = Instant.parse("2022-04-28T09:00:00.000Z"),
    status = Status.AVAILABLE,
)

val validDisplayText = DisplayText(
    language = "fr",
    text = validUtf8String,
)

val validGeoLocation = GeoLocation(
    latitude = validLatitude,
    longitude = validLongitude,
)

val validAdditionalGeoLocation = AdditionalGeoLocation(
    latitude = validLatitude,
    longitude = validLongitude,
    name = validDisplayText,
)

val validImage = Image(
    url = validUrl,
    thumbnail = validUrl,
    category = ImageCategory.LOCATION,
    type = "jpeg",
    width = 12345,
    height = 123,
)

val validBusinessDetails = BusinessDetails(
    name = "name",
    website = validUrl,
    logo = validImage,
)

val validRegularHours = RegularHours(
    weekday = 1,
    periodBegin = "00:00",
    periodEnd = "23:59",
)

val validExceptionalPeriod = ExceptionalPeriod(
    periodBegin = Instant.parse("2022-04-28T08:00:00.000Z"),
    periodEnd = Instant.parse("2022-04-28T09:00:00.000Z"),
)

val validHours = Hours(
    regularHours = listOf(validRegularHours),
    twentyfourseven = false,
    exceptionalOpenings = listOf(validExceptionalPeriod),
    exceptionalClosings = emptyList(),
)

val validEnergySource = EnergySource(
    source = EnergySourceCategory.NUCLEAR,
    percentage = BigDecimal.ZERO,
)

val validEnvironmentalImpact = EnvironmentalImpact(
    category = EnvironmentalImpactCategory.CARBON_DIOXIDE,
    amount = BigDecimal.ZERO,
)

val validEnergyMix = EnergyMix(
    isGreenEnergy = true,
    energySources = listOf(validEnergySource),
    environImpact = listOf(validEnvironmentalImpact),
    supplierName = "supplier_name",
    energyProductName = "energy_product_name",
)

val validPublishTokenType = PublishTokenType(
    uid = "uid",
    type = TokenType.AD_HOC_USER,
    visualNumber = "visual_number",
    issuer = "issuer",
    groupId = "group_id",
)

val validLocation = Location(
    countryCode = "fr",
    partyId = "abc",
    id = "location1",
    publish = true,
    publishAllowedTo = listOf(validPublishTokenType),
    name = "name",
    address = "address",
    city = "city",
    postalCode = "33520",
    state = null,
    country = "FRA",
    coordinates = validGeoLocation,
    relatedLocations = listOf(validAdditionalGeoLocation),
    parkingType = ParkingType.ON_STREET,
    evses = emptyList(),
    directions = listOf(validDisplayText),
    operator = validBusinessDetails,
    suboperator = null,
    owner = null,
    facilities = listOf(Facility.CARPOOL_PARKING),
    timeZone = "Europe/Oslo",
    openingTimes = validHours,
    chargingWhenClosed = false,
    images = listOf(validImage),
    energyMix = validEnergyMix,
    lastUpdated = Instant.now(),
)

val validConnector = Connector(
    id = "id",
    standard = ConnectorType.DOMESTIC_A,
    format = ConnectorFormat.CABLE,
    powerType = PowerType.AC_1_PHASE,
    maxVoltage = 12,
    maxAmperage = 8,
    maxElectricPower = null,
    tariffIds = listOf("tariff_id"),
    termsAndConditions = "https://myspecs.com/me",
    lastUpdated = Instant.now(),
)

val validEvse = Evse(
    uid = "uid",
    evseId = "FR*A23*E45B*78C",
    status = Status.AVAILABLE,
    statusSchedule = listOf(validStatusSchedule),
    capabilities = listOf(Capability.CHARGING_PROFILE_CAPABLE),
    connectors = listOf(validConnector),
    floorLevel = "5",
    coordinates = validGeoLocation,
    physicalReference = "visualid1",
    directions = listOf(validDisplayText),
    parkingRestrictions = listOf(ParkingRestriction.CUSTOMERS),
    images = listOf(validImage),
    lastUpdated = Instant.now(),
)
