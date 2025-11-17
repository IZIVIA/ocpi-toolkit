package com.izivia.ocpi.toolkit.modules.hubclientinfo.service

import com.izivia.ocpi.toolkit.common.validation.validate
import com.izivia.ocpi.toolkit.common.validation.validateLength
import com.izivia.ocpi.toolkit.modules.hubclientinfo.HubClientInfoReceiverInterface
import com.izivia.ocpi.toolkit.modules.hubclientinfo.domain.ClientInfo
import com.izivia.ocpi.toolkit.modules.hubclientinfo.repositories.HubClientInfoReceiverRepository

class HubClientInfoReceiverService(
    private val repository: HubClientInfoReceiverRepository,
) : HubClientInfoReceiverInterface {

    override suspend fun get(countryCode: String, partyId: String): ClientInfo? {
        validate {
            validateLength("countryCode", countryCode, 2)
            validateLength("partyId", partyId, 3)
        }

        return repository.get(countryCode, partyId)
    }

    override suspend fun put(
        countryCode: String,
        partyId: String,
        clientInfo: ClientInfo,
    ) {
        validate {
            validateLength("countryCode", countryCode, 2)
            validateLength("partyId", partyId, 3)
        }

        return repository.put(countryCode, partyId, clientInfo)
    }
}
