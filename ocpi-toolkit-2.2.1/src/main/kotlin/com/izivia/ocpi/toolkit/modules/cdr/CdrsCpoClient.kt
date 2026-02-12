package com.izivia.ocpi.toolkit.modules.cdr

import com.izivia.ocpi.toolkit.common.*
import com.izivia.ocpi.toolkit.modules.cdr.domain.Cdr
import com.izivia.ocpi.toolkit.modules.versions.domain.InterfaceRole
import com.izivia.ocpi.toolkit.modules.versions.domain.ModuleID
import com.izivia.ocpi.toolkit.serialization.mapper
import com.izivia.ocpi.toolkit.serialization.serializeObject
import com.izivia.ocpi.toolkit.transport.TransportClient
import com.izivia.ocpi.toolkit.transport.domain.HttpMethod
import com.izivia.ocpi.toolkit.transport.domain.HttpRequest

/**
 * Sends calls to an eMSP server
 * @property transportClientBuilder used to build transport client
 * @property partnerId used to know which partner to communicate with
 */
class CdrsCpoClient(
    private val transportClientBuilder: TransportClientBuilder,
    private val partnerId: String,
) : CdrsEmspInterface<URL> {
    private suspend fun buildTransport(): TransportClient = transportClientBuilder
        .buildFor(
            partnerId = partnerId,
            module = ModuleID.cdrs,
            role = InterfaceRole.RECEIVER,
        )

    override suspend fun getCdr(param: URL): Cdr? =
        with(transportClientBuilder.buildFor(partnerId, param)) {
            send(
                HttpRequest(
                    method = HttpMethod.GET,
                    path = "/",
                ),
            )
                .parseOptionalResult()
        }

    override suspend fun postCdr(cdr: Cdr): URL? =
        with(buildTransport()) {
            send(
                HttpRequest(
                    method = HttpMethod.POST,
                    body = mapper.serializeObject(cdr),
                ),
            )
                .also {
                    // this will check response for errors and throw exceptions where applicable
                    it.parseResultOrNull<URL?>()
                }
                // https://github.com/ocpi/ocpi/blob/v2.2.1-d2/mod_cdrs.asciidoc#response-headers
                .getHeader(Header.LOCATION)
        }
}
