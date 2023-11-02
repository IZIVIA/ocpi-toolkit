package com.izivia.ocpi.toolkit.tests.integration

import com.izivia.ocpi.toolkit.common.*
import com.izivia.ocpi.toolkit.modules.credentials.CredentialsServer
import com.izivia.ocpi.toolkit.modules.credentials.domain.BusinessDetails
import com.izivia.ocpi.toolkit.modules.credentials.domain.CredentialRole
import com.izivia.ocpi.toolkit.modules.credentials.domain.Role
import com.izivia.ocpi.toolkit.modules.credentials.repositories.CredentialsRoleRepository
import com.izivia.ocpi.toolkit.modules.credentials.services.CredentialsClientService
import com.izivia.ocpi.toolkit.modules.credentials.services.CredentialsServerService
import com.izivia.ocpi.toolkit.modules.versions.VersionDetailsServer
import com.izivia.ocpi.toolkit.modules.versions.VersionsClient
import com.izivia.ocpi.toolkit.modules.versions.VersionsServer
import com.izivia.ocpi.toolkit.modules.versions.services.VersionDetailsService
import com.izivia.ocpi.toolkit.modules.versions.services.VersionsService
import com.izivia.ocpi.toolkit.samples.common.*
import com.izivia.ocpi.toolkit.tests.integration.common.BaseServerIntegrationTest
import com.izivia.ocpi.toolkit.tests.integration.mock.PlatformMongoRepository
import com.izivia.ocpi.toolkit.transport.domain.HttpMethod
import com.izivia.ocpi.toolkit.transport.domain.HttpStatus
import com.mongodb.client.MongoCollection
import com.mongodb.client.MongoDatabase
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Test
import org.litote.kmongo.eq
import org.litote.kmongo.getCollection
import strikt.api.expectCatching
import strikt.api.expectThat
import strikt.api.expectThrows
import strikt.assertions.*
import java.util.*

class CredentialsIntegrationTests : BaseServerIntegrationTest() {

    data class ServerSetupResult(
        val transport: Http4kTransportServer,
        val platformCollection: MongoCollection<Platform>,
        val versionsEndpoint: String
    )

    private var database: MongoDatabase? = null

    private fun setupReceiver(): ServerSetupResult {
        if (database == null) database = buildDBClient().getDatabase("ocpi-2-2-1-tests")
        val receiverPlatformCollection = database!!
            .getCollection<Platform>("receiver-server-${UUID.randomUUID()}")

        // Reset spy variables
        requestCounter = 1
        correlationCounter = 1

        // Setup receiver (only server)
        val receiverPlatformRepo = PlatformMongoRepository(collection = receiverPlatformCollection)
        val receiverServer = buildTransportServer(receiverPlatformRepo)
        val receiverServerVersionsUrl = "${receiverServer.baseUrl}/versions"
        val receiverVersionsCacheRepository = VersionsCacheRepository(baseUrl = receiverServer.baseUrl)
        val receiverVersionDetailsCacheRepository = VersionDetailsCacheRepository(baseUrl = receiverServer.baseUrl)
        runBlocking {
            CredentialsServer(
                service = CredentialsServerService(
                    platformRepository = receiverPlatformRepo,
                    credentialsRoleRepository = object : CredentialsRoleRepository {
                        override suspend fun getCredentialsRoles(): List<CredentialRole> = listOf(
                            CredentialRole(
                                role = Role.EMSP,
                                business_details = BusinessDetails(name = "Receiver", website = null, logo = null),
                                party_id = "DEF",
                                country_code = "FR"
                            )
                        )
                    },
                    transportClientBuilder = Http4kTransportClientBuilder(),
                    serverVersionsUrlProvider = { receiverServerVersionsUrl }
                )
            ).registerOn(receiverServer)
            VersionsServer(
                service = VersionsService(
                    repository = receiverVersionsCacheRepository
                )
            ).registerOn(receiverServer)
            VersionDetailsServer(
                service = VersionDetailsService(
                    repository = receiverVersionDetailsCacheRepository
                )
            ).registerOn(receiverServer)
        }

        return ServerSetupResult(
            transport = receiverServer,
            platformCollection = receiverPlatformCollection,
            versionsEndpoint = receiverServerVersionsUrl
        )
    }

