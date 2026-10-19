package com.izivia.ocpi.toolkit.modules.credentials

import com.izivia.ocpi.toolkit.common.TestTransportClient
import com.izivia.ocpi.toolkit.common.TestWithSerializerProviders
import com.izivia.ocpi.toolkit.common.TransportClientBuilder
import com.izivia.ocpi.toolkit.modules.buildHttpRequest
import com.izivia.ocpi.toolkit.modules.credentials.domain.Credentials
import com.izivia.ocpi.toolkit.modules.credentials.domain.Role
import com.izivia.ocpi.toolkit.modules.credentials.repositories.PartnerRepository
import com.izivia.ocpi.toolkit.modules.credentials.services.CredentialsServerService
import com.izivia.ocpi.toolkit.modules.hubclientinfo.HubClientInfoReceiverClient
import com.izivia.ocpi.toolkit.modules.hubclientinfo.HubClientInfoReceiverInterface
import com.izivia.ocpi.toolkit.modules.hubclientinfo.HubClientInfoReceiverServer
import com.izivia.ocpi.toolkit.modules.hubclientinfo.HubClientInfoSenderClient
import com.izivia.ocpi.toolkit.modules.hubclientinfo.HubClientInfoSenderInterface
import com.izivia.ocpi.toolkit.modules.hubclientinfo.HubClientInfoSenderServer
import com.izivia.ocpi.toolkit.modules.hubclientinfo.domain.ClientInfo
import com.izivia.ocpi.toolkit.modules.hubclientinfo.domain.ConnectionStatus
import com.izivia.ocpi.toolkit.modules.toSearchResult
import com.izivia.ocpi.toolkit.modules.versions.VersionDetailsClient
import com.izivia.ocpi.toolkit.modules.versions.VersionsClient
import com.izivia.ocpi.toolkit.modules.versions.VersionsServer
import com.izivia.ocpi.toolkit.modules.versions.domain.Version
import com.izivia.ocpi.toolkit.modules.versions.domain.VersionDetails
import com.izivia.ocpi.toolkit.modules.versions.services.VersionsService
import com.izivia.ocpi.toolkit.samples.common.Http4kTransportServer
import com.izivia.ocpi.toolkit.serialization.OcpiSerializer
import com.izivia.ocpi.toolkit.serialization.mapper
import com.izivia.ocpi.toolkit.serialization.serializeObject
import com.izivia.ocpi.toolkit.transport.AbstractTransportClient
import com.izivia.ocpi.toolkit.transport.context.RequestMessageRoutingHeaders
import com.izivia.ocpi.toolkit.transport.domain.HttpHeaders
import com.izivia.ocpi.toolkit.transport.domain.HttpMethod
import com.izivia.ocpi.toolkit.transport.domain.HttpRequest
import com.izivia.ocpi.toolkit.transport.domain.HttpResponse
import com.izivia.ocpi.toolkit.transport.domain.HttpStatus
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.MethodSource
import strikt.api.expectThat
import strikt.assertions.all
import strikt.assertions.doesNotContainKeys
import strikt.assertions.hasSize
import strikt.assertions.isEqualTo
import strikt.assertions.isNotEmpty
import java.time.Instant

/**
 * From OCPI 2.2.1 doc: "The requests/responses to/from Configuration Modules: Credentials, Versions and Hub Client
 * Info are not to be routed [...]. Thus routing headers SHALL NOT be used with these modules."
 */
class ConfigurationModulesRoutingHeadersTest : TestWithSerializerProviders {

    private val routingHeaders = RequestMessageRoutingHeaders(
        toPartyId = "AAA",
        toCountryCode = "FR",
        fromPartyId = "BBB",
        fromCountryCode = "DE",
    )

    private val routingHeaderNames = arrayOf(
        HttpHeaders.OCPI_TO_PARTY_ID,
        HttpHeaders.OCPI_TO_COUNTRY_CODE,
        HttpHeaders.OCPI_FROM_PARTY_ID,
        HttpHeaders.OCPI_FROM_COUNTRY_CODE,
    )

