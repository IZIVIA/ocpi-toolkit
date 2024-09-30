package com.izivia.ocpi.toolkit.tests.integration

import com.izivia.ocpi.toolkit.common.*
import com.izivia.ocpi.toolkit.modules.credentials.CredentialsServer
import com.izivia.ocpi.toolkit.modules.credentials.domain.CredentialRole
import com.izivia.ocpi.toolkit.modules.credentials.domain.Role
import com.izivia.ocpi.toolkit.modules.credentials.repositories.CredentialsRoleRepository
import com.izivia.ocpi.toolkit.modules.credentials.services.CredentialsClientService
import com.izivia.ocpi.toolkit.modules.credentials.services.CredentialsServerService
import com.izivia.ocpi.toolkit.modules.credentials.services.RequiredEndpoints
import com.izivia.ocpi.toolkit.modules.locations.domain.BusinessDetails
import com.izivia.ocpi.toolkit.modules.versions.VersionsClient
import com.izivia.ocpi.toolkit.modules.versions.VersionsServer
import com.izivia.ocpi.toolkit.modules.versions.domain.ModuleID
import com.izivia.ocpi.toolkit.modules.versions.domain.Version
import com.izivia.ocpi.toolkit.modules.versions.domain.VersionNumber
import com.izivia.ocpi.toolkit.modules.versions.repositories.InMemoryVersionsRepository
import com.izivia.ocpi.toolkit.modules.versions.services.VersionsService
import com.izivia.ocpi.toolkit.samples.common.*
import com.izivia.ocpi.toolkit.tests.integration.common.BaseServerIntegrationTest
import com.izivia.ocpi.toolkit.tests.integration.mock.PartnerMongoRepository
import com.izivia.ocpi.toolkit.transport.domain.HttpMethod
import com.izivia.ocpi.toolkit.transport.domain.HttpStatus
import com.mongodb.client.MongoCollection
import com.mongodb.client.MongoDatabase
import kotlinx.coroutines.runBlocking
import org.eclipse.jetty.client.HttpResponseException
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.litote.kmongo.eq
import org.litote.kmongo.getCollection
import strikt.api.expectCatching
import strikt.api.expectThat
import strikt.api.expectThrows
import strikt.assertions.*
import java.util.*
import java.util.concurrent.ExecutionException

class CredentialsIntegrationTests : BaseServerIntegrationTest() {

    data class ServerSetupResult(
        val transport: Http4kTransportServer,
        val partnerCollection: MongoCollection<Partner>,
        val versionsEndpoint: String,
    )

    private var database: MongoDatabase? = null

    private fun setupReceiver(requiredEndpoints: RequiredEndpoints? = null): ServerSetupResult {
        if (database == null) database = buildDBClient().getDatabase("ocpi-2-2-1-tests")
        val receiverPartnerCollection = database!!
            .getCollection<Partner>("receiver-server-${UUID.randomUUID()}")

        // Reset spy variables
        requestCounter = 1
        correlationCounter = 1

        // Setup receiver (only server)
        val receiverPlatformRepo = PartnerMongoRepository(collection = receiverPartnerCollection)
        val receiverServer = buildTransportServer(receiverPlatformRepo)
        val receiverServerVersionsUrl = "${receiverServer.baseUrl}/versions"
        val receiverVersionsCacheRepository = InMemoryVersionsRepository()
        runBlocking {
            CredentialsServer(
                service = CredentialsServerService(
                    partnerRepository = receiverPlatformRepo,
                    credentialsRoleRepository = object : CredentialsRoleRepository {
                        override suspend fun getCredentialsRoles(): List<CredentialRole> = listOf(
                            CredentialRole(
                                role = Role.EMSP,
                                businessDetails = BusinessDetails(name = "Receiver", website = null, logo = null),
                                partyId = "DEF",
                                countryCode = "FR",
                            ),
                        )
                    },
                    transportClientBuilder = Http4kTransportClientBuilder(),
                    serverVersionsUrlProvider = { receiverServerVersionsUrl },
                    requiredEndpoints = requiredEndpoints,
                ),
                versionsRepository = receiverVersionsCacheRepository,
            ).registerOn(receiverServer)
            VersionsServer(
                service = VersionsService(
                    repository = receiverVersionsCacheRepository,
                    baseUrlProvider = receiverServer::baseUrl,
                ),
            ).registerOn(receiverServer)
        }

        return ServerSetupResult(
            transport = receiverServer,
            partnerCollection = receiverPartnerCollection,
            versionsEndpoint = receiverServerVersionsUrl,
        )
    }

