package com.izivia.ocpi.toolkit.modules.locations.domain

/**
 * Categories of energy sources.
 */
enum class EnergySourceCategory {
    /**
     * Nuclear power sources.
     */
    NUCLEAR,

    /**
     * All kinds of fossil power sources.
     */
    GENERAL_FOSSIL,

    /**
     * Fossil power from coal.
     */
    COAL,

    /**
     * Fossil power from gas.
     */
    GAS,

    /**
     * All kinds of regenerative power sources.
     */
    GENERAL_GREEN,

    /**
     * Regenerative power from PV.
     */
    SOLAR,

    /**
     * Regenerative power from wind turbines.
     */
    WIND,

    /**
     * Regenerative power from water turbines.
     */
    WATER,
}
