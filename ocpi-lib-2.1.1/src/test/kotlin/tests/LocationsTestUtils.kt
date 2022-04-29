package tests

import ocpi.locations.domain.*
import java.math.BigDecimal
import java.time.Instant

val dummyLocation = Location(
    id = "location1",
    type = LocationType.ON_STREET,
    name = "name",
    address = "address",
    city = "city",
    postal_code = "33520",
    country = "FRA",
    coordinates = GeoLocation(latitude = "1.0", longitude = "2.56"),
    related_locations = listOf(
        AdditionalGeoLocation(
            latitude = "1.001",
            longitude = "2.56002",
            name = DisplayText(
                language = "en",
                text = "Parking lot"
            )
        )
    ),
    evses = emptyList(),
    directions = listOf(
        DisplayText(
            language = "en",
            text = "In front of bridge"
        )
    ),
    operator = BusinessDetails(
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
    opening_times = Hours(
        regular_hours = listOf(
            RegularHours(
                weekday = 1,
                period_begin = "10:00",
                period_end = "18:05"
            )
        ),
        twenty_four_seven = false,
        exceptional_openings = emptyList(),
        exceptional_closings = emptyList()
    ),
    charging_when_closed = false,
    images = listOf(
        Image(
            url = "http://imageurl/a.png",
            thumbnail = null,
            category = ImageCategory.LOCATION,
            type = "png",
            width = null,
            height = null
        )
    ),
    energy_mix = EnergyMix(
        is_green_energy = true,
        energy_sources = listOf(
            EnergySource(
                source = EnergySourceCategory.NUCLEAR,
                percentage = BigDecimal.valueOf(100)
            )
        ),
        environ_impact = listOf(
            EnvironmentalImpact(
                source = EnvironmentalImpactCategory.NUCLEAR_WASTE,
                amount = BigDecimal.valueOf(2)
            )
        ),
        supplier_name = "edf",
        energy_product_name = null
    ),
    last_updated = Instant.now()
)

val dummyEvse = Evse(
    uid = "uid",
    evse_id = "evse_id",
    status = Status.AVAILABLE,
    status_schedule = listOf(
        StatusSchedule(
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
    coordinates = GeoLocation(
        latitude = "1.5",
        longitude = "6.48"
    ),
    physical_reference = "visualid1",
    directions = listOf(
        DisplayText(
            language = "en",
            text = "next to gas station"
        )
    ),
    parking_restrictions = listOf(
        ParkingRestriction.CUSTOMERS
    ),
    images = listOf(
        Image(
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

val dummyConnector = Connector(
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

val dummyLocationPartial = LocationPartial(
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

val dummyEvsePartial = EvsePartial(
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

val dummyConnectorPartial = ConnectorPartial(
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