package tests.integration

import com.mongodb.client.MongoCollection
import com.mongodb.client.MongoDatabase
import common.OcpiClientInvalidParametersException
import common.OcpiResponseException
import common.OcpiStatus
import ocpi.credentials.CredentialsClient
import ocpi.credentials.CredentialsServer
import ocpi.credentials.services.CredentialsClientService
import ocpi.credentials.services.CredentialsServerService
import ocpi.locations.domain.BusinessDetails
import ocpi.versions.VersionsClient
import ocpi.versions.VersionsServer
import ocpi.versions.domain.VersionNumber
import ocpi.versions.validation.VersionsValidationService
import org.junit.jupiter.api.Test
import org.litote.kmongo.eq
import org.litote.kmongo.getCollection
import org.litote.kmongo.set
import org.litote.kmongo.setTo
import samples.common.Http4kTransportClientBuilder
import samples.common.Http4kTransportServer
import samples.common.Platform
import samples.common.VersionsCacheRepository
import strikt.api.expectCatching
import strikt.api.expectThat
import strikt.assertions.*
import tests.integration.common.BaseServerIntegrationTest
import tests.integration.mock.PlatformMongoRepository
import java.util.*

class CredentialsIntegrationTests : BaseServerIntegrationTest() {

    data class ServerSetupResult(
        val transport: Http4kTransportServer,
        val platformCollection: MongoCollection<Platform>
    )

    private var database: MongoDatabase? = null

    private fun setupReceiver(): ServerSetupResult {
        if (database == null) database = buildDBClient().getDatabase("ocpi-2-1-1-tests")
        val receiverPlatformCollection = database!!
            .getCollection<Platform>("receiver-server-${UUID.randomUUID()}")

        // Setup receiver (only server)
        val receiverServer = buildTransportServer()
        val receiverPlatformRepo = PlatformMongoRepository(collection = receiverPlatformCollection)
        val receiverVersionsCacheRepository = VersionsCacheRepository(baseUrl = receiverServer.baseUrl)
        CredentialsServer(
            transportServer = receiverServer,
            service = CredentialsServerService(
                platformRepository = receiverPlatformRepo,
                serverBusinessDetails = BusinessDetails(name = "Receiver", website = null, logo = null),
                serverPartyId = "DEF",
                serverCountryCode = "FR",
                transportClientBuilder = Http4kTransportClientBuilder(),
                serverUrl = receiverServer.baseUrl
            )
        )
        VersionsServer(
            transportServer = receiverServer,
            platformRepository = receiverPlatformRepo,
            validationService = VersionsValidationService(
                repository = receiverVersionsCacheRepository
            )
        )

        return ServerSetupResult(
            transport = receiverServer,
            platformCollection = receiverPlatformCollection
        )
    }

    private fun setupSender(): ServerSetupResult {
        if (database == null) database = buildDBClient().getDatabase("ocpi-2-1-1-tests")
        val senderPlatformCollection = database!!
            .getCollection<Platform>("sender-server-${UUID.randomUUID()}")

        // Setup sender (server)
        val senderServer = buildTransportServer()

        VersionsServer(
            transportServer = senderServer,
            platformRepository = PlatformMongoRepository(collection = senderPlatformCollection),
            validationService = VersionsValidationService(
                repository = VersionsCacheRepository(baseUrl = senderServer.baseUrl)
            )
        )

        return ServerSetupResult(
            transport = senderServer,
            platformCollection = senderPlatformCollection
        )
    }

