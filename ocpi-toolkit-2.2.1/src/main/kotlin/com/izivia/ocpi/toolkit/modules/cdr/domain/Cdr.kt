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
 * @property country_code (max-length=2) ISO-3166 alpha-2 country code of the CPO that 'owns' this CDR.
 * @property party_id (max-length=3) CPO ID of the CPO that 'owns' this CDR (following the ISO-15118
standard).
 * @property start_date_time Start timestamp of the charging session, or in-case of a reservation
(before the start of a session) the start of the reservation.
 * @property end_date_time The timestamp when the session was completed/finished, charging
might have finished before the session ends, for example: EV is full,
but parking cost also has to be paid.
 * @property session_id (max-length=36) Unique ID of the Session for which this CDR is sent. Is only allowed
to be omitted when the CPO has not implemented the Sessions
module or this CDR is the result of a reservation that never became
a charging session, thus no OCPI Session.
 * @property cdr_token Token used to start this charging session, includes all the relevant
information to identify the unique token.
 * @property auth_method Method used for authentication.
 * @property authorization_reference (max-length=36) Reference to the authorization given by the eMSP. When the eMSP
provided an authorization_reference in either: real-time
authorization or StartSession, this field SHALL contain the same
value. When different authorization_reference values have
been given by the eMSP that are relevant to this Session, the last
given value SHALL be used here.
 * @property cdr_location Location where the charging session took place, including only the
relevant EVSE and Connector.
 * @property meter_id (max-length=255) Identification of the Meter inside the Charge Point.
 * @property currency (max-length=3) Currency of the CDR in ISO 4217 Code.
 * @property tariffs List of relevant Tariff Elements, see: Tariff. When relevant, a Free of
Charge tariff should also be in this list, and point to a defined Free
of Charge Tariff.
 * @property charging_periods List of Charging Periods that make up this charging session. A
session consists of 1 or more periods, where each period has a
different relevant Tariff.
 * @property signed_data Signed data that belongs to this charging Session
 * @property total_cost Total sum of all the costs of this transaction in the specified
currency
 * @property total_fixed_cost Total sum of all the fixed costs in the specified currency, except
fixed price components of parking and reservation. The cost not
depending on amount of time/energy used etc. Can contain costs
like a start tariff.
 * @property total_energy Total energy charged, in kWh
 * @property total_energy_cost Total sum of all the cost of all the energy used, in the specified
currency
 * @property total_time Total duration of the charging session (including the duration of
charging and not charging), in hours
 * @property total_time_cost Total sum of all the cost related to duration of charging during this
transaction, in the specified currency.
 * @property total_parking_time Total duration of the charging session where the EV was not
charging (no energy was transferred between EVSE and EV), in
hours.
 * @property total_parking_cost Total sum of all the cost related to parking of this transaction,
including fixed price components, in the specified currency.
 * @property total_reservation_cost Total sum of all the cost related to a reservation of a Charge Point,
including fixed price components, in the specified currency.
 * @property remark (max-length=255) Optional remark, can be used to provide additional human readable
information to the CDR, for example: reason why a transaction was
stopped.
 * @property invoice_reference_id (max-length=39) This field can be used to reference an invoice, that will later be send
for this CDR. Making it easier to link a CDR to a given invoice.
Maybe even group CDRs that will be on the same invoice.
 * @property credit ? When set to true, this is a Credit CDR, and the field
credit_reference_id needs to be set as well.
 * @property credit_reference_id (max-length=39) Is required to be set for a Credit CDR. This SHALL contain the id
of the CDR for which this is a Credit CDR.
 * @property last_updated Timestamp when this CDR was last updated (or created).
 */

@Partial
data class Cdr(
    val id: CiString,
    val country_code: CiString,
    val party_id: CiString,
    val start_date_time: Instant,
    val end_date_time: Instant,
    val session_id: CiString? = null,
    val cdr_token: CdrToken,
    val auth_method: AuthMethod,
    val authorization_reference: String? = null,
    val cdr_location: CdrLocation,
    val meter_id: String? = null,
    val currency: String,
    val tariffs: List<Tariff>,
    val charging_periods: List<ChargingPeriod>,
    val signed_data: SignedData? = null,
    val total_cost: Price,
    val total_fixed_cost: Price? = null,
    val total_energy: BigDecimal,
    val total_energy_cost: BigDecimal? = null,
    val total_time: BigDecimal,
    val total_time_cost: Price? = null,
    val total_parking_time: BigDecimal? = null,
    val total_parking_cost: Price? = null,
    val total_reservation_cost: Price? = null,
    val remark: String? = null,
    val invoice_reference_id: CiString? = null,
    val credit: Boolean? = null,
    val credit_reference_id: CiString? = null,
    val last_updated: Instant
)
