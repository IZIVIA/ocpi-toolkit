package tests

import common.OcpiStatus
import ocpi.credentials.CredentialsClient
import ocpi.credentials.CredentialsServer
import ocpi.credentials.services.CredentialsClientService
import ocpi.credentials.services.CredentialsServerService
import ocpi.versions.VersionsClient
import ocpi.versions.VersionsServer
import ocpi.versions.domain.VersionNumber
import ocpi.versions.validation.VersionsValidationService
import org.junit.jupiter.api.Test
import org.litote.kmongo.getCollection
import samples.common.Http4kTransportClientBuilder
import samples.common.Platform
import samples.common.VersionsCacheRepository
import strikt.api.expectThat
import strikt.assertions.isEqualTo
import strikt.assertions.isNotEmpty
import strikt.assertions.isNotNull
import strikt.assertions.isNull
import tests.mock.PlatformMongoRepository
import java.util.*

class CredentialsIntegrationTests : BaseServerIntegrationTest() {

    @Test
    fun credentialsTest() {
        // Db setup
        val database = buildDBClient().getDatabase("ocpi-2-2-1-tests")
        val receiverPlatformCollection = database.getCollection<Platform>("receiver-server")
        val senderPlatformCollection = database.getCollection<Platform>("sender-client")

        // Setup receiver (only server)
        val receiverServer = buildTransportServer()
        val receiverPlatformRepo = PlatformMongoRepository(collection = receiverPlatformCollection)
        val receiverVersionsCacheRepository = VersionsCacheRepository(baseUrl = receiverServer.baseUrl)
        CredentialsServer(
            transportServer = receiverServer,
            service = CredentialsServerService(
                platformRepository = receiverPlatformRepo,
                transportClientBuilder = Http4kTransportClientBuilder(),
                serverUrl = receiverServer.baseUrl
            )
        )
        VersionsServer(
            transportServer = receiverServer,
            validationService = VersionsValidationService(
                repository = receiverVersionsCacheRepository,
                platformRepository = receiverPlatformRepo
            )
        )
        receiverServer.start()

        // Setup sender (server)
        val senderServer = buildTransportServer()
        val versionsRepo = VersionsCacheRepository(baseUrl = senderServer.baseUrl)
        VersionsServer(
            transportServer = senderServer,
            validationService = VersionsValidationService(
                repository = versionsRepo,
                platformRepository = PlatformMongoRepository(collection = senderPlatformCollection)
            )
        )
        senderServer.start()

        // Setup sender (client)
        val transportTowardsReceiver = receiverServer.getClient()
        val credentialsClientService = CredentialsClientService(
            clientPlatformRepository = PlatformMongoRepository(collection = senderPlatformCollection),
            versionsRepository = versionsRepo,
            credentialsClient = CredentialsClient(transportClient = transportTowardsReceiver),
            versionsClient = VersionsClient(transportClient = transportTowardsReceiver)
        )

        // Store token A on the receiver side, that will be used by the sender to begin registration and store it as
        // well in the client so that it knows what token to send
        val tokenA = UUID.randomUUID().toString()
        receiverPlatformCollection.insertOne(
            Platform(
                url = senderServer.baseUrl,
                tokenA = tokenA
            )
        )
        senderPlatformCollection.insertOne(
            Platform(
                url = receiverServer.baseUrl,
                tokenA = tokenA
            )
        )

        // Now that we have all the prerequisites, we can begin the registration
        val credentials = credentialsClientService.register(
            clientVersionsEndpointUrl = senderServer.baseUrl,
            platformUrl = receiverServer.baseUrl
        )

        // Now we can do some requests to check if the credentials provided are right (and if token A is now invalid)
        val versionsClient = VersionsClient(
            transportClient = receiverServer.getClient()
        )

        expectThat(
            versionsClient.getVersions(
                token = credentials.token
            )
        ) {
            get { data }
                .isNotNull()
                .isNotEmpty()
                .isEqualTo(receiverVersionsCacheRepository.getVersions())

            get { status_code }
                .isEqualTo(OcpiStatus.SUCCESS.code)
        }

        var versionNumber = VersionNumber.V2_2_1
        expectThat(
            versionsClient.getVersionDetails(
                token = credentials.token,
                versionNumber = versionNumber.value
            )
        ) {
            get { data }
                .isNotNull()
                .isEqualTo(receiverVersionsCacheRepository.getVersionDetails(versionNumber))

            get { status_code }
                .isEqualTo(OcpiStatus.SUCCESS.code)
        }

        versionNumber = VersionNumber.V2_2
        expectThat(
            versionsClient.getVersionDetails(
                token = credentials.token,
                versionNumber = versionNumber.value
            )
        ) {
            get { data }
                .isNull()

            get { status_code }
                .isEqualTo(OcpiStatus.SERVER_UNSUPPORTED_VERSION.code)
        }

        expectThat(
            versionsClient.getVersions(
                token = tokenA
            )
        ) {
            get { data }
                .isNull()

            get { status_code }
                .isEqualTo(OcpiStatus.CLIENT_INVALID_PARAMETERS.code)
        }

        versionNumber = VersionNumber.V2_2_1
        expectThat(
            versionsClient.getVersionDetails(
                token = tokenA,
                versionNumber = versionNumber.value
            )
        ) {
            get { data }
                .isNull()

            get { status_code }
                .isEqualTo(OcpiStatus.CLIENT_INVALID_PARAMETERS.code)
        }
    }
}