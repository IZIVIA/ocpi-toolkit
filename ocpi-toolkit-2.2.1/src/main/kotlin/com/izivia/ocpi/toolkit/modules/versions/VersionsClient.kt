package com.izivia.ocpi.toolkit.modules.versions

import com.izivia.ocpi.toolkit.common.OcpiResponseBody
import com.izivia.ocpi.toolkit.common.authenticate
import com.izivia.ocpi.toolkit.common.parseBody
import com.izivia.ocpi.toolkit.common.withRequiredHeaders
import com.izivia.ocpi.toolkit.modules.credentials.repositories.PartnerRepository
import com.izivia.ocpi.toolkit.modules.versions.domain.Version
import com.izivia.ocpi.toolkit.transport.TransportClientBuilder
import com.izivia.ocpi.toolkit.transport.domain.HttpMethod
import com.izivia.ocpi.toolkit.transport.domain.HttpRequest

/**
 * Used to get the versions of a partner
 * @property transportClientBuilder used to build transport client
 * @property serverVersionsEndpointUrl used to know which partner to communicate with
 * @property partnerRepository used to get information about the partner (token)
 */
class VersionsClient(
    private val transportClientBuilder: TransportClientBuilder,
    private val serverVersionsEndpointUrl: String,
    private val partnerRepository: PartnerRepository
) : VersionsInterface {

    override suspend fun getVersions(): OcpiResponseBody<List<Version>> =
        with(
            transportClientBuilder
                .build(baseUrl = serverVersionsEndpointUrl)
        ) {
            send(
                HttpRequest(method = HttpMethod.GET)
                    .withRequiredHeaders(
                        requestId = generateRequestId(),
                        correlationId = generateCorrelationId()
                    )
                    .authenticate(
                        partnerRepository = partnerRepository,
                        partnerUrl = serverVersionsEndpointUrl,
                        allowTokenA = true
                    )
            )
                .parseBody()
        }
}
