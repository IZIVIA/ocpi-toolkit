package com.izivia.ocpi.toolkit.samples.common

import com.izivia.ocpi.toolkit.common.TransportClientBuilder
import com.izivia.ocpi.toolkit.modules.credentials.services.PartnerProvider
import com.izivia.ocpi.toolkit.modules.versions.domain.InterfaceRole
import com.izivia.ocpi.toolkit.modules.versions.domain.ModuleID
import com.izivia.ocpi.toolkit.transport.AbstractTransportClientBuilder
import com.izivia.ocpi.toolkit.transport.TransportClient

class Http4kTransportClientBuilder(
    partnerProvider: PartnerProvider,
) : TransportClientBuilder, AbstractTransportClientBuilder<ModuleID, InterfaceRole>(partnerProvider) {
    override fun build(baseUrl: String): TransportClient = Http4kTransportClient(baseUrl)
}
