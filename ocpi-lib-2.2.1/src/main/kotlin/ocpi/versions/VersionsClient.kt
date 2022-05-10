package ocpi.versions

import common.OcpiResponseBody
import common.authenticate
import common.parseBody
import common.withDebugHeaders
import ocpi.credentials.repositories.PlatformRepository
import ocpi.versions.domain.Version
import ocpi.versions.domain.VersionDetails
import transport.TransportClient
import transport.domain.HttpMethod
import transport.domain.HttpRequest

class VersionsClient(
    private val transportClient: TransportClient,
    private val platformRepository: PlatformRepository
) : VersionsInterface {

    override fun getVersions(): OcpiResponseBody<List<Version>> =
        transportClient
            .send(
                HttpRequest(
                    method = HttpMethod.GET,
                    path = "/"
                )
                    .withDebugHeaders()
                    .authenticate(
                        platformRepository = platformRepository,
                        baseUrl = transportClient.baseUrl,
                        allowTokenA = true
                    )
            )
            .parseBody()

    override fun getVersionDetails(versionNumber: String): OcpiResponseBody<VersionDetails> =
        transportClient
            .send(
                HttpRequest(
                    method = HttpMethod.GET,
                    path = "/$versionNumber"
                )
                    .withDebugHeaders()
                    .authenticate(
                        platformRepository = platformRepository,
                        baseUrl = transportClient.baseUrl,
                        allowTokenA = true
                    )
            )
            .parseBody()
}