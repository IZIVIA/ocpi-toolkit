package com.izivia.ocpi.toolkit.modules.tokens.domain

enum class AllowedType {
    /**
     * This Token is allowed to charge (at this location).
     */
    ALLOWED,

    /**
     * This Token is blocked.
     */
    BLOCKED,

    /**
     * This Token has expired.
     */
    EXPIRED,

    /**
     * This Token belongs to an account that has not enough credits to charge (at the given location).
     */
    NO_CREDIT,

    /**
     * Token is valid, but is not allowed to charge at the given location.
     */
    NOT_ALLOWED,
}
