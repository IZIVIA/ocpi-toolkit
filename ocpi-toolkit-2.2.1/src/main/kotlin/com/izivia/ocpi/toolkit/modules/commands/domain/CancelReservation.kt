package com.izivia.ocpi.toolkit.modules.commands.domain

import com.izivia.ocpi.toolkit.common.CiString

/**
 * @property responseUrl (max-length=255) URL that the CommandResult POST should be sent to. This URL might contain a
 * unique ID to be able to distinguish between CancelReservation requests.
 *
 * @property reservationId (max-length=36) Reservation id, unique for this reservation. If the Charge Point already has
 * a reservation that matches this reservationId the Charge Point will replace the reservation.
 */
data class CancelReservation(
    val responseUrl: CiString,
    val reservationId: CiString,
)