    private fun setupSender(): ServerSetupResult {
        if (database == null) database = buildDBClient().getDatabase("ocpi-2-2-1-tests")
        val senderPartnerCollection = database!!
            .getCollection<Partner>("sender-server-${UUID.randomUUID()}")

        // Reset spy variables
        requestCounter = 1
        correlationCounter = 1

        // Setup sender (server)
        val senderServer = buildTransportServer(PartnerMongoRepository(collection = senderPartnerCollection))
        val senderServerVersionsUrl = "${senderServer.baseUrl}/versions"

        runBlocking {
            VersionsServer(
                service = VersionsService(
                    repository = VersionsCacheRepository(baseUrl = senderServer.baseUrl),
                    baseUrlProvider = senderServer::baseUrl,
                ),
            ).registerOn(senderServer)
        }

        return ServerSetupResult(
            transport = senderServer,
            partnerCollection = senderPartnerCollection,
            versionsEndpoint = senderServerVersionsUrl,
        )
    }

    private fun setupCredentialsSenderClient(
        senderServerSetupResult: ServerSetupResult,
        receiverServerSetupResult: ServerSetupResult,
        requiredEndpoints: RequiredEndpoints? = null,
    ): CredentialsClientService {
        // Setup sender (client)
        return CredentialsClientService(
            clientVersionsEndpointUrl = senderServerSetupResult.versionsEndpoint,
            clientPartnerRepository = PartnerMongoRepository(collection = senderServerSetupResult.partnerCollection),
            clientVersionsRepository = VersionsCacheRepository(baseUrl = senderServerSetupResult.transport.baseUrl),
            clientCredentialsRoleRepository = object : CredentialsRoleRepository {
                override suspend fun getCredentialsRoles(): List<CredentialRole> = listOf(
                    CredentialRole(
                        role = Role.CPO,
                        businessDetails = BusinessDetails(name = "Sender", website = null, logo = null),
                        partyId = "ABC",
                        countryCode = "FR",
                    ),
                )
            },
            partnerId = receiverServerSetupResult.versionsEndpoint,
            transportClientBuilder = Http4kTransportClientBuilder(),
            requiredEndpoints = requiredEndpoints,
        )
    }

    @Test
    fun `should not properly run registration because wrong setup of token A`() {
        val receiverServer = setupReceiver()
        val senderServer = setupSender()

        val credentialsClientService = setupCredentialsSenderClient(
            senderServerSetupResult = senderServer,
            receiverServerSetupResult = receiverServer,
        )

        val tokenA = UUID.randomUUID().toString()
        receiverServer.partnerCollection.insertOne(Partner(url = senderServer.versionsEndpoint, tokenA = tokenA))

        // Start the servers
        receiverServer.transport.start()
        senderServer.transport.start()

        // Fails because the senders does not know the TOKEN_A to send with the request
        expectCatching {
            credentialsClientService.register()
        }.isFailure().isA<OcpiClientInvalidParametersException>()

        receiverServer.partnerCollection.deleteOne(Partner::url eq senderServer.versionsEndpoint)
        senderServer.partnerCollection.insertOne(Partner(url = receiverServer.versionsEndpoint, tokenA = tokenA))

        // Fails because the receiver does not know the TOKEN_A used by the sender
        expectCatching {
            credentialsClientService.register()
        }
            .isFailure()
            .isA<ExecutionException>()
            .get { this.cause }
            .isA<HttpResponseException>()
            .get { this.response.status }
            .isEqualTo(HttpStatus.UNAUTHORIZED.code)

        receiverServer.partnerCollection.deleteOne(Partner::url eq senderServer.versionsEndpoint)
        receiverServer.partnerCollection.insertOne(
            Partner(
                url = receiverServer.versionsEndpoint,
                tokenA = "!$tokenA",
            ),
        )

        // Fails because the token sent by sender is not the same as the one in the receiver
        expectCatching {
            credentialsClientService.register()
        }
            .isFailure()
            .isA<ExecutionException>()
            .get { this.cause }
            .isA<HttpResponseException>()
            .get { this.response.status }
            .isEqualTo(HttpStatus.UNAUTHORIZED.code)
    }

