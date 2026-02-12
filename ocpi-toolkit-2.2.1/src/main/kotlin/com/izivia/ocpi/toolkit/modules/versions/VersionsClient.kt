package com.izivia.ocpi.toolkit.modules.versions

import com.izivia.ocpi.toolkit.common.OcpiToolkitUnknownEndpointException
import com.izivia.ocpi.toolkit.common.TransportClientBuilder
import com.izivia.ocpi.toolkit.common.parseResultList
import com.izivia.ocpi.toolkit.modules.credentials.repositories.PartnerRepository
import com.izivia.ocpi.toolkit.modules.versions.domain.Version
import com.izivia.ocpi.toolkit.transport.domain.HttpMethod
import com.izivia.ocpi.toolkit.transport.domain.HttpRequest

/**
 * Used to get the versions of a partner
 * @property transportClientBuilder used to build transport client
 * @property partnerId used to know which partner to communicate with
 * @property partnerRepository used to get information about the partner (token)
 */
class VersionsClient(
    private val transportClientBuilder: TransportClientBuilder,
    private val partnerId: String,
    private val partnerRepository: PartnerRepository,
) : VersionsInterface {

    override suspend fun getVersions(): List<Version> =
        with(
            transportClientBuilder
                .buildFor(
                    partnerId = partnerId,
                    baseUrl = partnerRepository
                        .getPartnerUrl(partnerId = partnerId)
                        ?: throw OcpiToolkitUnknownEndpointException("version with no url"),
                    allowTokenA = true,
                ),
        ) {
            send(
                HttpRequest(method = HttpMethod.GET),
            )
                .parseResultList<Version>()
        }
}
