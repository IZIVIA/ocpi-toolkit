package com.izivia.ocpi.toolkit.modules.locations.domain

import com.fasterxml.jackson.annotation.JsonSetter
import com.fasterxml.jackson.annotation.Nulls
import com.izivia.ocpi.toolkit.annotations.Partial
import com.izivia.ocpi.toolkit.common.CiString
import java.time.Instant

/**
 * A connector is the socket or cable available for the EV to use. A single EVSE may provide multiple connectors but
 * only one of them can be in use at the same time. A connector always belongs to an EVSE object.
 *
 * @property id (max-length=36) Identifier of the connector within the EVSE. Two connectors may have the same id as long
 * as they do not belong to the same EVSE object.
 * @property standard The standard of the installed connector.
 * @property format The format (socket/cable) of the installed connector.
 * @property powerType
 * @property maxVoltage Maximum voltage of the connector (line to neutral for AC_3_PHASE), in volt V. For example: DC
 * Chargers might vary the voltage during charging when battery almost full.
 * @property maxAmperage Maximum amperage of the connector, in ampere A.
 * @property maxElectricPower Maximum electric power that can be delivered by this connector, in Watts (W). When the
 * maximum electric power is lower than the calculated value from voltage and amperage, this value should be set. For
 * example: A DC Charge Point which can delivers up to 920V and up to 400A can be limited to a maximum of 150kW
 * (max_electric_power = 150000). Depending on the car, it may supply max voltage or current, but not both at the same
 * time. For AC Charge Points, the amount of phases used can also have influence on the maximum power.
 * @property tariffIds (max-length=36) Identifier of the current charging tariff structure. For a "Free of Charge"
 * tariff this field should be set, and point to a defined "Free of Charge" tariff.
 * @property termsAndConditions URL (string(255) type following the w3.org spec. to the operator's terms and
 * conditions
 * @property lastUpdated Timestamp when this Connectors was last updated (or created).
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
    @JsonSetter(contentNulls = Nulls.SKIP)
    val tariffIds: List<CiString>? = null,
    val termsAndConditions: String? = null,
    val lastUpdated: Instant,
)
