package com.izivia.ocpi.toolkit.samples.common

import transport.TransportClient
import transport.TransportClientBuilder

class Http4kTransportClientBuilder : TransportClientBuilder() {
    override fun build(url: String): TransportClient =
        com.izivia.ocpi.toolkit.samples.common.Http4kTransportClient(url)
}