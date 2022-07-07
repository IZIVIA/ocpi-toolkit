package com.izivia.ocpi.toolkit.modules.tariff.domain

import com.izivia.ocpi.toolkit.annotations.Partial

@Partial
enum class ReservationRestrictionType {
    /**
     *  Used in TariffElements to describe costs for a reservation
     */
    RESERVATION,

    /**
     *Used in TariffElements to describe costs for a reservation that expires (i.e. driver does not start a
    charging session before expiry_date of the reservation).
     */
    RESERVATION_EXPIRES
}
