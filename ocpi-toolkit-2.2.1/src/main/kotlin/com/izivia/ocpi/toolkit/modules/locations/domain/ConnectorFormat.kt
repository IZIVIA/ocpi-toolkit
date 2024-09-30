package com.izivia.ocpi.toolkit.modules.locations.domain

/**
 * The format of the connector, whether it is a socket or a plug.
 */
enum class ConnectorFormat {
    /**
     * 	The connector is a socket; the EV user needs to bring a fitting plug.
     */
    SOCKET,

    /**
     * 	The connector is an attached cable; the EV users car needs to have a fitting inlet.
     */
    CABLE,
}
