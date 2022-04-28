package tests

import common.OcpiResponseBody
import common.OcpiStatusCode
import common.SearchResult
import ocpi.locations.domain.Location
import ocpi.locations.services.LocationsCpoService
import org.junit.jupiter.api.Test
import strikt.api.expectThat
import strikt.assertions.isEqualTo
import strikt.assertions.isNotNull
import strikt.assertions.isNull
import java.time.Instant

class LocationsCpoServiceTest {
    private lateinit var service: LocationsCpoService
    private val from = Instant.parse("2022-04-28T08:00:00.000Z")
    private val to = Instant.parse("2022-04-28T09:00:00.000Z")

    @Test
    fun getLocationsParamsValidationTest() {
        service = LocationsCpoService(locationsCpoRepository(emptyList()))

        expectThat(service.getLocations(dateFrom = from, dateTo = from, offset = 0, limit = null)) {
            get(OcpiResponseBody<SearchResult<Location>>::status_code)
                .isEqualTo(OcpiStatusCode.SUCCESS.code)

            get(OcpiResponseBody<SearchResult<Location>>::data).get { this }
                .isNotNull()
                .get(SearchResult<Location>::offset)
                .isEqualTo(0)
        }

        expectThat(service.getLocations(dateFrom = to, dateTo = from, offset = 0, limit = null)) {
            get(OcpiResponseBody<SearchResult<Location>>::status_code)
                .isEqualTo(OcpiStatusCode.INVALID_OR_MISSING_PARAMETERS.code)

            get(OcpiResponseBody<SearchResult<Location>>::data).get { this }
                .isNull()
        }

        expectThat(service.getLocations(dateFrom = from, dateTo = to, offset = 0, limit = null)) {
            get(OcpiResponseBody<SearchResult<Location>>::status_code)
                .isEqualTo(OcpiStatusCode.SUCCESS.code)

            get(OcpiResponseBody<SearchResult<Location>>::data).get { this }
                .isNotNull()
                .get(SearchResult<Location>::offset)
                .isEqualTo(0)
        }

        expectThat(service.getLocations(dateFrom = null, dateTo = to, offset = 0, limit = null)) {
            get(OcpiResponseBody<SearchResult<Location>>::status_code)
                .isEqualTo(OcpiStatusCode.SUCCESS.code)

            get(OcpiResponseBody<SearchResult<Location>>::data).get { this }
                .isNotNull()
                .get(SearchResult<Location>::offset)
                .isEqualTo(0)
        }

        expectThat(service.getLocations(dateFrom = from, dateTo = null, offset = 0, limit = null)) {
            get(OcpiResponseBody<SearchResult<Location>>::status_code)
                .isEqualTo(OcpiStatusCode.SUCCESS.code)

            get(OcpiResponseBody<SearchResult<Location>>::data).get { this }
                .isNotNull()
                .get(SearchResult<Location>::offset)
                .isEqualTo(0)
        }

        expectThat(service.getLocations(dateFrom = null, dateTo = null, offset = 0, limit = null)) {
            get(OcpiResponseBody<SearchResult<Location>>::status_code)
                .isEqualTo(OcpiStatusCode.SUCCESS.code)

            get(OcpiResponseBody<SearchResult<Location>>::data).get { this }
                .isNotNull()
                .get(SearchResult<Location>::offset)
                .isEqualTo(0)
        }

        expectThat(service.getLocations(dateFrom = null, dateTo = null, offset = -10, limit = null)) {
            get(OcpiResponseBody<SearchResult<Location>>::status_code)
                .isEqualTo(OcpiStatusCode.INVALID_OR_MISSING_PARAMETERS.code)

            get(OcpiResponseBody<SearchResult<Location>>::data).get { this }
                .isNull()
        }

        expectThat(service.getLocations(dateFrom = null, dateTo = null, offset = 0, limit = -10)) {
            get(OcpiResponseBody<SearchResult<Location>>::status_code)
                .isEqualTo(OcpiStatusCode.INVALID_OR_MISSING_PARAMETERS.code)

            get(OcpiResponseBody<SearchResult<Location>>::data).get { this }
                .isNull()
        }

        expectThat(service.getLocations(dateFrom = null, dateTo = null, offset = 0, limit = 100)) {
            get(OcpiResponseBody<SearchResult<Location>>::status_code)
                .isEqualTo(OcpiStatusCode.SUCCESS.code)

            get(OcpiResponseBody<SearchResult<Location>>::data).get { this }
                .isNotNull()
                .get(SearchResult<Location>::offset)
                .isEqualTo(0)

            get(OcpiResponseBody<SearchResult<Location>>::data).get { this }
                .isNotNull()
                .get(SearchResult<Location>::limit)
                .isEqualTo(100)
        }

        expectThat(service.getLocations(dateFrom = null, dateTo = null, offset = 100, limit = 100)) {
            get(OcpiResponseBody<SearchResult<Location>>::status_code)
                .isEqualTo(OcpiStatusCode.SUCCESS.code)

            get(OcpiResponseBody<SearchResult<Location>>::data).get { this }
                .isNotNull()
                .get(SearchResult<Location>::offset)
                .isEqualTo(100)

            get(OcpiResponseBody<SearchResult<Location>>::data).get { this }
                .isNotNull()
                .get(SearchResult<Location>::limit)
                .isEqualTo(100)
        }

        expectThat(service.getLocations(dateFrom = null, dateTo = null, offset = 0, limit = 0)) {
            get(OcpiResponseBody<SearchResult<Location>>::status_code)
                .isEqualTo(OcpiStatusCode.SUCCESS.code)

            get(OcpiResponseBody<SearchResult<Location>>::data).get { this }
                .isNotNull()
                .get(SearchResult<Location>::offset)
                .isEqualTo(0)

            get(OcpiResponseBody<SearchResult<Location>>::data).get { this }
                .isNotNull()
                .get(SearchResult<Location>::limit)
                .isEqualTo(0)
        }
    }
}