    private fun setupCredentialsSenderClient(
        senderServerSetupResult: ServerSetupResult,
        receiverServerSetupResult: ServerSetupResult
    ): CredentialsClientService {
        // Setup sender (client)
        val transportTowardsReceiver = receiverServerSetupResult.transport.getClient()
        return CredentialsClientService(
            clientVersionsEndpointUrl = senderServerSetupResult.transport.baseUrl,
            clientPlatformRepository = PlatformMongoRepository(collection = senderServerSetupResult.platformCollection),
            clientVersionsRepository = VersionsCacheRepository(baseUrl = senderServerSetupResult.transport.baseUrl),
            clientBusinessDetails = BusinessDetails(name = "Sender", website = null, logo = null),
            clientPartyId = "ABC",
            clientCountryCode = "FR",
            serverUrl = receiverServerSetupResult.transport.baseUrl,
            credentialsClient = CredentialsClient(transportClient = transportTowardsReceiver),
            versionsClient = VersionsClient(
                transportClient = transportTowardsReceiver,
                platformRepository = PlatformMongoRepository(collection = senderServerSetupResult.platformCollection)
            )
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
        receiverServer.platformCollection.insertOne(Platform(url = senderServer.transport.baseUrl, tokenA = tokenA))

        // Start the servers
        receiverServer.transport.start()
        senderServer.transport.start()

        // Fails because the senders does not know the TOKEN_A to send with the request
        expectCatching {
            credentialsClientService.register()
        }.isFailure().isA<OcpiClientInvalidParametersException>()

        receiverServer.platformCollection.deleteOne(Platform::url eq senderServer.transport.baseUrl)
        senderServer.platformCollection.insertOne(Platform(url = receiverServer.transport.baseUrl, tokenA = tokenA))

        // Fails because the receiver does not know the TOKEN_A used by the sender
        expectCatching {
            credentialsClientService.register()
        }
            .isFailure()
            .isA<OcpiResponseException>()
            .get { statusCode }
            .isEqualTo(OcpiStatus.CLIENT_INVALID_PARAMETERS.code)

        receiverServer.platformCollection.deleteOne(Platform::url eq senderServer.transport.baseUrl)
        receiverServer.platformCollection.insertOne(
            Platform(
                url = receiverServer.transport.baseUrl,
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
        receiverServer.platformCollection.insertOne(Platform(url = senderServer.transport.baseUrl, tokenA = tokenA))
        senderServer.platformCollection.insertOne(Platform(url = receiverServer.transport.baseUrl, tokenA = tokenA))

        receiverServer.transport.start()
        senderServer.transport.start()

        // We don't need to register, we will use TOKEN_A for our requests

        val versionsClient = VersionsClient(
            transportClient = receiverServer.transport.getClient(),
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
        receiverServer.platformCollection.insertOne(Platform(url = senderServer.transport.baseUrl, tokenA = tokenA))
        senderServer.platformCollection.insertOne(Platform(url = receiverServer.transport.baseUrl, tokenA = tokenA))

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
        receiverServer.platformCollection.insertOne(Platform(url = senderServer.transport.baseUrl, tokenA = tokenA))
        senderServer.platformCollection.insertOne(Platform(url = receiverServer.transport.baseUrl, tokenA = tokenA))

        // Start the servers
        receiverServer.transport.start()
        senderServer.transport.start()

        credentialsClientService.register()

        val versionsClient = VersionsClient(
            transportClient = receiverServer.transport.getClient(),
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
        receiverServer.platformCollection.insertOne(Platform(url = senderServer.transport.baseUrl, tokenA = tokenA))
        senderServer.platformCollection.insertOne(Platform(url = receiverServer.transport.baseUrl, tokenA = tokenA))

        // Start the servers
        receiverServer.transport.start()
        senderServer.transport.start()

        credentialsClientService.register()

        val versionsClient = VersionsClient(
            transportClient = receiverServer.transport.getClient(),
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

        expectThat(
            versionsClient.getVersions()
        ) {
            get { data }
                .isNull()

            get { status_code }
                .isEqualTo(OcpiStatus.CLIENT_INVALID_PARAMETERS.code)
        }
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
        receiverServer.platformCollection.insertOne(Platform(url = senderServer.transport.baseUrl, tokenA = tokenA))
        senderServer.platformCollection.insertOne(Platform(url = receiverServer.transport.baseUrl, tokenA = tokenA))

        // Start the servers
        receiverServer.transport.start()
        senderServer.transport.start()

        val credentialsAfterRegistration = credentialsClientService.register()

        val versionsClient = VersionsClient(
            transportClient = receiverServer.transport.getClient(),
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

        // Gireve update process (delete then register)
        credentialsClientService.delete()
        val newTokenA = UUID.randomUUID().toString()
        receiverServer.platformCollection.updateOne(Platform::url eq senderServer.transport.baseUrl, set(Platform::tokenA setTo newTokenA))
        senderServer.platformCollection.updateOne(Platform::url eq receiverServer.transport.baseUrl, set(Platform::tokenA setTo newTokenA))
        credentialsClientService.register()

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