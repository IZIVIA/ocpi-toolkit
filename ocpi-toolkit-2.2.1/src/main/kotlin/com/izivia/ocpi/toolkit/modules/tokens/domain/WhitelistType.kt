package com.izivia.ocpi.toolkit.modules.tokens.domain

/**
 * Defines when authorization of a Token by the CPO is allowed.
 * The validity of a Token has no influence on this. If a Token is: valid = false, when the whitelist field requires real-time
 * authorization, the CPO SHALL do a real-time authorization, the state of the Token might have changed.
 */
enum class WhitelistType {
    /**
     * Token always has to be whitelisted, realtime authorization is not possible/allowed. CPO shall
     * always allow any use of this Token.
     */
    ALWAYS,

    /**
     * It is allowed to whitelist the token, realtime authorization is also allowed. The CPO may choose
     * which version of authorization to use.
     */
    ALLOWED,

    /**
     * In normal situations realtime authorization shall be used. But when the CPO cannot get a response
     * from the eMSP (communication between CPO and eMSP is offline), the CPO shall allow this Token
     * to be used.
     */
    ALLOWED_OFFLINE,

    /**
     * Whitelisting is forbidden, only realtime authorization is allowed. CPO shall always send a realtime
     * authorization for any use of this Token to the eMSP.
     */
    NEVER,
}