    @Test
    fun `should access versions module properly with token A and return right errors when needed`() {
        val receiverServer = setupReceiver()
        val senderServer = setupSender()

        val tokenA = UUID.randomUUID().toString()
        receiverServer.partnerCollection.insertOne(Partner(url = senderServer.versionsEndpoint, tokenA = tokenA))
        senderServer.partnerCollection.insertOne(Partner(url = receiverServer.versionsEndpoint, tokenA = tokenA))

        receiverServer.transport.start()
        senderServer.transport.start()

        // We don't need to register, we will use TOKEN_A for our requests

        val versionsClient = VersionsClient(
            transportClientBuilder = Http4kTransportClientBuilder(),
            partnerId = receiverServer.versionsEndpoint,
            partnerRepository = PartnerMongoRepository(collection = senderServer.partnerCollection),
        )

        expectThat(
            runBlocking {
                versionsClient.getVersions()
            },
        ) {
            get { data }
                .isNotNull()
                .isNotEmpty()
                .isEqualTo(
                    listOf(
                        Version(
                            version = VersionNumber.V2_2_1.value,
                            url = "${receiverServer.transport.baseUrl}/2.2.1",
                        ),
                    ),
                )

            get { statusCode }
                .isEqualTo(OcpiStatus.SUCCESS.code)
        }
    }

    @Test
    fun `should not properly run registration process because required endpoints are missing`() {
        val receiverServer = setupReceiver(
            RequiredEndpoints(
                receiver = listOf(ModuleID.credentials, ModuleID.locations),
                sender = listOf(ModuleID.chargingprofiles),
            ),
        )
        val senderServer = setupSender()

        val credentialsClientService = setupCredentialsSenderClient(
            senderServerSetupResult = senderServer,
            receiverServerSetupResult = receiverServer,
        )

        // Store token A on the receiver side, that will be used by the sender to begin registration and store it as
        // well in the client so that it knows what token to send
        val tokenA = UUID.randomUUID().toString()
        receiverServer.partnerCollection.insertOne(Partner(url = senderServer.versionsEndpoint, tokenA = tokenA))
        senderServer.partnerCollection.insertOne(Partner(url = receiverServer.versionsEndpoint, tokenA = tokenA))

        // Start the servers
        receiverServer.transport.start()
        senderServer.transport.start()

        expectThat(
            assertThrows<OcpiResponseException> {
                runBlocking {
                    credentialsClientService.register()
                }
            },
        ) {
            get { statusCode }
                .isEqualTo(OcpiStatus.SERVER_NO_MATCHING_ENDPOINTS.code)
        }
    }

