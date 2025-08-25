package com.izivia.ocpi.toolkit.modules.locations.domain

import com.fasterxml.jackson.annotation.JsonEnumDefaultValue

/**
 * This value, if provided, represents the restriction to the parking spot for different purposes.
 */
enum class ParkingRestriction {
    /**
     *	Reserved parking spot for electric vehicles.
     */
    EV_ONLY,

    /**
     *	Parking is only allowed while plugged in (charging).
     */
    PLUGGED,

    /**
     *	Reserved parking spot for disabled people with valid ID.
     */
    DISABLED,

    /**
     *	Parking spot for customers/guests only, for example in case of a hotel or shop.
     */
    CUSTOMERS,

    /**
     *	Parking spot only suitable for (electric) motorcycles or scooters.
     */
    MOTORCYCLES,

    /**
     * Placeholder entry serving as default value if we can not match with any other.
     * Avoids failing deserialization on invalid entries.
     */
    @JsonEnumDefaultValue
    OTHER,
}
