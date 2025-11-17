package com.izivia.ocpi.toolkit.modules.hubclientinfo.service

import com.izivia.ocpi.toolkit.common.SearchResult
import com.izivia.ocpi.toolkit.common.validation.validate
import com.izivia.ocpi.toolkit.common.validation.validateDates
import com.izivia.ocpi.toolkit.common.validation.validateInt
import com.izivia.ocpi.toolkit.modules.hubclientinfo.HubClientInfoSenderInterface
import com.izivia.ocpi.toolkit.modules.hubclientinfo.domain.ClientInfo
import com.izivia.ocpi.toolkit.modules.hubclientinfo.repositories.HubClientInfoSenderRepository
import java.time.Instant

class HubClientInfoSenderService(
    private val repository: HubClientInfoSenderRepository,
) : HubClientInfoSenderInterface {

    override suspend fun getAll(
        dateFrom: Instant?,
        dateTo: Instant?,
        offset: Int,
        limit: Int?,
    ): SearchResult<ClientInfo> {
        validate {
            if (dateFrom != null && dateTo != null) validateDates("dateFrom", dateFrom, "dateTo", dateTo)
            if (limit != null) validateInt("limit", limit, 0, null)
            validateInt("offset", offset, 0, null)
        }

        return repository.getAll(dateFrom, dateTo, offset, limit)
    }
}