    @Test
    fun `should properly run registration process then correct get credentials from receiver`() {
        val receiverServer = setupReceiver()
        val senderServer = setupSender()

        val credentialsClientService = setupCredentialsSenderClient(
            senderServerSetupResult = senderServer,
            receiverServerSetupResult = receiverServer,
        )

        // Store token A on the receiver side, that will be used by the sender to begin registration and store it as
        // well in the client so that it knows what token to send
        val tokenA = UUID.randomUUID().toString()
        receiverServer.partnerCollection.insertOne(Partner(url = senderServer.versionsEndpoint, tokenA = tokenA))
        senderServer.partnerCollection.insertOne(Partner(url = receiverServer.versionsEndpoint, tokenA = tokenA))

        // Start the servers
        receiverServer.transport.start()
        senderServer.transport.start()

        val credentials = runBlocking {
            credentialsClientService.register()
        }

        expectThat(
            receiverServer.transport.requestHistory,
        ).hasSize(3).and {
            get(0).and {
                get { first }.and { // request
                    get { method }.isEqualTo(HttpMethod.GET)
                    get { path }.isEqualTo("/versions")
                    get { headers[Header.X_REQUEST_ID] }.isNotNull().isEqualTo("req-id-1")
                    get { headers[Header.X_CORRELATION_ID] }.isNotNull().isEqualTo("corr-id-1")
                    get { headers[Header.AUTHORIZATION] }.isNotNull().isEqualTo("Token ${tokenA.encodeBase64()}")
                }

                get { second }.and { // response
                    get { status }.isEqualTo(HttpStatus.OK)
                    get { headers[Header.X_REQUEST_ID] }.isNotNull().isEqualTo("req-id-1")
                    get { headers[Header.X_CORRELATION_ID] }.isNotNull().isEqualTo("corr-id-1")
                    get { headers[Header.CONTENT_TYPE] }.isNotNull().isEqualTo("application/json")
                    get { body }.isNotNull().contains("\"status_code\":1000")
                }
            }

            get(1).and {
                get { first }.and { // request
                    get { method }.isEqualTo(HttpMethod.GET)
                    get { path }.isEqualTo("/{versionNumber}")
                    get { pathParams["versionNumber"] }.isNotNull().isEqualTo("2.2.1")
                    get { headers[Header.X_REQUEST_ID] }.isNotNull().isEqualTo("req-id-2")
                    get { headers[Header.X_CORRELATION_ID] }.isNotNull().isEqualTo("corr-id-2")
                    get { headers[Header.AUTHORIZATION] }.isNotNull().isEqualTo("Token ${tokenA.encodeBase64()}")
                }

                get { second }.and { // response
                    get { status }.isEqualTo(HttpStatus.OK)
                    get { headers[Header.X_REQUEST_ID] }.isNotNull().isEqualTo("req-id-2")
                    get { headers[Header.X_CORRELATION_ID] }.isNotNull().isEqualTo("corr-id-2")
                    get { headers[Header.CONTENT_TYPE] }.isNotNull().isEqualTo("application/json")
                    get { body }.isNotNull().contains("\"status_code\":1000")
                }
            }

            get(2).and {
                get { first }.and { // request
                    get { method }.isEqualTo(HttpMethod.POST)
                    get { path }.isEqualTo("/2.2.1/credentials")
                    get { headers[Header.X_REQUEST_ID] }.isNotNull().isEqualTo("req-id-3")
                    get { headers[Header.X_CORRELATION_ID] }.isNotNull().isEqualTo("corr-id-3")
                    get { headers[Header.AUTHORIZATION] }.isNotNull().isEqualTo("Token ${tokenA.encodeBase64()}")
                    get { headers[Header.CONTENT_TYPE] }.isNotNull().isEqualTo("application/json")
                }

                get { second }.and { // response
                    get { status }.isEqualTo(HttpStatus.OK)
                    get { headers[Header.X_REQUEST_ID] }.isNotNull().isEqualTo("req-id-3")
                    get { headers[Header.X_CORRELATION_ID] }.isNotNull().isEqualTo("corr-id-3")
                    get { headers[Header.CONTENT_TYPE] }.isNotNull().isEqualTo("application/json")
                    get { body }.isNotNull().contains("\"status_code\":1000")
                }
            }
        }

        val tokenB = receiverServer.transport.requestHistory[2].first.body!!
            .substringAfterLast("\"token\":\"").split("\"").first()

        expectThat(
            senderServer.transport.requestHistory,
        ).hasSize(2).and {
            get(0).and {
                get { first }.and { // request
                    get { method }.isEqualTo(HttpMethod.GET)
                    get { path }.isEqualTo("/versions")
                    get { headers[Header.X_REQUEST_ID] }.isNotNull().isEqualTo("req-id-4")
                    get { headers[Header.X_CORRELATION_ID] }.isNotNull().isEqualTo("corr-id-3")
                    get { headers[Header.AUTHORIZATION] }.isNotNull().isEqualTo("Token ${tokenB.encodeBase64()}")
                }

                get { second }.and { // response
                    get { status }.isEqualTo(HttpStatus.OK)
                    get { headers[Header.X_REQUEST_ID] }.isNotNull().isEqualTo("req-id-4")
                    get { headers[Header.X_CORRELATION_ID] }.isNotNull().isEqualTo("corr-id-3")
                    get { headers[Header.CONTENT_TYPE] }.isNotNull().isEqualTo("application/json")
                    get { body }.isNotNull().contains("\"status_code\":1000")
                }
            }

            get(1).and {
                get { first }.and { // request
                    get { method }.isEqualTo(HttpMethod.GET)
                    get { path }.isEqualTo("/{versionNumber}")
                    get { pathParams["versionNumber"] }.isNotNull().isEqualTo("2.2.1")
                    get { headers[Header.X_REQUEST_ID] }.isNotNull().isEqualTo("req-id-5")
                    get { headers[Header.X_CORRELATION_ID] }.isNotNull().isEqualTo("corr-id-3")
                    get { headers[Header.AUTHORIZATION] }.isNotNull().isEqualTo("Token ${tokenB.encodeBase64()}")
                }

                get { second }.and { // response
                    get { status }.isEqualTo(HttpStatus.OK)
                    get { headers[Header.X_REQUEST_ID] }.isNotNull().isEqualTo("req-id-5")
                    get { headers[Header.X_CORRELATION_ID] }.isNotNull().isEqualTo("corr-id-3")
                    get { headers[Header.CONTENT_TYPE] }.isNotNull().isEqualTo("application/json")
                    get { body }.isNotNull().contains("\"status_code\":1000")
                }
            }
        }

        expectThat(
            runBlocking {
                credentialsClientService.get()
            },
        ).isEqualTo(credentials)
    }

