package tests.integration

import com.mongodb.client.MongoCollection
import com.mongodb.client.MongoDatabase
import common.OcpiClientInvalidParametersException
import common.OcpiResponseException
import common.OcpiStatus
import ocpi.credentials.CredentialsClient
import ocpi.credentials.CredentialsServer
import ocpi.credentials.domain.BusinessDetails
import ocpi.credentials.domain.CiString
import ocpi.credentials.domain.CredentialRole
import ocpi.credentials.domain.Role
import ocpi.credentials.repositories.CredentialsRoleRepository
import ocpi.credentials.services.CredentialsClientService
import ocpi.credentials.services.CredentialsServerService
import ocpi.versions.VersionsClient
import ocpi.versions.VersionsServer
import ocpi.versions.domain.VersionNumber
import ocpi.versions.validation.VersionsValidationService
import org.junit.jupiter.api.Test
import org.litote.kmongo.eq
import org.litote.kmongo.getCollection
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
        if (database == null) database = buildDBClient().getDatabase("ocpi-2-2-1-tests")
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
                credentialsRoleRepository = object: CredentialsRoleRepository {
                    override fun getCredentialsRoles(): List<CredentialRole> = listOf(
                        CredentialRole(
                            role = Role.EMSP,
                            business_details = BusinessDetails(name = "Receiver", website = null, logo = null),
                            party_id = CiString("DEF"),
                            country_code = CiString("FR")
                        )
                    )
                },
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

        return ServerSetupResult(
            transport = receiverServer,
            platformCollection = receiverPlatformCollection
        )
    }

    private fun setupSender(): ServerSetupResult {
        if (database == null) database = buildDBClient().getDatabase("ocpi-2-2-1-tests")
        val senderPlatformCollection = database!!
            .getCollection<Platform>("sender-server-${UUID.randomUUID()}")

        // Setup sender (server)
        val senderServer = buildTransportServer()

        VersionsServer(
            transportServer = senderServer,
            validationService = VersionsValidationService(
                repository = VersionsCacheRepository(baseUrl = senderServer.baseUrl),
                platformRepository = PlatformMongoRepository(collection = senderPlatformCollection)
            )
        )

        return ServerSetupResult(
            transport = senderServer,
            platformCollection = senderPlatformCollection
        )
    }

    private fun setupCredentialsSenderClient(senderServerSetupResult: ServerSetupResult, receiverServerSetupResult: ServerSetupResult): CredentialsClientService {
        // Setup sender (client)
        val transportTowardsReceiver = receiverServerSetupResult.transport.getClient()
        return CredentialsClientService(
            clientVersionsEndpointUrl = senderServerSetupResult.transport.baseUrl,
            clientPlatformRepository = PlatformMongoRepository(collection = senderServerSetupResult.platformCollection),
            clientVersionsRepository = VersionsCacheRepository(baseUrl = senderServerSetupResult.transport.baseUrl),
            clientCredentialsRoleRepository = object: CredentialsRoleRepository {
                override fun getCredentialsRoles(): List<CredentialRole> = listOf(
                    CredentialRole(
                        role = Role.CPO,
                        business_details = BusinessDetails(name = "Sender", website = null, logo = null),
                        party_id = CiString("ABC"),
                        country_code = CiString("FR")
                    )
                )
            },
            serverUrl = receiverServerSetupResult.transport.baseUrl,
            credentialsClient = CredentialsClient(transportClient = transportTowardsReceiver),
            versionsClient = VersionsClient(transportClient = transportTowardsReceiver)
        )
    }

    @Test
    fun `should properly run registration process then credentials provided are working and the old ones are not`() {
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

        // Now we can do some requests to check if the credentials provided are right (and if token A is now invalid)
        val versionsClient = VersionsClient(
            transportClient = receiverServer.transport.getClient()
        )

        expectThat(
            versionsClient.getVersions(
                token = credentials.token
            )
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

        var versionNumber = VersionNumber.V2_2_1
        expectThat(
            versionsClient.getVersionDetails(
                token = credentials.token,
                versionNumber = versionNumber.value
            )
        ) {
            get { data }
                .isNotNull()
                .isEqualTo(
                    VersionsCacheRepository(baseUrl = receiverServer.transport.baseUrl)
                        .getVersionDetails(versionNumber)
                )

            get { status_code }
                .isEqualTo(OcpiStatus.SUCCESS.code)
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
        receiverServer.platformCollection.insertOne(Platform(url = receiverServer.transport.baseUrl, tokenA = "!$tokenA"))

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
        receiverServer.platformCollection.insertOne(Platform(url = senderServer.transport.baseUrl, tokenA = tokenA))
        senderServer.platformCollection.insertOne(Platform(url = receiverServer.transport.baseUrl, tokenA = tokenA))

        receiverServer.transport.start()
        senderServer.transport.start()

        // We don't need to register, we will use TOKEN_A for our requests

        val versionsClient = VersionsClient(
            transportClient = receiverServer.transport.getClient()
        )

        expectThat(
            versionsClient.getVersions(
                token = tokenA
            )
        ) {
            get { data }
                .isNotNull()
                .isEqualTo(VersionsCacheRepository(baseUrl = receiverServer.transport.baseUrl).getVersions())

            get { status_code }
                .isEqualTo(OcpiStatus.SUCCESS.code)
        }

        expectThat(
            versionsClient.getVersionDetails(
                token = tokenA,
                versionNumber = VersionNumber.V2_2_1.value
            )
        ) {
            get { data }
                .isNotNull()
                .isEqualTo(
                    VersionsCacheRepository(baseUrl = receiverServer.transport.baseUrl)
                        .getVersionDetails(VersionNumber.V2_2_1)
                )

            get { status_code }
                .isEqualTo(OcpiStatus.SUCCESS.code)
        }

        expectThat(
            versionsClient.getVersions(
                token = "!$tokenA"
            )
        ) {
            get { data }
                .isNull()

            get { status_code }
                .isEqualTo(OcpiStatus.CLIENT_INVALID_PARAMETERS.code)
        }

        expectThat(
            versionsClient.getVersionDetails(
                token = tokenA,
                versionNumber = VersionNumber.V2_2.value
            )
        ) {
            get { data }
                .isNull()

            get { status_code }
                .isEqualTo(OcpiStatus.SERVER_UNSUPPORTED_VERSION.code)
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

        val credentials = credentialsClientService.register()

        val updatedCredentials = credentialsClientService.update()

        val versionsClient = VersionsClient(
            transportClient = receiverServer.transport.getClient()
        )

        expectThat(
            versionsClient.getVersions(
                token = updatedCredentials.token
            )
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
            versionsClient.getVersions(
                token = credentials.token
            )
        ) {
            get { data }
                .isNull()

            get { status_code }
                .isEqualTo(OcpiStatus.CLIENT_INVALID_PARAMETERS.code)
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

        val credentials = credentialsClientService.register()

        val versionsClient = VersionsClient(
            transportClient = receiverServer.transport.getClient()
        )

        expectThat(
            versionsClient.getVersions(
                token = credentials.token
            )
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
            versionsClient.getVersions(
                token = credentials.token
            )
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
            transportClient = receiverServer.transport.getClient()
        )

        expectThat(
            versionsClient.getVersions(
                token = credentialsAfterRegistration.token
            )
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

        val updatedCredentials = credentialsClientService.update()

        expectThat(
            versionsClient.getVersions(
                token = updatedCredentials.token
            )
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
            versionsClient.getVersions(
                token = credentialsAfterRegistration.token
            )
        ) {
            get { data }
                .isNull()

            get { status_code }
                .isEqualTo(OcpiStatus.CLIENT_INVALID_PARAMETERS.code)
        }

        credentialsClientService.delete()

        expectThat(
            versionsClient.getVersions(
                token = updatedCredentials.token
            )
        ) {
            get { data }
                .isNull()

            get { status_code }
                .isEqualTo(OcpiStatus.CLIENT_INVALID_PARAMETERS.code)
        }

        expectThat(
            versionsClient.getVersions(
                token = credentialsAfterRegistration.token
            )
        ) {
            get { data }
                .isNull()

            get { status_code }
                .isEqualTo(OcpiStatus.CLIENT_INVALID_PARAMETERS.code)
        }
    }
}