    private fun setupSender(): ServerSetupResult {
        if (database == null) database = buildDBClient().getDatabase("ocpi-2-2-1-tests")
        val senderPlatformCollection = database!!
            .getCollection<Platform>("sender-server-${UUID.randomUUID()}")

        // Reset spy variables
        requestCounter = 1
        correlationCounter = 1

        // Setup sender (server)
        val senderServer = buildTransportServer(PlatformMongoRepository(collection = senderPlatformCollection))
        val senderServerVersionsUrl = "${senderServer.baseUrl}/versions"

        runBlocking {
            VersionsServer(
                service = VersionsService(
                    repository = VersionsCacheRepository(baseUrl = senderServer.baseUrl)
                )
            ).registerOn(senderServer)
            VersionDetailsServer(
                service = VersionDetailsService(
                    repository = VersionDetailsCacheRepository(baseUrl = senderServer.baseUrl)
                )
            ).registerOn(senderServer)
        }

        return ServerSetupResult(
            transport = senderServer,
            platformCollection = senderPlatformCollection,
            versionsEndpoint = senderServerVersionsUrl
        )
    }

    private fun setupCredentialsSenderClient(
        senderServerSetupResult: ServerSetupResult,
        receiverServerSetupResult: ServerSetupResult
    ): CredentialsClientService {
        // Setup sender (client)
        return CredentialsClientService(
            clientVersionsEndpointUrl = senderServerSetupResult.versionsEndpoint,
            clientPlatformRepository = PlatformMongoRepository(collection = senderServerSetupResult.platformCollection),
            clientVersionsRepository = VersionsCacheRepository(baseUrl = senderServerSetupResult.transport.baseUrl),
            clientCredentialsRoleRepository = object : CredentialsRoleRepository {
                override suspend fun getCredentialsRoles(): List<CredentialRole> = listOf(
                    CredentialRole(
                        role = Role.CPO,
                        business_details = BusinessDetails(name = "Sender", website = null, logo = null),
                        party_id = "ABC",
                        country_code = "FR"
                    )
                )
            },
            serverVersionsEndpointUrl = receiverServerSetupResult.versionsEndpoint,
            transportClientBuilder = Http4kTransportClientBuilder()
        )
    }

    @Test
    fun `should not properly run registration because wrong setup of token A`() {
        val receiverServer = setupReceiver()
        val senderServer = setupSender()

        val credentialsClientService = setupCredentialsSenderClient(
            senderServerSetupResult = senderServer,
            receiverServerSetupResult = receiverServer
        )

        val tokenA = UUID.randomUUID().toString()
        receiverServer.platformCollection.insertOne(Platform(url = senderServer.versionsEndpoint, tokenA = tokenA))

        // Start the servers
        receiverServer.transport.start()
        senderServer.transport.start()

        // Fails because the senders does not know the TOKEN_A to send with the request
        expectCatching {
            credentialsClientService.register()
        }.isFailure().isA<OcpiClientInvalidParametersException>()

        receiverServer.platformCollection.deleteOne(Platform::url eq senderServer.versionsEndpoint)
        senderServer.platformCollection.insertOne(Platform(url = receiverServer.versionsEndpoint, tokenA = tokenA))

        // Fails because the receiver does not know the TOKEN_A used by the sender
        expectCatching {
            credentialsClientService.register()
        }
            .isFailure()
            .isA<OcpiResponseException>()
            .get { statusCode }
            .isEqualTo(OcpiStatus.CLIENT_INVALID_PARAMETERS.code)

        receiverServer.platformCollection.deleteOne(Platform::url eq senderServer.versionsEndpoint)
        receiverServer.platformCollection.insertOne(
            Platform(
                url = receiverServer.versionsEndpoint,
                tokenA = "!$tokenA"
            )
        )

        // Fails because the token sent by sender is not the same as the one in the receiver
        expectCatching {
            credentialsClientService.register()
        }
            .isFailure()
            .isA<OcpiResponseException>()
            .get { statusCode }
            .isEqualTo(OcpiStatus.CLIENT_INVALID_PARAMETERS.code)
    }

