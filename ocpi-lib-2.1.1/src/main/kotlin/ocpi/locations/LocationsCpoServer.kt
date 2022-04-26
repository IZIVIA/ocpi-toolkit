package ocpi.locations

import transport.TransportServer

/**
 * Receives calls from a CPO
 * @property transportClient
 */
class LocationsCpoServer(
    private val transportClient: TransportServer,
    private val callbacks: LocationsCpoInterface
) {

    init {
        // TODO
    }
}