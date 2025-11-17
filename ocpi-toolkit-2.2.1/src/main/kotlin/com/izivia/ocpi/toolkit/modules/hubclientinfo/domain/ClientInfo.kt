package com.izivia.ocpi.toolkit.modules.hubclientinfo.domain

import com.izivia.ocpi.toolkit.common.CiString
import com.izivia.ocpi.toolkit.modules.credentials.domain.Role
import java.time.Instant

/**
 * @property countryCode Country code of the country this party is operating in, as used in the credentials exchange.
 * @property partyId CPO or eMSP ID of this party (following the 15118 ISO standard), as used in the credentials exchange.
 * @property role The role of the connected party.
 * @property status Status of the connection to the party.
 * @property lastUpdated Timestamp when this ClientInfo object was last updated.
 */
data class ClientInfo(
    val countryCode: CiString,
    val partyId: CiString,
    val role: Role,
    val status: ConnectionStatus,
    val lastUpdated: Instant,
)
