package com.izivia.ocpi.toolkit.modules.versions

import com.izivia.ocpi.toolkit.common.*
import com.izivia.ocpi.toolkit.modules.credentials.repositories.PartnerRepository
import com.izivia.ocpi.toolkit.modules.versions.domain.Version
import com.izivia.ocpi.toolkit.transport.TransportClientBuilder
import com.izivia.ocpi.toolkit.transport.domain.*

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

    override suspend fun getVersions(): OcpiResponseBody<List<Version>> =
        with(
            transportClientBuilder
                .build(
                    baseUrl = partnerRepository
                        .getPartnerUrl(partnerId = partnerId)
                        ?: throw OcpiToolkitUnknownEndpointException("version with no url"),
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
                .also {
                    if (it.status != HttpStatus.OK) throw HttpException(it.status, parseHttpStatus(it.status.code).name)
                }
                .runCatching {
                    this.parseBody<OcpiResponseBody<List<Version>>>()
                }
                .onFailure {
                    throw OcpiToolkitResponseParsingException(partnerId, it)
                }
                .getOrThrow()
        }
}
