package com.izivia.ocpi.toolkit.modules.versions

import com.izivia.ocpi.toolkit.common.OcpiToolkitUnknownEndpointException
import com.izivia.ocpi.toolkit.common.TransportClientBuilder
import com.izivia.ocpi.toolkit.common.parseResult
import com.izivia.ocpi.toolkit.modules.credentials.repositories.PartnerRepository
import com.izivia.ocpi.toolkit.modules.versions.domain.VersionDetails
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