    private val versionsBody =
        """{"data":[{"version":"2.2.1","url":"http://sender/2.2.1"}],"status_code":1000,""" +
            """"timestamp":"2026-01-01T00:00:00Z"}"""
    private val versionDetailsBody =
        """{"data":{"version":"2.2.1","endpoints":[]},"status_code":1000,"timestamp":"2026-01-01T00:00:00Z"}"""
    private val credentialsBody =
        """{"data":{"token":"token","url":"http://sender/versions","roles":[]},"status_code":1000,""" +
            """"timestamp":"2026-01-01T00:00:00Z"}"""
    private val emptyBody = """{"status_code":1000,"timestamp":"2026-01-01T00:00:00Z"}"""
    private val emptyListBody = """{"data":[],"status_code":1000,"timestamp":"2026-01-01T00:00:00Z"}"""

    @ParameterizedTest
    @MethodSource("getAvailableOcpiSerializers")
    fun `credentials handshake does not forward the incoming routing headers to the version calls`(
        serializer: OcpiSerializer,
    ) {
        mapper = serializer
        val transport = RecordingTransportClient {
            if (transportUrls.last() == "http://sender/versions") versionsBody else versionDetailsBody
        }
        val partnerRepository = mockk<PartnerRepository>(relaxed = true)
        coEvery { partnerRepository.getPartnerIdByCredentialsServerToken(any()) } returns null
        coEvery { partnerRepository.getPartnerIdByCredentialsTokenA(any()) } returns "partner"
        coEvery { partnerRepository.saveCredentialsServerToken(any(), any()) } returns "tokenC"
        val service = CredentialsServerService(
            partnerRepository = partnerRepository,
            credentialsRoleRepository = mockk(relaxed = true),
            transportClientBuilder = transport.builder(),
            serverVersionsUrlProvider = { "http://receiver/versions" },
            requiredEndpoints = null,
        )

        runBlocking(routingHeaders) {
            service.post(
                token = "tokenA",
                credentials = Credentials(token = "tokenB", url = "http://sender/versions", roles = emptyList()),
                debugHeaders = emptyMap(),
            )
        }

        expectThat(transport.requests).hasSize(2).all { get { headers }.doesNotContainKeys(*routingHeaderNames) }
    }

    @ParameterizedTest
    @MethodSource("getAvailableOcpiSerializers")
    fun `configuration module clients do not send routing headers`(serializer: OcpiSerializer) {
        mapper = serializer
        val transport = RecordingTransportClient { request ->
            when {
                transportUrls.last() == "http://sender/versions" -> versionsBody
                transportUrls.last() == "http://sender/2.2.1" -> versionDetailsBody
                transportUrls.last() == "http://sender/credentials" && request.method != HttpMethod.DELETE ->
                    credentialsBody
                transportUrls.last() == "http://sender/hubclientinfo" && request.method == HttpMethod.GET &&
                    request.path.isEmpty() -> emptyListBody
                else -> emptyBody
            }
        }
        val partnerRepository = mockk<PartnerRepository>()
        coEvery { partnerRepository.getPartnerUrl("partner") } returns "http://sender/versions"
        coEvery { partnerRepository.getVersion("partner") } returns Version("2.2.1", "http://sender/2.2.1")
        val credentials = Credentials(token = "token", url = "http://receiver/versions", roles = emptyList())
        val clientInfo = ClientInfo("FR", "ABC", Role.EMSP, ConnectionStatus.CONNECTED, Instant.EPOCH)

        runBlocking(routingHeaders) {
            VersionsClient(transport.builder(), "partner", partnerRepository).getVersions()
            VersionDetailsClient(transport.builder(), "partner", partnerRepository).getVersionDetails()

            transport.transportUrls += "http://sender/credentials"
            CredentialsClient(transport).run {
                get("token")
                post("token", credentials, emptyMap())
                put("token", credentials, emptyMap())
                delete("token")
            }

            HubClientInfoSenderClient(transport.builder("http://sender/hubclientinfo"), "partner").run {
                get("FR", "ABC")
                put("FR", "ABC", clientInfo)
            }
            HubClientInfoReceiverClient(transport.builder("http://sender/hubclientinfo"), "partner")
                .getAll(dateFrom = null, dateTo = null, offset = 0, limit = null)
        }

        expectThat(transport.requests).isNotEmpty().all { get { headers }.doesNotContainKeys(*routingHeaderNames) }
    }

