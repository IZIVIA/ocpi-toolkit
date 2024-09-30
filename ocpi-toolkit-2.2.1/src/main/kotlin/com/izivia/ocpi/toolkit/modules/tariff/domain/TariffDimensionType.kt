package com.izivia.ocpi.toolkit.modules.tariff.domain

enum class TariffDimensionType {
    /**
     * Defined in kWh, step_size multiplier: 1 Wh
     */
    ENERGY,

    /**
     * Flat fee without unit for step_size
     */
    FLAT,

    /**
     * Time not charging: defined in hours, step_size multiplier: 1 second
     */
    PARKING_TIME,

    /**
     * Time charging: defined in hours, step_size multiplier: 1 second
     * Can also be used in combination with a RESERVATION restriction to describe the price of the
     * reservation time.
     */
    TIME,
}
