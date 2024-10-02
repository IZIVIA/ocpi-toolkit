package com.izivia.ocpi.toolkit.modules.commands.domain

import com.izivia.ocpi.toolkit.common.CiString
import com.izivia.ocpi.toolkit.common.URL
import com.izivia.ocpi.toolkit.modules.tokens.domain.Token

/**
 * @property responseUrl (max-length=255) URL that the CommandResult POST should be sent to. This URL might contain a
 * unique ID to be able to distinguish between StartSession requests.
 *
 * @property token Token object the Charge Point has to use to start a new session. The Token provided in this request
 * is authorized by the eMSP.
 *
 * @property locationId (max-length=36) Location.id of the Location (belonging to the CPO this request is sent to) on
 * which a session is to be started.
 *
 * @property evseUid (max-length=36) Optional EVSE.uid of the EVSE of this Location on which a session is to be started.
 * Required when connector_id is set.
 *
 * @property connectorId (max-length=36) Optional Connector.id of the Connector of the EVSE on which a session is to be
 * started. This field is required when the capability: START_SESSION_CONNECTOR_REQUIRED is set on the EVSE.
 *
 * @property authorizationReference (max-length=36) Reference to the authorization given by the eMSP, when given, this
 * reference will be provided in the relevant Session and/or CDR.
 */
data class StartSession(
    val responseUrl: URL,
    val token: Token,
    val locationId: CiString,
    val evseUid: CiString?,
    val connectorId: CiString?,
    val authorizationReference: CiString?,
)
