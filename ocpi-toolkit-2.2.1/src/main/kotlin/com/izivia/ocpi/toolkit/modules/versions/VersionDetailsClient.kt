package com.izivia.ocpi.toolkit.modules.versions

import com.izivia.ocpi.toolkit.common.OcpiToolkitUnknownEndpointException
import com.izivia.ocpi.toolkit.common.authenticate
import com.izivia.ocpi.toolkit.common.parseResult
import com.izivia.ocpi.toolkit.common.withRequiredHeaders
import com.izivia.ocpi.toolkit.modules.credentials.repositories.PartnerRepository
import com.izivia.ocpi.toolkit.modules.versions.domain.VersionDetails
import com.izivia.ocpi.toolkit.transport.TransportClientBuilder
import com.izivia.ocpi.toolkit.transport.domain.HttpMethod
import com.izivia.ocpi.toolkit.transport.domain.HttpRequest

/**
 * Used to get the version details of a partner
 * @property transportClientBuilder used to build transport client
 * @property partnerId used to know which partner to communicate with
 * @property partnerRepository used to get information about the partner (endpoint, token)
 */
class VersionDetailsClient(
    private val transportClientBuilder: TransportClientBuilder,
    private val partnerId: String,
    private val partnerRepository: PartnerRepository,
) : VersionDetailsInterface {

    override suspend fun getVersionDetails(): VersionDetails =
        with(
            transportClientBuilder
                .build(
                    baseUrl = partnerRepository
                        .getVersion(partnerId = partnerId)
                        ?.url
                        ?: throw OcpiToolkitUnknownEndpointException("version details"),
                ),
        ) {
            send(
                HttpRequest(method = HttpMethod.GET)
                    .withRequiredHeaders(
                        requestId = generateRequestId(),
                        correlationId = generateCorrelationId(),
                    )
                    .authenticate(
                        partnerRepository = partnerRepository,
                        partnerId = partnerId,
                        allowTokenA = true,
                    ),
            )
                .parseResult()
        }
}
