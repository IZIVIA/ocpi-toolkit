package com.izivia.ocpi.toolkit.modules.locations.domain

/**
 * The status of an EVSE.
 */
enum class Status {
    /**
     * The EVSE/Connector is able to start a new charging session.
     */
    AVAILABLE,

    /**
     * The EVSE/Connector is not accessible because of a physical barrier, i.e. a car.
     */
    BLOCKED,

    /**
     * The EVSE/Connector is in use.
     */
    CHARGING,

    /**
     * The EVSE/Connector is not yet active, or it is no longer available (deleted).
     */
    INOPERATIVE,

    /**
     * The EVSE/Connector is currently out of order.
     */
    OUTOFORDER,

    /**
     * The EVSE/Connector is planned, will be operating soon
     */
    PLANNED,

    /**
     * The EVSE/Connector/charge point is discontinued/removed.
     */
    REMOVED,

    /**
     * The EVSE/Connector is reserved for a particular EV driver and is unavailable for other drivers.
     */
    RESERVED,

    /**
     * No status information available. (Also used when offline)
     */
    UNKNOWN,
}
