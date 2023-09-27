package com.izivia.ocpi.toolkit.modules.sessions.domain

enum class SessionStatusType {
    /**
     * The session has been accepted and is active. All pre-conditions were met: Communication
     * between EV and EVSE (for example: cable plugged in correctly), EV or driver is authorized. EV is
     * being charged, or can be charged. Energy is, or is not, being transfered.
     */
    ACTIVE,

    /**
     * The session has been finished successfully. No more modifications will be made to the Session
     * object using this state.
     */
    COMPLETED,

    /**
     * The Session object using this state is declared invalid and will not be billed.
     */
    INVALID,

    /**
     * The session is pending, it has not yet started. Not all pre-conditions are met. This is the initial state.
     * The session might never become an active session.
     */
    PENDING,

    /**
     * The session is started due to a reservation, charging has not yet started. The session might never
     * become an active session.
     */
    RESERVATION,
}
