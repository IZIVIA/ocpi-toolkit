package com.izivia.ocpi.toolkit.samples.common

import com.izivia.ocpi.toolkit.modules.locations.domain.*
import com.izivia.ocpi.toolkit.modules.tokens.domain.TokenType
import java.math.BigDecimal
import java.time.Instant

val validUrl = "https://abc.com"
val validLatitude = "-15.234567"
val validLongitude = "23.234567"

val validStatusSchedule = StatusSchedule(
    period_begin = Instant.parse("2022-04-28T08:00:00.000Z"),
    period_end = Instant.parse("2022-04-28T09:00:00.000Z"),
    status = Status.AVAILABLE
)

val validDisplayText = DisplayText(
    language = "fr",
    text = "abcdef"
)

val validGeoLocation = GeoLocation(
    latitude = validLatitude,
    longitude = validLongitude
)

val validAdditionalGeoLocation = AdditionalGeoLocation(
    latitude = validLatitude,
    longitude = validLongitude,
    name = validDisplayText
)

val validImage = Image(
    url = validUrl,
    thumbnail = validUrl,
    category = ImageCategory.LOCATION,
    type = "jpeg",
    width = 12345,
    height = 123
)

val validBusinessDetails = BusinessDetails(
    name = "name",
    website = validUrl,
    logo = validImage
)

val validRegularHours = RegularHours(
    weekday = 1,
    period_begin = "00:00",
    period_end = "23:59",
)

val validExceptionalPeriod = ExceptionalPeriod(
    period_begin = Instant.parse("2022-04-28T08:00:00.000Z"),
    period_end = Instant.parse("2022-04-28T09:00:00.000Z")
)

val validHours = Hours(
    regular_hours = listOf(validRegularHours),
    twenty_four_seven = false,
    exceptional_openings = listOf(validExceptionalPeriod),
    exceptional_closings = emptyList()
)

val validEnergySource = EnergySource(
    source = EnergySourceCategory.NUCLEAR,
    percentage = BigDecimal.ZERO
)

val validEnvironmentalImpact = EnvironmentalImpact(
    category = EnvironmentalImpactCategory.CARBON_DIOXIDE,
    amount = BigDecimal.ZERO
)

val validEnergyMix = EnergyMix(
    is_green_energy = true,
    energy_sources = listOf(validEnergySource),
    environ_impact = listOf(validEnvironmentalImpact),
    supplier_name = "supplier_name",
    energy_product_name = "energy_product_name"
)

val validPublishTokenType = PublishTokenType(
    uid = "uid",
    type = TokenType.AD_HOC_USER,
    visual_number = "visual_number",
    issuer = "issuer",
    group_id = "group_id"
)

val validLocation = Location(
    country_code = "fr",
    party_id = "abc",
    id = "location1",
    publish = true,
    publish_allowed_to = listOf(validPublishTokenType),
    name = "name",
    address = "address",
    city = "city",
    postal_code = "33520",
    state = null,
    country = "FRA",
    coordinates = validGeoLocation,
    related_locations = listOf(validAdditionalGeoLocation),
    parking_type = ParkingType.ON_STREET,
    evses = emptyList(),
    directions = listOf(validDisplayText),
    operator = validBusinessDetails,
    suboperator = null,
    owner = null,
    facilities = listOf(Facility.CARPOOL_PARKING),
    time_zone = "Europe/Oslo",
    opening_times = validHours,
    charging_when_closed = false,
    images = listOf(validImage),
    energy_mix = validEnergyMix,
    last_updated = Instant.now()
)

val validConnector = Connector(
    id = "id",
    standard = ConnectorType.DOMESTIC_A,
    format = ConnectorFormat.CABLE,
    power_type = PowerType.AC_1_PHASE,
    max_voltage = 12,
    max_amperage = 8,
    max_electric_power = null,
    tariff_ids = listOf("tariff_id"),
    terms_and_conditions = "https://myspecs.com/me",
    last_updated = Instant.now()
)

val validEvse = Evse(
    uid = "uid",
    evse_id = "FR*A23*E45B*78C",
    status = Status.AVAILABLE,
    status_schedule = listOf(validStatusSchedule),
    capabilities = listOf(Capability.CHARGING_PROFILE_CAPABLE),
    connectors = listOf(validConnector),
    floor_level = "5",
    coordinates = validGeoLocation,
    physical_reference = "visualid1",
    directions = listOf(validDisplayText),
    parking_restrictions = listOf(ParkingRestriction.CUSTOMERS),
    images = listOf(validImage),
    last_updated = Instant.now()
)