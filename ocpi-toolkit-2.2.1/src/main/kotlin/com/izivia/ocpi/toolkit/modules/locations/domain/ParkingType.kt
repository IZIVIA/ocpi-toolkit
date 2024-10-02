package com.izivia.ocpi.toolkit.modules.locations.domain

/**
 * Reflects the general type of the charge points location. May be used for user information.
 */
enum class ParkingType {
    /**
     * Location on a parking facility/rest area along a motorway, freeway, interstate, highway etc.
     */
    ALONG_MOTORWAY,

    /**
     * Multistory car park.
     */
    PARKING_GARAGE,

    /**
     * A cleared area that is intended for parking vehicles, i.e. at super markets, bars, etc.
     */
    PARKING_LOT,

    /**
     * Location is on the driveway of a house/building.
     */
    ON_DRIVEWAY,

    /**
     * Parking in public space.
     */
    ON_STREET,

    /**
     * Multistory car park, mainly underground.
     */
    UNDERGROUND_GARAGE,
}
