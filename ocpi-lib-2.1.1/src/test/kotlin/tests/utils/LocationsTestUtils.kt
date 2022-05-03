package tests.utils

import ocpi.locations.domain.*
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
    twenty_four_seven = null,
    exceptional_openings = listOf(validExceptionalPeriod),
    exceptional_closings = emptyList()
)

val validEnergySource = EnergySource(
    source = EnergySourceCategory.NUCLEAR,
    percentage = BigDecimal.ZERO
)

val validEnvironmentalImpact = EnvironmentalImpact(
    source = EnvironmentalImpactCategory.CARBON_DIOXIDE,
    amount = BigDecimal.ZERO
)

val validEnergyMix = EnergyMix(
    is_green_energy = true,
    energy_sources = listOf(validEnergySource),
    environ_impact = listOf(validEnvironmentalImpact),
    supplier_name = "supplier_name",
    energy_product_name = "energy_product_name"
)

val validLocation = Location(
    id = "location1",
    type = LocationType.ON_STREET,
    name = "name",
    address = "address",
    city = "city",
    postal_code = "33520",
    country = "FRA",
    coordinates = validGeoLocation,
    related_locations = listOf(validAdditionalGeoLocation),
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

val validEvse = Evse(
    uid = "uid",
    evse_id = "FR*A23*E45B*78C",
    status = Status.AVAILABLE,
    status_schedule = listOf(validStatusSchedule),
    capabilities = listOf(Capability.CHARGING_PROFILE_CAPABLE),
    connectors = emptyList(),
    floor_level = "5",
    coordinates = validGeoLocation,
    physical_reference = "visualid1",
    directions = listOf(validDisplayText),
    parking_restrictions = listOf(ParkingRestriction.CUSTOMERS),
    images = listOf(validImage),
    last_updated = Instant.now()
)

val validConnector = Connector(
    id = "id",
    standard = ConnectorType.DOMESTIC_A,
    format = ConnectorFormat.CABLE,
    power_type = PowerType.AC_1_PHASE,
    voltage = 12,
    amperage = 8,
    tariff_id = "tariff_id",
    terms_and_conditions = "https://myspecs.com/me",
    last_updated = Instant.now()
)

val validLocationPartial = LocationPartial(
    id = "location1",
    type = LocationType.ON_STREET,
    name = "name",
    address = "address",
    city = "city",
    postal_code = "33520",
    country = "FRA",
    coordinates = GeoLocationPartial(latitude = "1.0", longitude = "2.56"),
    related_locations = listOf(
        AdditionalGeoLocationPartial(
            latitude = "1.001",
            longitude = "2.56002",
            name = DisplayTextPartial(
                language = "en",
                text = "Parking lot"
            )
        )
    ),
    evses = emptyList(),
    directions = listOf(
        DisplayTextPartial(
            language = "en",
            text = "In front of bridge"
        )
    ),
    operator = BusinessDetailsPartial(
        name = "John",
        website = null,
        logo = null
    ),
    suboperator = null,
    owner = null,
    facilities = listOf(
        Facility.CARPOOL_PARKING
    ),
    time_zone = "Europe/Oslo",
    opening_times = HoursPartial(
        regular_hours = listOf(
            RegularHoursPartial(
                weekday = 1,
                period_begin = "10:00",
                period_end = "18:05"
            )
        ),
        twenty_four_seven = null,
        exceptional_openings = emptyList(),
        exceptional_closings = emptyList()
    ),
    charging_when_closed = false,
    images = listOf(
        ImagePartial(
            url = "http://imageurl/a.png",
            thumbnail = null,
            category = ImageCategory.LOCATION,
            type = "png",
            width = null,
            height = null
        )
    ),
    energy_mix = EnergyMixPartial(
        is_green_energy = true,
        energy_sources = listOf(
            EnergySourcePartial(
                source = EnergySourceCategory.NUCLEAR,
                percentage = BigDecimal.valueOf(100)
            )
        ),
        environ_impact = listOf(
            EnvironmentalImpactPartial(
                source = EnvironmentalImpactCategory.NUCLEAR_WASTE,
                amount = BigDecimal.valueOf(2)
            )
        ),
        supplier_name = "edf",
        energy_product_name = null
    ),
    last_updated = Instant.now()
)

val validEvsePartial = EvsePartial(
    uid = "uid",
    evse_id = "evse_id",
    status = Status.AVAILABLE,
    status_schedule = listOf(
        StatusSchedulePartial(
            period_begin = Instant.now(),
            period_end = Instant.now().plusSeconds(60),
            status = Status.AVAILABLE
        )
    ),
    capabilities = listOf(
        Capability.CHARGING_PROFILE_CAPABLE
    ),
    connectors = emptyList(),
    floor_level = "5",
    coordinates = GeoLocationPartial(
        latitude = "1.5",
        longitude = "6.48"
    ),
    physical_reference = "visualid1",
    directions = listOf(
        DisplayTextPartial(
            language = "en",
            text = "next to gas station"
        )
    ),
    parking_restrictions = listOf(
        ParkingRestriction.CUSTOMERS
    ),
    images = listOf(
        ImagePartial(
            url = "http://imageurl/a.png",
            thumbnail = null,
            category = ImageCategory.LOCATION,
            type = "png",
            width = null,
            height = null
        )
    ),
    last_updated = Instant.now()
)

val validConnectorPartial = ConnectorPartial(
    id = "id",
    standard = ConnectorType.DOMESTIC_A,
    format = ConnectorFormat.CABLE,
    power_type = PowerType.AC_1_PHASE,
    voltage = 12,
    amperage = 8,
    tariff_id = "tariff_id",
    terms_and_conditions = "https://myspecs.com/me",
    last_updated = Instant.now()
)