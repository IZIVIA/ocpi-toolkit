package com.izivia.ocpi.toolkit.samples.common

import com.izivia.ocpi.toolkit.transport.TransportClient
import com.izivia.ocpi.toolkit.transport.TransportClientBuilder

class Http4kTransportClientBuilder : TransportClientBuilder {
    override fun build(url: String): TransportClient =
        com.izivia.ocpi.toolkit.samples.common.Http4kTransportClient(url)
}
