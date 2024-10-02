package com.izivia.ocpi.toolkit.modules.chargingProfiles.domain

enum class ChargingProfileResponseType {
    /**
     * ChargingProfile request accepted by the CPO, request will be forwarded to the EVSE.
     */
    ACCEPTED,

    /**
     * The ChargingProfiles not supported by this CPO, Charge Point, EVSE etc.
     */
    NOT_SUPPORTED,

    /**
     * ChargingProfile request rejected by the CPO. (Session might not be from a customer of the eMSP
     * that send this request)
     */
    REJECTED,

    /**
     * ChargingProfile request rejected by the CPO, requests are send more often then allowed.
     */
    TOO_OFTEN,

    /**
     * The Session in the requested command is not known by this CPO.
     */
    UNKNOWN_SESSION,
}