    @Test
    fun `should properly run registration process then run update properly`() = runBlocking {
        val receiverServer = setupReceiver()
        val senderServer = setupSender()

        val credentialsClientService = setupCredentialsSenderClient(
            senderServerSetupResult = senderServer,
            receiverServerSetupResult = receiverServer,
        )

        // Store token A on the receiver side, that will be used by the sender to begin registration and store it as
        // well in the client so that it knows what token to send
        val tokenA = UUID.randomUUID().toString()
        receiverServer.partnerCollection.insertOne(Partner(url = senderServer.versionsEndpoint, tokenA = tokenA))
        senderServer.partnerCollection.insertOne(Partner(url = receiverServer.versionsEndpoint, tokenA = tokenA))

        // Start the servers
        receiverServer.transport.start()
        senderServer.transport.start()

        credentialsClientService.register()
        credentialsClientService.update()

        val versionsClient = VersionsClient(
            transportClientBuilder = Http4kTransportClientBuilder(),
            partnerId = receiverServer.versionsEndpoint,
            partnerRepository = PartnerMongoRepository(collection = senderServer.partnerCollection),
        )

        expectThat(
            versionsClient.getVersions(),
        ) {
            get { data }
                .isNotNull()
                .isNotEmpty()
                .isEqualTo(
                    listOf(
                        Version(
                            version = VersionNumber.V2_2_1.value,
                            url = "${receiverServer.transport.baseUrl}/2.2.1",
                        ),
                    ),
                )

            get { statusCode }
                .isEqualTo(OcpiStatus.SUCCESS.code)
        }
    }

    @Test
    fun `should properly run registration process then delete credentials properly then re-register`() {
        val receiverServer = setupReceiver()
        val senderServer = setupSender()

        val senderCredentialsClientService = setupCredentialsSenderClient(
            senderServerSetupResult = senderServer,
            receiverServerSetupResult = receiverServer,
        )

        // Store token A on the receiver side, that will be used by the sender to begin registration and store it as
        // well in the client so that it knows what token to send
        val tokenA = UUID.randomUUID().toString()
        receiverServer.partnerCollection.insertOne(Partner(url = senderServer.versionsEndpoint, tokenA = tokenA))
        senderServer.partnerCollection.insertOne(Partner(url = receiverServer.versionsEndpoint, tokenA = tokenA))

        // Start the servers
        receiverServer.transport.start()
        senderServer.transport.start()

        runBlocking {
            senderCredentialsClientService.register()
        }

        val senderVersionsClient = VersionsClient(
            transportClientBuilder = Http4kTransportClientBuilder(),
            partnerId = receiverServer.versionsEndpoint,
            partnerRepository = PartnerMongoRepository(collection = senderServer.partnerCollection),
        )
        val receiverVersionsClient = VersionsClient(
            transportClientBuilder = Http4kTransportClientBuilder(),
            partnerId = senderServer.versionsEndpoint,
            partnerRepository = PartnerMongoRepository(collection = receiverServer.partnerCollection),
        )

        expectThat(
            runBlocking {
                senderVersionsClient.getVersions()
            },
        ) {
            get { data }
                .isNotNull()
                .isNotEmpty()
                .isEqualTo(
                    listOf(
                        Version(
                            version = VersionNumber.V2_2_1.value,
                            url = "${receiverServer.transport.baseUrl}/2.2.1",
                        ),
                    ),
                )

            get { statusCode }
                .isEqualTo(OcpiStatus.SUCCESS.code)
        }

        // Sender unregisters, so ...
        runBlocking {
            senderCredentialsClientService.delete()
        }

        // ... receiver should not be able to call sender ...
        expectThrows<OcpiClientUnknownTokenException> {
            // no token to use, so we should have an OCPI client error
            runBlocking {
                receiverVersionsClient.getVersions()
            }
        }

        // ... and sender should still be able to call receiver, and even register!
        runBlocking {
            senderCredentialsClientService.register()
        }

        // ... and then, the receiver can send new requests ...
        expectThat(
            runBlocking {
                receiverVersionsClient.getVersions()
            },
        ) {
            get { data }
                .isNotNull()
                .isNotEmpty()
                .isEqualTo(
                    listOf(
                        Version(
                            version = VersionNumber.V2_2_1.value,
                            url = "${senderServer.transport.baseUrl}/2.2.1",
                        ),
                    ),
                )

            get { statusCode }
                .isEqualTo(OcpiStatus.SUCCESS.code)
        }

        // ... and sender can obviously still send requests
        expectThat(
            runBlocking {
                senderVersionsClient.getVersions()
            },
        ) {
            get { data }
                .isNotNull()
                .isNotEmpty()
                .isEqualTo(
                    listOf(
                        Version(
                            version = VersionNumber.V2_2_1.value,
                            url = "${receiverServer.transport.baseUrl}/2.2.1",
                        ),
                    ),
                )

            get { statusCode }
                .isEqualTo(OcpiStatus.SUCCESS.code)
        }
    }

