package ocpi.locations.domain

/**
 * The capabilities of an EVSE.
 */
enum class Capability {
    /**
     * The EVSE supports charging profiles. Sending Charging Profiles is not yet supported by OCPI.
     */
    CHARGING_PROFILE_CAPABLE,

    /**
     * Charging at this EVSE can be payed with credit card.
     */
    CREDIT_CARD_PAYABLE,

    /**
     * The EVSE can remotely be started/stopped.
     */
    REMOTE_START_STOP_CAPABLE,

    /**
     * The EVSE can be reserved.
     */
    RESERVABLE,

    /**
     * Charging at this EVSE can be authorized with an RFID token
     */
    RFID_READER,

    /**
     * Connectors have mechanical lock that can be requested by the eMSP to be unlocked.
     */
    UNLOCK_CAPABLE
}