    @ParameterizedTest
    @MethodSource("getAvailableOcpiSerializers")
    fun `configuration module servers do not answer with routing headers`(serializer: OcpiSerializer) {
        mapper = serializer
        val credentials = Credentials(token = "token", url = "http://receiver/versions", roles = emptyList())
        val clientInfo = ClientInfo("FR", "ABC", Role.EMSP, ConnectionStatus.CONNECTED, Instant.EPOCH)
        val credentialsService = mockk<CredentialsServerService>()
        coEvery { credentialsService.get(any()) } returns credentials
        coEvery { credentialsService.post(any(), any(), any()) } returns credentials
        coEvery { credentialsService.put(any(), any(), any()) } returns credentials
        coEvery { credentialsService.delete(any()) } returns Unit
        val versionsService = mockk<VersionsService> {
            every { versionsBasePath } returns "/versions"
            every { versionDetailsBasePath } returns "/versions"
            coEvery { getVersions() } returns listOf(Version("2.2.1", "http://receiver/versions/2.2.1"))
            coEvery { getVersionDetails(any()) } returns VersionDetails("2.2.1", emptyList())
        }
        val hubClientInfoReceiverService = mockk<HubClientInfoReceiverInterface>()
        coEvery { hubClientInfoReceiverService.get(any(), any()) } returns clientInfo
        coEvery { hubClientInfoReceiverService.put(any(), any(), any()) } returns Unit
        val hubClientInfoSenderService = mockk<HubClientInfoSenderInterface> {
            coEvery { getAll(any(), any(), any(), any()) } returns listOf(clientInfo).toSearchResult()
        }
        val transportServer = Http4kTransportServer("http://localhost:1234", 1234)
        runBlocking {
            CredentialsServer(credentialsService).registerOn(transportServer)
            VersionsServer(versionsService).registerOn(transportServer)
            HubClientInfoReceiverServer(hubClientInfoReceiverService).registerOn(transportServer)
            HubClientInfoSenderServer(hubClientInfoSenderService).registerOn(transportServer)
        }
        val client = TestTransportClient(transportServer.initRouterAndBuildClient())
        val credentialsBody = mapper.serializeObject(credentials)

        val responses = listOf(
            buildHttpRequest(HttpMethod.GET, "/2.2.1/credentials"),
            buildHttpRequest(HttpMethod.POST, "/2.2.1/credentials", credentialsBody),
            buildHttpRequest(HttpMethod.PUT, "/2.2.1/credentials", credentialsBody),
            buildHttpRequest(HttpMethod.DELETE, "/2.2.1/credentials"),
            buildHttpRequest(HttpMethod.GET, "/versions"),
            buildHttpRequest(HttpMethod.GET, "/versions/2.2.1"),
            buildHttpRequest(HttpMethod.GET, "/2.2.1/hubclientinfo/FR/ABC"),
            buildHttpRequest(HttpMethod.PUT, "/2.2.1/hubclientinfo/FR/ABC", mapper.serializeObject(clientInfo)),
            buildHttpRequest(HttpMethod.GET, "/2.2.1/hubclientinfo"),
        ).map { request -> client.send(request.withHeadersMixin(routingHeaders.toHttpHeaders())) }

        expectThat(responses).all {
            get { status }.isEqualTo(HttpStatus.OK)
            get { headers }.doesNotContainKeys(*routingHeaderNames)
        }
    }

    /**
     * Uses [AbstractTransportClient] so that routing headers are added exactly as they would be in production, and
     * records the requests actually sent.
     */
    private class RecordingTransportClient(
        private val respond: RecordingTransportClient.(HttpRequest) -> String,
    ) : AbstractTransportClient("token") {
        val requests = mutableListOf<HttpRequest>()
        val transportUrls = mutableListOf<String>()

        override suspend fun doSend(request: HttpRequest): HttpResponse {
            requests += request
            // pagination headers are only needed by paginated responses, but are harmless for the other ones
            return HttpResponse(
                status = HttpStatus.OK,
                body = respond(request),
                headers = mapOf(HttpHeaders.X_TOTAL_COUNT to "0", HttpHeaders.X_LIMIT to "10"),
            )
        }

        fun builder(moduleUrl: String? = null): TransportClientBuilder {
            val builder = mockk<TransportClientBuilder>()
            coEvery { builder.build(any(), any()) } answers {
                transportUrls += firstArg<String>()
                this@RecordingTransportClient
            }
            coEvery { builder.buildFor(any(), any<String>(), any()) } answers {
                transportUrls += secondArg<String>()
                this@RecordingTransportClient
            }
            coEvery { builder.buildFor(any(), any(), any(), any()) } answers {
                transportUrls += moduleUrl.orEmpty()
                this@RecordingTransportClient
            }
            return builder
        }
    }
}
