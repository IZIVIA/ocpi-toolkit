package com.izivia.ocpi.toolkit.modules.locations.http.emsp

import com.izivia.ocpi.toolkit.common.TestTransportClient
import com.izivia.ocpi.toolkit.common.TimeProvider
import com.izivia.ocpi.toolkit.modules.buildHttpRequest
import com.izivia.ocpi.toolkit.modules.locations.LocationsEmspServer
import com.izivia.ocpi.toolkit.modules.locations.domain.*
import com.izivia.ocpi.toolkit.modules.locations.repositories.LocationsEmspRepository
import com.izivia.ocpi.toolkit.modules.locations.services.LocationsEmspService
import com.izivia.ocpi.toolkit.modules.locations.services.LocationsEmspValidator
import com.izivia.ocpi.toolkit.modules.versions.repositories.InMemoryVersionsRepository
import com.izivia.ocpi.toolkit.samples.common.Http4kTransportServer
import com.izivia.ocpi.toolkit.serialization.OcpiSerializer
import com.izivia.ocpi.toolkit.serialization.mapper
import com.izivia.ocpi.toolkit.serialization.serializeObject
import com.izivia.ocpi.toolkit.transport.context.signalObjectCreated
import com.izivia.ocpi.toolkit.transport.domain.HttpMethod
import com.izivia.ocpi.toolkit.transport.domain.HttpStatus
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import io.mockk.slot
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.MethodSource
import strikt.api.expectThat
import strikt.assertions.isEqualTo
import java.time.Instant

/**
 * Verifies that PUT endpoints return HTTP 201 (Created) when the service signals
 * that a new object was created, as required by the OCPI specification.
 *
 * @see <a href="https://github.com/ocpi/ocpi/blob/v2.2.1-d2/status_codes.asciidoc">OCPI Status Codes</a>
 * @see <a href="https://github.com/IZIVIA/ocpi-toolkit/issues/65">Issue #65</a>
 */
class LocationsEmspHttpPutLocationCreatedTest : com.izivia.ocpi.toolkit.common.TestWithSerializerProviders {
    @ParameterizedTest
    @MethodSource("getAvailableOcpiSerializers")
    fun `should return 201 Created when putLocation creates a new object`(serializer: OcpiSerializer) {
        mapper = serializer
        val slots = object {
            var countryCode = slot<String>()
            var partyId = slot<String>()
            var locationId = slot<String>()
            var location = slot<Location>()
        }
        val location = Location(
            countryCode = "BE",
            partyId = "BEC",
            id = "LOC1",
            name = "Gent Zuid",
            address = "F.Rooseveltlaan 3A",
            city = "Gent",
            postalCode = "9000",
            country = "BEL",
            coordinates = GeoLocation("51.047599", "3.729944"),
            parkingType = ParkingType.ON_STREET,
            publish = true,
            timeZone = "Europe/Brussels",
            lastUpdated = Instant.parse("2015-06-29T20:39:09Z"),
        )

        // Repository that signals object creation via signalObjectCreated()
        val srv = mockk<LocationsEmspRepository> {
            coEvery {
                putLocation(
                    capture(slots.countryCode),
                    capture(slots.partyId),
                    capture(slots.locationId),
                    capture(slots.location),
                )
            } coAnswers {
                signalObjectCreated()
                location
            }
        }.buildCreatedTestServer()

        // when
        val resp = srv.send(
            buildHttpRequest(HttpMethod.PUT, "/locations/BE/BEC/LOC1", mapper.serializeObject(location)),
        )

        // then
        expectThat(resp) {
            get { status }.isEqualTo(HttpStatus.CREATED)
        }
    }

    @ParameterizedTest
    @MethodSource("getAvailableOcpiSerializers")
    fun `should return 200 OK when putLocation updates an existing object`(serializer: OcpiSerializer) {
        mapper = serializer
        val slots = object {
            var countryCode = slot<String>()
            var partyId = slot<String>()
            var locationId = slot<String>()
            var location = slot<Location>()
        }
        val location = Location(
            countryCode = "BE",
            partyId = "BEC",
            id = "LOC1",
            name = "Gent Zuid",
            address = "F.Rooseveltlaan 3A",
            city = "Gent",
            postalCode = "9000",
            country = "BEL",
            coordinates = GeoLocation("51.047599", "3.729944"),
            parkingType = ParkingType.ON_STREET,
            publish = true,
            timeZone = "Europe/Brussels",
            lastUpdated = Instant.parse("2015-06-29T20:39:09Z"),
        )

        // Repository that does NOT signal creation (object already existed)
        val srv = mockk<LocationsEmspRepository> {
            coEvery {
                putLocation(
                    capture(slots.countryCode),
                    capture(slots.partyId),
                    capture(slots.locationId),
                    capture(slots.location),
                )
            } coAnswers {
                location
            }
        }.buildCreatedTestServer()

        // when
        val resp = srv.send(
            buildHttpRequest(HttpMethod.PUT, "/locations/BE/BEC/LOC1", mapper.serializeObject(location)),
        )

        // then
        expectThat(resp) {
            get { status }.isEqualTo(HttpStatus.OK)
        }
    }
}

private val now = Instant.parse("2015-06-30T21:59:59Z")

private fun LocationsEmspRepository.buildCreatedTestServer(): TestTransportClient {
    val timeProvider = mockk<TimeProvider>()
    every { timeProvider.now() } returns now

    val transportServer = Http4kTransportServer("http://localhost:1234", 1234)

    val repo = this
    runBlocking {
        LocationsEmspServer(
            service = LocationsEmspValidator(LocationsEmspService(repo)),
            timeProvider = timeProvider,
            versionsRepository = InMemoryVersionsRepository(),
            basePathOverride = "/locations",
        ).registerOn(transportServer)
    }

    return TestTransportClient(transportServer.initRouterAndBuildClient())
}
