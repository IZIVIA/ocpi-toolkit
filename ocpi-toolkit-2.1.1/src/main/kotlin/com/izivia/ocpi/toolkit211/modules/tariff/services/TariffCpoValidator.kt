package com.izivia.ocpi.toolkit211.modules.tariff.services

import com.izivia.ocpi.toolkit211.common.SearchResult
import com.izivia.ocpi.toolkit211.common.validation.validate
import com.izivia.ocpi.toolkit211.common.validation.validateDates
import com.izivia.ocpi.toolkit211.common.validation.validateInt
import com.izivia.ocpi.toolkit211.modules.tariff.TariffCpoInterface
import com.izivia.ocpi.toolkit211.modules.tariff.domain.Tariff
import java.time.Instant

class TariffCpoValidator(
    private val service: TariffCpoInterface,
) : TariffCpoInterface {

    override suspend fun getTariffs(
        dateFrom: Instant?,
        dateTo: Instant?,
        offset: Int,
        limit: Int?,
    ): SearchResult<Tariff> {
        validate {
            if (dateFrom != null && dateTo != null) validateDates("dateFrom", dateFrom, "dateTo", dateTo)
            if (limit != null) validateInt("limit", limit, 0, null)
            validateInt("offset", offset, 0, null)
        }

        return service
            .getTariffs(dateFrom, dateTo, offset, limit)
            .also { searchResult ->
                searchResult.list.forEach { tariff -> tariff.validate() }
            }
    }
}
