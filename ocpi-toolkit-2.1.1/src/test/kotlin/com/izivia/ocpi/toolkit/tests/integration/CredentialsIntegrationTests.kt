package com.izivia.ocpi.toolkit.tests.integration

import com.izivia.ocpi.toolkit.common.OcpiClientInvalidParametersException
import com.izivia.ocpi.toolkit.common.OcpiResponseException
import com.izivia.ocpi.toolkit.common.OcpiStatus
import com.izivia.ocpi.toolkit.modules.credentials.CredentialsServer
import com.izivia.ocpi.toolkit.modules.credentials.services.CredentialsClientService
import com.izivia.ocpi.toolkit.modules.credentials.services.CredentialsServerService
import com.izivia.ocpi.toolkit.modules.locations.domain.BusinessDetails
import com.izivia.ocpi.toolkit.modules.versions.VersionDetailsServer
import com.izivia.ocpi.toolkit.modules.versions.VersionsClient
import com.izivia.ocpi.toolkit.modules.versions.VersionsServer
import com.izivia.ocpi.toolkit.modules.versions.validation.VersionDetailsValidationService
import com.izivia.ocpi.toolkit.modules.versions.validation.VersionsValidationService
import com.izivia.ocpi.toolkit.samples.common.*
import com.izivia.ocpi.toolkit.tests.integration.common.BaseServerIntegrationTest
import com.izivia.ocpi.toolkit.tests.integration.mock.PlatformMongoRepository
import com.mongodb.client.MongoCollection
import com.mongodb.client.MongoDatabase
import org.junit.jupiter.api.Test
import org.litote.kmongo.eq
import org.litote.kmongo.getCollection
import strikt.api.expectCatching
import strikt.api.expectThat
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
        if (database == null) database = buildDBClient().getDatabase("ocpi-2-1-1-tests")
        val receiverPlatformCollection = database!!
            .getCollection<Platform>("receiver-server-${UUID.randomUUID()}")

        // Setup receiver (only server)
        val receiverServer = buildTransportServer()
        val receiverServerVersionsUrl = "${receiverServer.baseUrl}/versions"
        val receiverPlatformRepo = PlatformMongoRepository(collection = receiverPlatformCollection)
        val receiverVersionsCacheRepository = VersionsCacheRepository(baseUrl = receiverServer.baseUrl)
        val receiverVersionDetailsCacheRepository = VersionDetailsCacheRepository(baseUrl = receiverServer.baseUrl)
        CredentialsServer(
            transportServer = receiverServer,
            service = CredentialsServerService(
                platformRepository = receiverPlatformRepo,
                serverBusinessDetails = BusinessDetails(name = "Receiver", website = null, logo = null),
                serverPartyId = "DEF",
                serverCountryCode = "FR",
                transportClientBuilder = Http4kTransportClientBuilder(),
                serverVersionsUrl = receiverServerVersionsUrl
            )
        )
        VersionsServer(
            transportServer = receiverServer,
            platformRepository = receiverPlatformRepo,
            validationService = VersionsValidationService(
                repository = receiverVersionsCacheRepository
            )
        )
        VersionDetailsServer(
            transportServer = receiverServer,
            platformRepository = receiverPlatformRepo,
            validationService = VersionDetailsValidationService(
                repository = receiverVersionDetailsCacheRepository
            )
        )

        return ServerSetupResult(
            transport = receiverServer,
            platformCollection = receiverPlatformCollection,
            versionsEndpoint = receiverServerVersionsUrl
        )
    }

    private fun setupSender(): ServerSetupResult {
        if (database == null) database = buildDBClient().getDatabase("ocpi-2-1-1-tests")
        val senderPlatformCollection = database!!
            .getCollection<Platform>("sender-server-${UUID.randomUUID()}")

        // Setup sender (server)
        val senderServer = buildTransportServer()
        val senderServerVersionsUrl = "${senderServer.baseUrl}/versions"

        VersionsServer(
            transportServer = senderServer,
            platformRepository = PlatformMongoRepository(collection = senderPlatformCollection),
            validationService = VersionsValidationService(
                repository = VersionsCacheRepository(baseUrl = senderServer.baseUrl)
            )
        )
        VersionDetailsServer(
            transportServer = senderServer,
            platformRepository = PlatformMongoRepository(collection = senderPlatformCollection),
            validationService = VersionDetailsValidationService(
                repository = VersionDetailsCacheRepository(baseUrl = senderServer.baseUrl)
            )
        )

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
            clientBusinessDetails = BusinessDetails(name = "Sender", website = null, logo = null),
            clientPartyId = "ABC",
            clientCountryCode = "FR",
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
    fun `should access versions module properly with token A`() {
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
            versionsClient.getVersions()
        ) {
            get { data }
                .isNotNull()
                .isEqualTo(VersionsCacheRepository(baseUrl = receiverServer.transport.baseUrl).getVersions())

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

        val credentials = credentialsClientService.register()

        expectThat(
            credentialsClientService.get()
        ).isEqualTo(credentials)
    }

    @Test
    fun `should properly run registration process then run update properly`() {
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
                    VersionsCacheRepository(baseUrl = receiverServer.transport.baseUrl)
                        .getVersions()
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

        credentialsClientService.register()

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
                    VersionsCacheRepository(baseUrl = receiverServer.transport.baseUrl)
                        .getVersions()
                )

            get { status_code }
                .isEqualTo(OcpiStatus.SUCCESS.code)
        }

        credentialsClientService.delete()

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

        val credentialsAfterRegistration = credentialsClientService.register()

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
                    VersionsCacheRepository(baseUrl = receiverServer.transport.baseUrl)
                        .getVersions()
                )

            get { status_code }
                .isEqualTo(OcpiStatus.SUCCESS.code)
        }

        expectThat(
            credentialsClientService.get()
        ).isEqualTo(credentialsAfterRegistration)

        credentialsClientService.update()

        expectThat(
            versionsClient.getVersions()
        ) {
            get { data }
                .isNotNull()
                .isNotEmpty()
                .isEqualTo(
                    VersionsCacheRepository(baseUrl = receiverServer.transport.baseUrl)
                        .getVersions()
                )

            get { status_code }
                .isEqualTo(OcpiStatus.SUCCESS.code)
        }

        credentialsClientService.delete()

        expectThat(
            versionsClient.getVersions()
        ) {
            get { data }
                .isNull()

            get { status_code }
                .isEqualTo(OcpiStatus.CLIENT_INVALID_PARAMETERS.code)
        }
    }
}