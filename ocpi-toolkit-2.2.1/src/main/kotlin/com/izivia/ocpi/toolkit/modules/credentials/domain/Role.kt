package com.izivia.ocpi.toolkit.modules.credentials.domain

enum class Role {
    /**
     *  Charge Point Operator Role.
     */
    CPO,

    /**
     *  eMobility Service Provider Role.
     */
    EMSP,

    /**
     *  Hub role.
     */
    HUB,

    /**
     *  National Access Point Role (national Database with all Location information of a country).
     */
    NAP,

    /**
     *  Navigation Service Provider Role, role like an eMSP (probably only interested in Location information).
     */
    NSP,

    /**
     *  Other role.
     */
    OTHER,

    /**
     *  Smart Charging Service Provider Role
     */
    SCSP,
}
