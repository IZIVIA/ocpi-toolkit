package com.izivia.ocpi.toolkit.modules.chargingProfiles.domain

enum class ChargingProfileResultType {
    /**
     * ChargingProfile request accepted by the EVSE.
     */
    ACCEPTED,

    /**
     * ChargingProfile request rejected by the EVSE.
     */
    REJECTED,

    /**
     * No Charging Profile(s) were found by the EVSE matching the request.
     */
    UNKNOWN,
}
