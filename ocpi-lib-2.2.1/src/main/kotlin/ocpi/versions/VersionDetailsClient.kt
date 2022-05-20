package ocpi.versions

import common.*
import ocpi.credentials.repositories.PlatformRepository
import ocpi.versions.domain.VersionDetails
import transport.TransportClientBuilder
import transport.domain.HttpMethod
import transport.domain.HttpRequest

/**
 * Used to get the version details of a platform
 * @property transportClientBuilder used to build transport client
 * @property serverVersionsEndpointUrl used to know which platform to communicate with
 * @property platformRepository used to get information about the platform (endpoint, token)
 */
class VersionDetailsClient(
    private val transportClientBuilder: TransportClientBuilder,
    private val serverVersionsEndpointUrl: String,
    private val platformRepository: PlatformRepository
) : VersionDetailsInterface {

    override fun getVersionDetails(): OcpiResponseBody<VersionDetails> =
        transportClientBuilder
            .build(
                url = platformRepository
                    .getVersion(platformUrl = serverVersionsEndpointUrl)
                    ?.url
                    ?: throw OcpiToolkitUnknownEndpointException("version details")
            )
            .send(
                HttpRequest(method = HttpMethod.GET)
                    .withDebugHeaders()
                    .authenticate(
                        platformRepository = platformRepository,
                        baseUrl = serverVersionsEndpointUrl,
                        allowTokenA = true
                    )
            )
            .parseBody()
}