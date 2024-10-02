package com.izivia.ocpi.toolkit.modules.commands.domain

import com.izivia.ocpi.toolkit.common.CiString
import com.izivia.ocpi.toolkit.common.URL

/**
 * @property responseUrl (max-length=255) URL that the CommandResult POST should be sent to. This URL might contain a
 * unique ID to be able to distinguish between UnlockConnector requests.
 *
 * @property locationId (max-length=36) Location.id of the Location (belonging to the CPO this request is sent to) of
 * which it is requested to unlock the connector.
 *
 * @property evseUid (max-length=36) EVSE.uid of the EVSE of this Location of which it is requested to unlock the
 * connector.
 *
 * @property connectorId (max-length=36) Connector.id of the Connector of this Location of which it is requested to
 * unlock.
 */
data class UnlockConnector(
    val responseUrl: URL,
    val locationId: CiString,
    val evseUid: CiString,
    val connectorId: CiString,
)
