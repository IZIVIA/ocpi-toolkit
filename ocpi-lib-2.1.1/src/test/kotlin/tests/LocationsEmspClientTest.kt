package tests

import ocpi.locations.domain.*
import org.junit.jupiter.api.Test
import java.math.BigDecimal
import java.time.Instant


class LocationsEmspClientTest {

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

    @Test
    fun locationsEmspClientTest() {
    }
}