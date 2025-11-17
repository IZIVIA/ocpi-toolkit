package com.izivia.ocpi.toolkit.modules.hubclientinfo.domain

enum class ConnectionStatus {
    /**
     * Party is connected.
     */
    CONNECTED,

    /**
     * Party is currently not connected.
     */
    OFFLINE,

    /**
     * Connection to this party is planned, but has never been connected.
     */
    PLANNED,

    /**
     * Party is now longer active, will never connect anymore.
     */
    SUSPENDED,

    /**
     * Placeholder entry serving as default value if we can not match with any other.
     * Avoids failing deserialization on invalid entries.
     */
    OTHER,
}
