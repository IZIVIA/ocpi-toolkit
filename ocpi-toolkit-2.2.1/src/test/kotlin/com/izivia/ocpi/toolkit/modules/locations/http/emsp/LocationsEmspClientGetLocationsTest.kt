package com.izivia.ocpi.toolkit.modules.locations.http.emsp

import com.izivia.ocpi.toolkit.common.*
import com.izivia.ocpi.toolkit.modules.credentials.repositories.PartnerRepository
import com.izivia.ocpi.toolkit.modules.locations.LocationsEmspClient
import com.izivia.ocpi.toolkit.modules.locations.domain.toPartial
import com.izivia.ocpi.toolkit.modules.versions.domain.Endpoint
import com.izivia.ocpi.toolkit.modules.versions.domain.InterfaceRole
import com.izivia.ocpi.toolkit.modules.versions.domain.ModuleID
import com.izivia.ocpi.toolkit.samples.common.Http4kTransportClient
import com.izivia.ocpi.toolkit.samples.common.validLocation
import com.izivia.ocpi.toolkit.transport.TransportClient
import com.izivia.ocpi.toolkit.transport.TransportClientBuilder
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import org.http4k.core.HttpHandler
import org.http4k.core.Request
import org.http4k.core.Response
import org.junit.jupiter.api.Test
import strikt.api.expectThat
import strikt.api.expectThrows
import strikt.assertions.hasSize
import java.time.Instant
import org.http4k.core.Status.Companion as httpStatus

class LocationsEmspClientGetLocationsTest {
    private val mockPartnerRepository = mockk<PartnerRepository> {
        coEvery { getEndpoints(any()) } coAnswers {
            listOf(Endpoint(ModuleID.locations, InterfaceRole.SENDER, ""))
        }
        coEvery { getCredentialsClientToken(any()) } coAnswers { "clientToken" }
    }

    private val invalidLocation = validLocation.toPartial().copy(
        id = null,
    )

    @Test
    fun `ignore invalid locations`() {
        val client = LocationsEmspClient(
            transportClientBuilder = mockSearchResult(listOf(invalidLocation, validLocation, invalidLocation)),
            partnerId = "irrelevant",
            partnerRepository = mockPartnerRepository,
            ignoreInvalidListEntry = true,
        )

        runBlocking {
            expectThat(client.getLocations(null, null, 0, 10)) {
                get { list }.hasSize(1)
            }
        }
    }

    @Test
    fun `fail on invalid location`() {
        val client = LocationsEmspClient(
            transportClientBuilder = mockSearchResult(listOf(validLocation, invalidLocation)),
            partnerId = "irrelevant",
            partnerRepository = mockPartnerRepository,
        )

        runBlocking {
            expectThrows<OcpiToolkitResponseParsingException> {
                (client.getLocations(null, null, 0, 10))
            }
        }
    }

    @Test
    fun `retrieve valid locations`() {
        val client = LocationsEmspClient(
            transportClientBuilder = mockSearchResult(listOf(validLocation, validLocation)),
            partnerId = "irrelevant",
            partnerRepository = mockPartnerRepository,
        )

        runBlocking {
            expectThat(client.getLocations(null, null, 0, 10)) {
                get { list }.hasSize(2)
            }
        }
    }
}

private fun mockSearchResult(data: List<*>) =
    MockHttpTransportClientBuilder { _: Request ->
        Response(httpStatus.OK).body(
            mapper.writeValueAsString(
                OcpiResponseBody(
                    data = data,
                    statusCode = OcpiStatus.SUCCESS.code,
                    statusMessage = OcpiStatus.SUCCESS.name,
                    timestamp = Instant.now(),
                ),
            ),
        ).headers(
            listOf(
                Header.X_TOTAL_COUNT to data.size.toString(),
                Header.X_LIMIT to data.size.toString(),
            ),
        )
    }

class MockHttpTransportClientBuilder(
    private val handler: HttpHandler,
) : TransportClientBuilder {
    override fun build(baseUrl: String): TransportClient =
        Http4kTransportClient(handler)
}
