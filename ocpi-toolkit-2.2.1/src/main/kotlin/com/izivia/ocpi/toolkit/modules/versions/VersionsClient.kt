package com.izivia.ocpi.toolkit.modules.versions

import com.izivia.ocpi.toolkit.common.OcpiResponseBody
import com.izivia.ocpi.toolkit.common.authenticate
import com.izivia.ocpi.toolkit.common.parseBody
import com.izivia.ocpi.toolkit.common.withDebugHeaders
import com.izivia.ocpi.toolkit.modules.credentials.repositories.PlatformRepository
import com.izivia.ocpi.toolkit.modules.versions.domain.Version
import transport.TransportClientBuilder
import transport.domain.HttpMethod
import transport.domain.HttpRequest

/**
 * Used to get the versions of a platform
 * @property transportClientBuilder used to build transport client
 * @property serverVersionsEndpointUrl used to know which platform to communicate with
 * @property platformRepository used to get information about the platform (token)
 */
class VersionsClient(
    private val transportClientBuilder: TransportClientBuilder,
    private val serverVersionsEndpointUrl: String,
    private val platformRepository: PlatformRepository
) : VersionsInterface {

    override fun getVersions(): OcpiResponseBody<List<Version>> =
        transportClientBuilder
            .build(url = serverVersionsEndpointUrl)
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