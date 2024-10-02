package com.izivia.ocpi.toolkit.modules.commands.domain

import com.izivia.ocpi.toolkit.common.CiString
import com.izivia.ocpi.toolkit.common.URL

/**
 * @property responseUrl (max-length-255) URL that the CommandResult POST should be sent to. This URL might contain a
 * unique ID to be able to distinguish between StopSession requests.
 *
 * @property sessionId (max-length-36) Session.id of the Session that is requested to be stopped.
 */
data class StopSession(
    val responseUrl: URL,
    val sessionId: CiString,
)
