package com.izivia.ocpi.toolkit.modules.tokens.domain

/**
 * Defines when authorization of a Token by the CPO is allowed.
 */
enum class WhitelistType {
    /**
     * Token always has to be whitelisted, realtime authorization is not possible/allowed.
     */
    ALWAYS,

    /**
     * It is allowed to whitelist the token, realtime authorization is also allowed.
     */
    ALLOWED,

    /**
     * Whitelisting is only allowed when CPO cannot reach the eMSP (communication between CPO and eMSP is offline)
     */
    ALLOWED_OFFLINE,

    /**
     * Whitelisting is forbidden, only realtime authorization is allowed. Token should always be authorized by the eMSP.
     */
    NEVER
}
