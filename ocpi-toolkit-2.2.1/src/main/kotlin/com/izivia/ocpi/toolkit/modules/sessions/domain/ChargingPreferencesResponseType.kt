package com.izivia.ocpi.toolkit.modules.sessions.domain

enum class ChargingPreferencesResponseType {
    /**
     * Charging Preferences accepted, EVSE will try to accomplish them, although
     * this is no guarantee that they will be fulfilled.
     */
    ACCEPTED,

    /**
     * CPO requires departure_time to be able to perform Charging Preference
     * based Smart Charging.
     */
    DEPARTURE_REQUIRED,

    /**
     * CPO requires energy_need to be able to perform Charging Preference based
     * Smart Charging.
     */
    ENERGY_NEED_REQUIRED,

    /**
     * Charging Preferences contain a demand that the EVSE knows it cannot fulfill.
     */
    NOT_POSSIBLE,

    /**
     * profile_type contains a value that is not supported by the EVSE.
     */
    PROFILE_TYPE_NOT_SUPPORTED,
}
