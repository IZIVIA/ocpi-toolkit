package com.izivia.ocpi.toolkit.modules.commands.domain

import com.izivia.ocpi.toolkit.common.CiString
import com.izivia.ocpi.toolkit.common.URL
import com.izivia.ocpi.toolkit.modules.tokens.domain.Token
import java.time.Instant

/**
 * @property responseUrl (max-length=255) URL that the CommandResult POST should be sent to. This URL might contain a
 * unique ID to be able to distinguish between ReserveNow requests.
 *
 * @property token Token object for how to reserve this Charge Point (and specific EVSE).
 *
 * @property expiryDate The Date/Time when this reservation ends, in UTC.
 *
 * @property reservationId (max-length=36) Reservation id, unique for this reservation. If the Receiver (typically CPO)
 * Point already has a reservation that matches this reservationId for that Location it will replace the reservation.
 *
 * @property locationId (max-length=36) Location.id of the Location (belonging to the CPO this request is sent to) for
 * which to reserve an EVSE.
 *
 * @property evseUid (max-length=36) Optional EVSE.uid of the EVSE of this Location if a specific EVSE has to be
 * reserved.
 *
 * @property authorizationReference (max-length=36) Reference to the authorization given by the eMSP, when given, this
 * reference will be provided in the relevant Session and/or CDR.
 */
data class ReserveNow(
    val responseUrl: URL,
    val token: Token,
    val expiryDate: Instant,
    val reservationId: CiString,
    val locationId: CiString,
    val evseUid: CiString?,
    val authorizationReference: CiString?,
)