    @Test
    fun `should access versions module properly with token A and return right errors when needed`() {
        val receiverServer = setupReceiver()
        val senderServer = setupSender()

        val tokenA = UUID.randomUUID().toString()
        receiverServer.platformCollection.insertOne(Platform(url = senderServer.versionsEndpoint, tokenA = tokenA))
        senderServer.platformCollection.insertOne(Platform(url = receiverServer.versionsEndpoint, tokenA = tokenA))

        receiverServer.transport.start()
        senderServer.transport.start()

        // We don't need to register, we will use TOKEN_A for our requests

        val versionsClient = VersionsClient(
            transportClientBuilder = Http4kTransportClientBuilder(),
            serverVersionsEndpointUrl = receiverServer.versionsEndpoint,
            platformRepository = PlatformMongoRepository(collection = senderServer.platformCollection)
        )

        expectThat(
            runBlocking {
                versionsClient.getVersions()
            }
        ) {
            get { data }
                .isNotNull()
                .isEqualTo(
                    runBlocking {
                        VersionsCacheRepository(
                            baseUrl =
                            receiverServer.transport.baseUrl
                        ).getVersions()
                    }
                )

            get { status_code }
                .isEqualTo(OcpiStatus.SUCCESS.code)
        }
    }

    @Test
    fun `should properly run registration process then correct get credentials from receiver`() {
        val receiverServer = setupReceiver()
        val senderServer = setupSender()

        val credentialsClientService = setupCredentialsSenderClient(
            senderServerSetupResult = senderServer,
            receiverServerSetupResult = receiverServer
        )

        // Store token A on the receiver side, that will be used by the sender to begin registration and store it as
        // well in the client so that it knows what token to send
        val tokenA = UUID.randomUUID().toString()
        receiverServer.platformCollection.insertOne(Platform(url = senderServer.versionsEndpoint, tokenA = tokenA))
        senderServer.platformCollection.insertOne(Platform(url = receiverServer.versionsEndpoint, tokenA = tokenA))

        // Start the servers
        receiverServer.transport.start()
        senderServer.transport.start()

        val credentials = runBlocking {
            credentialsClientService.register()
        }

        expectThat(
            receiverServer.transport.requestHistory
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
            senderServer.transport.requestHistory
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
            }
        ).isEqualTo(credentials)
    }

    @Test
    fun `should properly run registration process then run update properly`() = runBlocking {
        val receiverServer = setupReceiver()
        val senderServer = setupSender()

        val credentialsClientService = setupCredentialsSenderClient(
            senderServerSetupResult = senderServer,
            receiverServerSetupResult = receiverServer
        )

        // Store token A on the receiver side, that will be used by the sender to begin registration and store it as
        // well in the client so that it knows what token to send
        val tokenA = UUID.randomUUID().toString()
        receiverServer.platformCollection.insertOne(Platform(url = senderServer.versionsEndpoint, tokenA = tokenA))
        senderServer.platformCollection.insertOne(Platform(url = receiverServer.versionsEndpoint, tokenA = tokenA))

        // Start the servers
        receiverServer.transport.start()
        senderServer.transport.start()

        credentialsClientService.register()
        credentialsClientService.update()

        val versionsClient = VersionsClient(
            transportClientBuilder = Http4kTransportClientBuilder(),
            serverVersionsEndpointUrl = receiverServer.versionsEndpoint,
            platformRepository = PlatformMongoRepository(collection = senderServer.platformCollection)
        )

        expectThat(
            versionsClient.getVersions()
        ) {
            get { data }
                .isNotNull()
                .isNotEmpty()
                .isEqualTo(
                    runBlocking {
                        VersionsCacheRepository(baseUrl = receiverServer.transport.baseUrl)
                            .getVersions()
                    }
                )

            get { status_code }
                .isEqualTo(OcpiStatus.SUCCESS.code)
        }
    }

    @Test
    fun `should properly run registration process then delete credentials properly`() {
        val receiverServer = setupReceiver()
        val senderServer = setupSender()

        val credentialsClientService = setupCredentialsSenderClient(
            senderServerSetupResult = senderServer,
            receiverServerSetupResult = receiverServer
        )

        // Store token A on the receiver side, that will be used by the sender to begin registration and store it as
        // well in the client so that it knows what token to send
        val tokenA = UUID.randomUUID().toString()
        receiverServer.platformCollection.insertOne(Platform(url = senderServer.versionsEndpoint, tokenA = tokenA))
        senderServer.platformCollection.insertOne(Platform(url = receiverServer.versionsEndpoint, tokenA = tokenA))

        // Start the servers
        receiverServer.transport.start()
        senderServer.transport.start()

        runBlocking {
            credentialsClientService.register()
        }

        val versionsClient = VersionsClient(
            transportClientBuilder = Http4kTransportClientBuilder(),
            serverVersionsEndpointUrl = receiverServer.versionsEndpoint,
            platformRepository = PlatformMongoRepository(collection = senderServer.platformCollection)
        )

        expectThat(
            runBlocking {
                versionsClient.getVersions()
            }
        ) {
            get { data }
                .isNotNull()
                .isNotEmpty()
                .isEqualTo(
                    runBlocking {
                        VersionsCacheRepository(baseUrl = receiverServer.transport.baseUrl)
                            .getVersions()
                    }
                )

            get { status_code }
                .isEqualTo(OcpiStatus.SUCCESS.code)
        }

        runBlocking {
            credentialsClientService.delete()
        }

        expectCatching {
            versionsClient.getVersions()
        }.isFailure()
    }

    @Test
    fun `should properly run registration process then get, update, delete properly`() {
        val receiverServer = setupReceiver()
        val senderServer = setupSender()

        val credentialsClientService = setupCredentialsSenderClient(
            senderServerSetupResult = senderServer,
            receiverServerSetupResult = receiverServer
        )

        // Store token A on the receiver side, that will be used by the sender to begin registration and store it as
        // well in the client so that it knows what token to send
        val tokenA = UUID.randomUUID().toString()
        receiverServer.platformCollection.insertOne(Platform(url = senderServer.versionsEndpoint, tokenA = tokenA))
        senderServer.platformCollection.insertOne(Platform(url = receiverServer.versionsEndpoint, tokenA = tokenA))

        // Start the servers
        receiverServer.transport.start()
        senderServer.transport.start()

        val credentialsAfterRegistration = runBlocking { credentialsClientService.register() }

        val versionsClient = VersionsClient(
            transportClientBuilder = Http4kTransportClientBuilder(),
            serverVersionsEndpointUrl = receiverServer.versionsEndpoint,
            platformRepository = PlatformMongoRepository(collection = senderServer.platformCollection)
        )

        expectThat(
            runBlocking {
                versionsClient.getVersions()
            }
        ) {
            get { data }
                .isNotNull()
                .isNotEmpty()
                .isEqualTo(
                    runBlocking {
                        VersionsCacheRepository(baseUrl = receiverServer.transport.baseUrl)
                            .getVersions()
                    }
                )

            get { status_code }
                .isEqualTo(OcpiStatus.SUCCESS.code)
        }

        expectThat(
            runBlocking {
                credentialsClientService.get()
            }
        ).isEqualTo(credentialsAfterRegistration)

        runBlocking {
            credentialsClientService.update()
        }

        expectThat(
            runBlocking {
                versionsClient.getVersions()
            }
        ) {
            get { data }
                .isNotNull()
                .isNotEmpty()
                .isEqualTo(
                    runBlocking {
                        VersionsCacheRepository(baseUrl = receiverServer.transport.baseUrl)
                            .getVersions()
                    }
                )

            get { status_code }
                .isEqualTo(OcpiStatus.SUCCESS.code)
        }

        runBlocking {
            credentialsClientService.delete()
        }

        expectThrows<OcpiClientUnknownTokenException> {
            // no token to use, so we should have an OCPI client error
            runBlocking {
                versionsClient.getVersions()
            }
        }
    }
}
