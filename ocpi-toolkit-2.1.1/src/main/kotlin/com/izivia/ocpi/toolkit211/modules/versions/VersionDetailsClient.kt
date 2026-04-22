package com.izivia.ocpi.toolkit211.modules.versions

import com.izivia.ocpi.toolkit.transport.domain.HttpMethod
import com.izivia.ocpi.toolkit.transport.domain.HttpRequest
import com.izivia.ocpi.toolkit211.common.OcpiToolkitUnknownEndpointException
import com.izivia.ocpi.toolkit211.common.TransportClientBuilder
import com.izivia.ocpi.toolkit211.common.parseResult
import com.izivia.ocpi.toolkit211.modules.credentials.repositories.PartnerRepository
import com.izivia.ocpi.toolkit211.modules.versions.domain.VersionDetails

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
                .buildFor(
                    partnerId = partnerId,
                    baseUrl = partnerRepository
                        .getVersion(partnerId = partnerId)
                        ?.url
                        ?: throw OcpiToolkitUnknownEndpointException("version details"),
                    allowTokenA = true,
                ),
        ) {
            send(
                HttpRequest(method = HttpMethod.GET),
            )
                .parseResult()
        }
}
