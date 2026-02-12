package com.izivia.ocpi.toolkit.samples.common

import com.izivia.ocpi.toolkit.common.TransportClientBuilder
import com.izivia.ocpi.toolkit.modules.credentials.services.PartnerProvider
import com.izivia.ocpi.toolkit.modules.versions.domain.InterfaceRole
import com.izivia.ocpi.toolkit.modules.versions.domain.ModuleID
import com.izivia.ocpi.toolkit.transport.AbstractTransportClientBuilder
import com.izivia.ocpi.toolkit.transport.TransportClient
import org.http4k.client.OkHttp
import org.http4k.core.Uri
import org.http4k.core.then
import org.http4k.filter.ClientFilters

class Http4kTransportClientBuilder(
    partnerProvider: PartnerProvider,
) : TransportClientBuilder, AbstractTransportClientBuilder<ModuleID, InterfaceRole>(partnerProvider) {
    override suspend fun build(
        baseUrl: String,
        authToken: String,
    ): TransportClient {
        return Http4kTransportClient(
            client = ClientFilters.SetBaseUriFrom(Uri.of(baseUrl)).then(OkHttp()),
            authToken = authToken,
        )
    }
}