    @Test
    fun `should properly run registration process then get, update, delete properly`() {
        val receiverServer = setupReceiver()
        val senderServer = setupSender()

        val senderCredentialsClientService = setupCredentialsSenderClient(
            senderServerSetupResult = senderServer,
            receiverServerSetupResult = receiverServer,
        )

        // Store token A on the receiver side, that will be used by the sender to begin registration and store it as
        // well in the client so that it knows what token to send
        val tokenA = UUID.randomUUID().toString()
        receiverServer.partnerCollection.insertOne(Partner(url = senderServer.versionsEndpoint, tokenA = tokenA))
        senderServer.partnerCollection.insertOne(Partner(url = receiverServer.versionsEndpoint, tokenA = tokenA))

        // Start the servers
        receiverServer.transport.start()
        senderServer.transport.start()

        val credentialsAfterRegistration = runBlocking { senderCredentialsClientService.register() }

        val senderVersionsClient = VersionsClient(
            transportClientBuilder = Http4kTransportClientBuilder(),
            partnerId = receiverServer.versionsEndpoint,
            partnerRepository = PartnerMongoRepository(collection = senderServer.partnerCollection),
        )
        val receiverVersionsClient = VersionsClient(
            transportClientBuilder = Http4kTransportClientBuilder(),
            partnerId = senderServer.versionsEndpoint,
            partnerRepository = PartnerMongoRepository(collection = receiverServer.partnerCollection),
        )

        expectThat(
            runBlocking {
                senderVersionsClient.getVersions()
            },
        ) {
            get { data }
                .isNotNull()
                .isNotEmpty()
                .isEqualTo(
                    listOf(
                        Version(
                            version = VersionNumber.V2_2_1.value,
                            url = "${receiverServer.transport.baseUrl}/2.2.1",
                        ),
                    ),
                )

            get { statusCode }
                .isEqualTo(OcpiStatus.SUCCESS.code)
        }

        expectThat(
            runBlocking {
                senderCredentialsClientService.get()
            },
        ).isEqualTo(credentialsAfterRegistration)

        runBlocking {
            senderCredentialsClientService.update()
        }

        expectThat(
            runBlocking {
                senderVersionsClient.getVersions()
            },
        ) {
            get { data }
                .isNotNull()
                .isNotEmpty()
                .isEqualTo(
                    listOf(
                        Version(
                            version = VersionNumber.V2_2_1.value,
                            url = "${receiverServer.transport.baseUrl}/2.2.1",
                        ),
                    ),
                )

            get { statusCode }
                .isEqualTo(OcpiStatus.SUCCESS.code)
        }

        // Sender unregisters, so ...
        runBlocking {
            senderCredentialsClientService.delete()
        }

        // ... receiver should not be able to call sender ...
        expectThrows<OcpiClientUnknownTokenException> {
            // no token to use, so we should have an OCPI client error
            runBlocking {
                receiverVersionsClient.getVersions()
            }
        }

        // ... and sender should still be able to call receiver
        expectThat(
            runBlocking {
                senderVersionsClient.getVersions()
            },
        ) {
            get { statusCode }
                .isEqualTo(OcpiStatus.SUCCESS.code)
        }
    }
}
