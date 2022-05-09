package ocpi.locations.domain

import io.github.quatresh.annotations.Partial
import java.time.Instant

/**
 * A connector is the socket or cable available for the EV to use. A single EVSE may provide multiple connectors but
 * only one of them can be in use at the same time. A connector always belongs to an EVSE object.
 *
 * @property id (max-length=36) Identifier of the connector within the EVSE. Two connectors may have the same id as long
 * as they do not belong to the same EVSE object.
 * @property standard The standard of the installed connector.
 * @property format The format (socket/cable) of the installed connector.
 * @property power_type
 * @property voltage Voltage of the connector (line to neutral for AC_3_PHASE), in volt V.
 * @property amperage maximum amperage of the connector, in ampere A.
 * @property tariff_id (max-length=36) Identifier of the current charging tariff structure. For a "Free of Charge"
 * tariff this field should be set, and point to a defined "Free of Charge" tariff.
 * @property terms_and_conditions URL (string(255) type following the w3.org spec. to the operator's terms and
 * conditions
 * @property last_updated Timestamp when this Connectors was last updated (or created).
 */
@Partial
data class Connector(
    val id: String,
    val standard: ConnectorType,
    val format: ConnectorFormat,
    val power_type: PowerType,
    val voltage: Int,
    val amperage: Int,
    val tariff_id: String?,
    val terms_and_conditions: String,
    val last_updated: Instant
)