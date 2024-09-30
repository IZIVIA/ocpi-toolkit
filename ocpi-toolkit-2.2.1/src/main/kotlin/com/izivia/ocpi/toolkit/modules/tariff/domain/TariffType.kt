package com.izivia.ocpi.toolkit.modules.tariff.domain

enum class TariffType {
    /**
     *Used to describe that a Tariff is valid when ad-hoc payment is used at the Charge Point (for example: Debit or
     *  Credit card payment terminal).
     */
    AD_HOC_PAYMENT,

    /**
     * Used to describe that a Tariff is valid when Charging Preference: CHEAP is set for the session.
     */
    PROFILE_CHEAP,

    /**
     * Used to describe that a Tariff is valid when Charging Preference: FAST is set for the session
     */
    PROFILE_FAST,

    /**
     * Used to describe that a Tariff is valid when Charging Preference: GREEN is set for the session
     */
    PROFILE_GREEN,

    /**
     * Used to describe that a Tariff is valid when using an RFID, without any Charging Preference, or when
     * Charging Preference: REGULAR is set for the session.
     */
    REGULAR,
}
