package com.izivia.ocpi.toolkit.modules.locations.domain

enum class PowerType {
    /**
     * 	AC mono phase.
     */
    AC_1_PHASE,

    /**
     * AC two phases, only two of the three available phases connected.
     */
    AC_2_PHASE,

    /**
     * AC two phases using split phase system.
     */
    AC_2_PHASE_SPLIT,

    /**
     * 	AC 3 phases.
     */
    AC_3_PHASE,

    /**
     * 	Direct Current.
     */
    DC,
}
