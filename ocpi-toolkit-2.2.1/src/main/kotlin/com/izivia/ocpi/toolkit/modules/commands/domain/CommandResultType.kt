package com.izivia.ocpi.toolkit.modules.commands.domain

enum class CommandResultType {
    /**
     * Command request accepted by the Charge Point.
     */
    ACCEPTED,

    /**
     * The Reservation has been canceled by the CPO.
     */
    CANCELED_RESERVATION,

    /**
     * EVSE is currently occupied, another session is ongoing. Cannot start a new session
     */
    EVSE_OCCUPIED,

    /**
     * EVSE is currently inoperative or faulted.
     */
    EVSE_INOPERATIVE,

    /**
     * Execution of the command failed at the Charge Point.
     */
    FAILED,

    /**
     * The requested command is not supported by this Charge Point, EVSE etc.
     */
    NOT_SUPPORTED,

    /**
     * Command request rejected by the Charge Point.
     */
    REJECTED,

    /**
     * Command request timeout, no response received from the Charge Point in a reasonable time.
     */
    TIMEOUT,

    /**
     * The Reservation in the requested command is not known by this Charge Point.
     */
    UNKNOWN_RESERVATION,
}
