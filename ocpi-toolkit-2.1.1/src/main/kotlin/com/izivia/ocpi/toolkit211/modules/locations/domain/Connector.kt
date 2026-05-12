package com.izivia.ocpi.toolkit211.modules.locations.domain

import com.izivia.ocpi.toolkit.annotations.Partial
import com.izivia.ocpi.toolkit211.common.CiString
import java.time.Instant

/**
 * A connector is the socket or cable available for the EV to use. A single EVSE may provide multiple connectors but
 * only one of them can be in use at the same time. A connector always belongs to an EVSE object.
 *
 * @property id (max-length=36) Identifier of the connector within the EVSE.
 * @property standard The standard of the installed connector.
 * @property format The format (socket/cable) of the installed connector.
 * @property powerType
 * @property maxVoltage Maximum voltage of the connector, in volt V.
 * @property maxAmperage Maximum amperage of the connector, in ampere A.
 * @property maxElectricPower Maximum electric power that can be delivered by this connector, in Watts (W).
 * @property tariffId (max-length=36) Optional identifier of the current charging tariff structure.
 * @property termsAndConditions URL to the operator's terms and conditions.
 * @property lastUpdated Timestamp when this Connector was last updated (or created).
 */
@Partial
data class Connector(
    val id: CiString,
    val standard: ConnectorType,
    val format: ConnectorFormat,
    val powerType: PowerType,
    val maxVoltage: Int,
    val maxAmperage: Int,
    val maxElectricPower: Int? = null,
    val tariffId: CiString? = null,
    val termsAndConditions: String? = null,
    val lastUpdated: Instant,
)
