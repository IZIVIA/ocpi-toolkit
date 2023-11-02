package com.izivia.ocpi.toolkit.modules.versions

import com.izivia.ocpi.toolkit.common.*
import com.izivia.ocpi.toolkit.modules.credentials.repositories.PlatformRepository
import com.izivia.ocpi.toolkit.modules.versions.domain.VersionDetails
import com.izivia.ocpi.toolkit.transport.TransportClientBuilder
import com.izivia.ocpi.toolkit.transport.domain.HttpMethod
import com.izivia.ocpi.toolkit.transport.domain.HttpRequest

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

    override suspend fun getVersionDetails(): OcpiResponseBody<VersionDetails> =
        with(
            transportClientBuilder
                .build(
                    baseUrl = platformRepository
                        .getVersion(platformUrl = serverVersionsEndpointUrl)
                        ?.url
                        ?: throw OcpiToolkitUnknownEndpointException("version details")
                )
        ) {
            send(
                HttpRequest(method = HttpMethod.GET)
                    .withRequiredHeaders(
                        requestId = generateRequestId(),
                        correlationId = generateCorrelationId()
                    )
                    .authenticate(
                        platformRepository = platformRepository,
                        platformUrl = serverVersionsEndpointUrl,
                        allowTokenA = true
                    )
            )
                .parseBody()
        }
}
