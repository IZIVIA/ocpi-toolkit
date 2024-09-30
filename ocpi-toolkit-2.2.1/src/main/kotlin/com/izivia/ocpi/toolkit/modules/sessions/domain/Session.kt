package com.izivia.ocpi.toolkit.modules.sessions.domain

import com.izivia.ocpi.toolkit.annotations.Partial
import com.izivia.ocpi.toolkit.common.CiString
import com.izivia.ocpi.toolkit.modules.cdr.domain.AuthMethod
import com.izivia.ocpi.toolkit.modules.cdr.domain.CdrToken
import com.izivia.ocpi.toolkit.modules.cdr.domain.ChargingPeriod
import com.izivia.ocpi.toolkit.modules.types.Price
import java.math.BigDecimal
import java.time.Instant

/**
 * The Session object describes one charging session. That doesn’t mean it is required that energy has been transferred between EV
 * and the Charge Point. It is possible that the EV never took energy from the Charge Point because it was instructed not to take
 * energy by the driver. But as the EV was connected to the Charge Point, some form of start tariff, park tariff or reservation cost might
 * be relevant.
 *
 * NOTE : Although OCPI supports such pricing mechanisms, local laws might not allow this.
 *
 * It is recommended to add enough ChargingPeriods to a Session so that the eMSP is able to provide feedback to the EV driver
 * about the progress of the charging session. The ideal amount of transmitted Charging Periods depends on the charging speed. The
 * Charging Periods should be sufficient for useful feedback but they should not generate too much unneeded traffic either. How many
 * Charging Periods are transmitted is left to the CPO to decide. The following are just some points to consider:
 * • Adding a new Charging Period every minute for an AC charging session can be too much as it will yield 180 Charging
 *  Periods for an (assumed to be) average 3h session.
 * • A new Charging Period every 30 minutes for a DC fast charging session is not enough as it will yield only one Charging
 *  Period for an (assumed to be) average 30min session.
 *
 * It is also recommended to add Charging Periods for all moments that are relevant for the Tariff changes, see CDR object
 * description for more information.
 *
 * For more information about how step_size impacts the calculation of the cost of charging also see the CDR object description.
 *
 * NOTE : Different authorization_reference values might happen when for example a ReserveNow had a different
 * authorization_reference then the value returned by a real-time authorization.
 *
 * @property countryCode (max-length 2) ISO-3166 alpha-2 country code of the MSP that 'owns' this Token.
 * @property partyId (max-length 3) ID of the eMSP that 'owns' this Token (following the ISO-15118 standard).
 * @property id (max-length 36) The unique id that identifies the charging session in the CPO platform.
 * @property startDateTime The timestamp when the session became ACTIVE in the Charge Point. When the session is still
 * PENDING, this field SHALL be set to thetime the Session was created at the Charge Point. When a Session goes from
 * PENDING to ACTIVE, this field SHALL be updated to the moment the Session went to ACTIVE in the Charge Point.
 * @property endDateTime The timestamp when the session was completed/finished, charging might have finished before
 * the session ends, for example: EV is full, but parking cost also has to be paid.
 * @property kwh How many kWh were charged.
 * @property cdrToken Token used to start this charging session, including all the relevant information to identify the unique token.
 * @property authMethod Method used for authentication. This might change during a session, for example when the
 * session was started with a reservation: ReserveNow: COMMAND. When the driver arrives and starts charging using a
 * Token that is whitelisted: WHITELIST.
 * @property authorizationReference (max-length 36) Reference to the authorization given by the eMSP. When the eMSP
 * provided an authorization_reference in either: real-time authorization, StartSession or ReserveNow this field SHALL
 * contain the same value . When different authorization_reference values have been given by the eMSP that are relevant
 * to this Session, the last given value SHALL be used here.
 * @property locationId (max-length 36) Location.id of the Location object of this CO, on which the charging session is/was happening.
 * @property evseUid (max-length 36) EVSE.uid of the EVSE of this Location on which the charging
 * session is/was happening. Allowed to be set to: #NA when this session is created for a reservation, but no EVSE yet
 * assigned to the driver.
 * @property connectorId (max-length 36) Connector.id of the Connector of this Location where the charging
 * session is/was happening. Allowed to be set to: #NA when this session is created for a reservation, but no connector
 * yet assigned to the driver.
 * @property meterId (max-length 255) Optional identification of the kWh meter.
 * @property currency  (max-length 3) ISO 4217 code of the currency used for this session.
 * @property chargingPeriods An optional list of Charging Periods that can be used to calculate
 * and verify the total cost.
 * @property totalCost The total cost of the session in the specified currency. This is the price that the eMSP will
 * have to pay to the CPO. A total_cost of 0.00 means free of charge. When omitted, i.e. no price information is given
 * in the Session object, it does not imply the session is/was free of charge.
 * @property status The status of the session.
 * @property lastUpdated Timestamp when this Session was last updated (or created).
 */

@Partial
data class Session(
    val countryCode: CiString,
    val partyId: CiString,
    val id: CiString,
    val startDateTime: Instant,
    val endDateTime: Instant? = null,
    val kwh: BigDecimal,
    val cdrToken: CdrToken,
    val authMethod: AuthMethod,
    val authorizationReference: CiString? = null,
    val locationId: CiString,
    val evseUid: CiString,
    val connectorId: CiString,
    val meterId: String? = null,
    val currency: CiString,
    val chargingPeriods: List<ChargingPeriod>? = emptyList(),
    val totalCost: Price? = null,
    val status: SessionStatusType,
    val lastUpdated: Instant,
)
