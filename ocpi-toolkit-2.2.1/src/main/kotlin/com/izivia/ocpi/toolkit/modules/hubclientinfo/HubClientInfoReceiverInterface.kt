package com.izivia.ocpi.toolkit.modules.hubclientinfo

import com.izivia.ocpi.toolkit.modules.hubclientinfo.domain.ClientInfo

/**
 * Receiver Interface (usually CPO or eMSP)
 *
 * - GET: Retrieve a ClientInfo object as it is stored in the connected clients system.
 * - POST: n/a
 * - PUT: Push new/updated ClientInfo object to the connect client
 * - PATCH: n/a
 * - DELETE: n/a, Use PUT, ClientInfo objects cannot be removed).
 */
interface HubClientInfoReceiverInterface {
    suspend fun get(countryCode: String, partyId: String): ClientInfo?
    suspend fun put(countryCode: String, partyId: String, clientInfo: ClientInfo)
}
