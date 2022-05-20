package samples.common

import transport.TransportClient
import transport.TransportClientBuilder

class Http4kTransportClientBuilder : TransportClientBuilder() {
    override fun build(url: String): TransportClient =
        Http4kTransportClient(url)
}