package com.izivia.ocpi.toolkit.samples.common

import com.izivia.ocpi.toolkit.transport.TransportClient
import com.izivia.ocpi.toolkit.transport.TransportClientBuilder

class Http4kTransportClientBuilder : TransportClientBuilder {
    override fun build(baseUrl: String): TransportClient =
        Http4kTransportClient(baseUrl)
}

