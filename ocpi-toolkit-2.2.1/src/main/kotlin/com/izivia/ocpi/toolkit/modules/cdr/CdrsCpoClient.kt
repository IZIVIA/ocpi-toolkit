package com.izivia.ocpi.toolkit.modules.cdr

import com.izivia.ocpi.toolkit.common.*
import com.izivia.ocpi.toolkit.modules.cdr.domain.Cdr
import com.izivia.ocpi.toolkit.modules.credentials.repositories.PartnerRepository
import com.izivia.ocpi.toolkit.modules.versions.domain.ModuleID
import com.izivia.ocpi.toolkit.transport.TransportClient
import com.izivia.ocpi.toolkit.transport.TransportClientBuilder
import com.izivia.ocpi.toolkit.transport.domain.HttpMethod
import com.izivia.ocpi.toolkit.transport.domain.HttpRequest

/**
 * Sends calls to an eMSP server
 * @property transportClientBuilder used to build transport client
 * @property partnerId used to know which partner to communicate with
 * @property partnerRepository used to get information about the partner (endpoint, token)
 */
class CdrsCpoClient(
    private val transportClientBuilder: TransportClientBuilder,
    private val partnerId: String,
    private val partnerRepository: PartnerRepository,
) : CdrsEmspInterface<URL> {
    private suspend fun buildTransport(): TransportClient = transportClientBuilder
        .buildFor(
            module = ModuleID.cdrs,
            partnerId = partnerId,
            partnerRepository = partnerRepository,
        )

    override suspend fun getCdr(param: URL): Cdr? =
        with(transportClientBuilder.build(param)) {
            send(
                HttpRequest(
                    method = HttpMethod.GET,
                    path = "/",
                ).withRequiredHeaders(
                    requestId = generateRequestId(),
                    correlationId = generateCorrelationId(),
                )
                    .authenticate(partnerRepository = partnerRepository, partnerId = partnerId),
            )
                .parseOptionalResult()
        }

    override suspend fun postCdr(cdr: Cdr): URL? =
        with(buildTransport()) {
            send(
                HttpRequest(
                    method = HttpMethod.POST,
                    body = mapper.writeValueAsString(cdr),
                ).withRequiredHeaders(
                    requestId = generateRequestId(),
                    correlationId = generateCorrelationId(),
                )
                    .authenticate(partnerRepository = partnerRepository, partnerId = partnerId),
            )
                .also {
                    // this will check response for errors and throw exceptions where applicable
                    it.parseResultOrNull<URL?>()
                }
                // https://github.com/ocpi/ocpi/blob/v2.2.1-d2/mod_cdrs.asciidoc#response-headers
                .getHeader(Header.LOCATION)
        }
}
