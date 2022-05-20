package com.izivia.ocpi.toolkit.modules.locations.domain

/**
 * Reflects the general type of the charge points location. May be used for user information.
 */
enum class LocationType {
    /**
     * Parking in public space.
     */
    ON_STREET,

    /**
     * Multistory car park.
     */
    PARKING_GARAGE,

    /**
     * Multistory car park, mainly underground.
     */
    UNDERGROUND_GARAGE,

    /**
     * A cleared area that is intended for parking vehicles, i.e. at super markets, bars, etc.
     */
    PARKING_LOT,

    /**
     * None of the given possibilities.
     */
    OTHER,

    /**
     * Parking location type is not known by the operator (default).
     */
    UNKNOWN
}