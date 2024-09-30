package com.izivia.ocpi.toolkit.modules.cdr.domain

import com.izivia.ocpi.toolkit.annotations.Partial
import com.izivia.ocpi.toolkit.common.CiString
import com.izivia.ocpi.toolkit.modules.tariff.domain.Tariff
import com.izivia.ocpi.toolkit.modules.types.Price
import java.math.BigDecimal
import java.time.Instant

/**
 * The CDR object describes the charging session and its costs, how these costs are composed, etc.
 *
 * @property id (max-length=39) Uniquely identifies the CDR within the CPO’s platform (and
suboperator platforms). This field is longer than the usual 36
characters to allow for credit CDRs to have something appended to
the original ID. Normal (non-credit) CDRs SHALL only have an ID
with a maximum length of 36.
 * @property countryCode (max-length=2) ISO-3166 alpha-2 country code of the CPO that 'owns' this CDR.
 * @property partyId (max-length=3) CPO ID of the CPO that 'owns' this CDR (following the ISO-15118
standard).
 * @property startDateTime Start timestamp of the charging session, or in-case of a reservation
(before the start of a session) the start of the reservation.
 * @property endDateTime The timestamp when the session was completed/finished, charging
might have finished before the session ends, for example: EV is full,
but parking cost also has to be paid.
 * @property sessionId (max-length=36) Unique ID of the Session for which this CDR is sent. Is only allowed
to be omitted when the CPO has not implemented the Sessions
module or this CDR is the result of a reservation that never became
a charging session, thus no OCPI Session.
 * @property cdrToken Token used to start this charging session, includes all the relevant
information to identify the unique token.
 * @property authMethod Method used for authentication.
 * @property authorizationReference (max-length=36) Reference to the authorization given by the eMSP. When the eMSP
provided an authorization_reference in either: real-time
authorization or StartSession, this field SHALL contain the same
value. When different authorization_reference values have
been given by the eMSP that are relevant to this Session, the last
given value SHALL be used here.
 * @property cdrLocation Location where the charging session took place, including only the
relevant EVSE and Connector.
 * @property meterId (max-length=255) Identification of the Meter inside the Charge Point.
 * @property currency (max-length=3) Currency of the CDR in ISO 4217 Code.
 * @property tariffs List of relevant Tariff Elements, see: Tariff. When relevant, a Free of
Charge tariff should also be in this list, and point to a defined Free
of Charge Tariff.
 * @property chargingPeriods List of Charging Periods that make up this charging session. A
session consists of 1 or more periods, where each period has a
different relevant Tariff.
 * @property signedData Signed data that belongs to this charging Session
 * @property totalCost Total sum of all the costs of this transaction in the specified
currency
 * @property totalFixedCost Total sum of all the fixed costs in the specified currency, except
fixed price components of parking and reservation. The cost not
depending on amount of time/energy used etc. Can contain costs
like a start tariff.
 * @property totalEnergy Total energy charged, in kWh
 * @property totalEnergyCost Total sum of all the cost of all the energy used, in the specified
currency
 * @property totalTime Total duration of the charging session (including the duration of
charging and not charging), in hours
 * @property totalTimeCost Total sum of all the cost related to duration of charging during this
transaction, in the specified currency.
 * @property totalParkingTime Total duration of the charging session where the EV was not
charging (no energy was transferred between EVSE and EV), in
hours.
 * @property totalParkingCost Total sum of all the cost related to parking of this transaction,
including fixed price components, in the specified currency.
 * @property totalReservationCost Total sum of all the cost related to a reservation of a Charge Point,
including fixed price components, in the specified currency.
 * @property remark (max-length=255) Optional remark, can be used to provide additional human readable
information to the CDR, for example: reason why a transaction was
stopped.
 * @property invoiceReferenceId (max-length=39) This field can be used to reference an invoice, that will later be send
for this CDR. Making it easier to link a CDR to a given invoice.
Maybe even group CDRs that will be on the same invoice.
 * @property credit ? When set to true, this is a Credit CDR, and the field
credit_reference_id needs to be set as well.
 * @property creditReferenceId (max-length=39) Is required to be set for a Credit CDR. This SHALL contain the id
of the CDR for which this is a Credit CDR.
 * @property homeChargingCompensation When set to true, this CDR is for a charging session using the home charger of the
EV Driver for which the energy cost needs to be financially compensated to the EV Driver.
 * @property lastUpdated Timestamp when this CDR was last updated (or created).
 */

@Partial
data class Cdr(
    val id: CiString,
    val countryCode: CiString,
    val partyId: CiString,
    val startDateTime: Instant,
    val endDateTime: Instant,
    val sessionId: CiString? = null,
    val cdrToken: CdrToken,
    val authMethod: AuthMethod,
    val authorizationReference: String? = null,
    val cdrLocation: CdrLocation,
    val meterId: String? = null,
    val currency: String,
    val tariffs: List<Tariff>?,
    val chargingPeriods: List<ChargingPeriod>,
    val signedData: SignedData? = null,
    val totalCost: Price,
    val totalFixedCost: Price? = null,
    val totalEnergy: BigDecimal,
    val totalEnergyCost: Price? = null,
    val totalTime: BigDecimal,
    val totalTimeCost: Price? = null,
    val totalParkingTime: BigDecimal? = null,
    val totalParkingCost: Price? = null,
    val totalReservationCost: Price? = null,
    val remark: String? = null,
    val invoiceReferenceId: CiString? = null,
    val credit: Boolean? = null,
    val creditReferenceId: CiString? = null,
    val homeChargingCompensation: Boolean? = null,
    val lastUpdated: Instant,
)
