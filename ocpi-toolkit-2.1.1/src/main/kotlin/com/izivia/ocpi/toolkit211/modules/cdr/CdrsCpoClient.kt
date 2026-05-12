package com.izivia.ocpi.toolkit211.modules.cdr

import com.izivia.ocpi.toolkit.transport.TransportClient
import com.izivia.ocpi.toolkit.transport.domain.HttpMethod
import com.izivia.ocpi.toolkit.transport.domain.HttpRequest
import com.izivia.ocpi.toolkit211.common.*
import com.izivia.ocpi.toolkit211.modules.cdr.domain.Cdr
import com.izivia.ocpi.toolkit211.modules.versions.domain.ModuleID
import com.izivia.ocpi.toolkit211.serialization.mapper
import com.izivia.ocpi.toolkit211.serialization.serializeObject

/**
 * Sends calls to an eMSP server
 */
class CdrsCpoClient(
    private val transportClientBuilder: TransportClientBuilder,
    private val partnerId: String,
) : CdrsEmspInterface<URL> {

    private suspend fun buildTransport(): TransportClient = transportClientBuilder
        .buildFor(
            partnerId = partnerId,
            module = ModuleID.cdrs,
        )

    override suspend fun getCdr(param: URL): Cdr? =
        with(transportClientBuilder.buildFor(partnerId, param)) {
            send(
                HttpRequest(
                    method = HttpMethod.GET,
                    path = "/",
                ),
            ).parseOptionalResult()
        }

    override suspend fun postCdr(cdr: Cdr): URL? =
        with(buildTransport()) {
            send(
                HttpRequest(
                    method = HttpMethod.POST,
                    body = mapper.serializeObject(cdr),
                ),
            )
                .also { it.parseResultOrNull<URL?>() }
                .getHeader(Header.LOCATION)
        }
}
