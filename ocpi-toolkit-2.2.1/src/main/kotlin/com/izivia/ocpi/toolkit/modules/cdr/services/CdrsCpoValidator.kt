package com.izivia.ocpi.toolkit.modules.cdr.services

import com.izivia.ocpi.toolkit.common.SearchResult
import com.izivia.ocpi.toolkit.common.validation.validateDates
import com.izivia.ocpi.toolkit.common.validation.validateInt
import com.izivia.ocpi.toolkit.common.validation.validateParams
import com.izivia.ocpi.toolkit.modules.cdr.CdrsCpoInterface
import com.izivia.ocpi.toolkit.modules.cdr.domain.Cdr
import java.time.Instant

class CdrsCpoValidator(
    private val service: CdrsCpoInterface,
) : CdrsCpoInterface {

    override suspend fun getCdrs(
        dateFrom: Instant?,
        dateTo: Instant?,
        offset: Int,
        limit: Int?,
    ): SearchResult<Cdr> {
        validateParams {
            if (dateTo != null && dateFrom != null) validateDates("dateFrom", dateFrom, "dateTo", dateTo)
            if (limit != null) validateInt("limit", limit, 0, null)
            validateInt("offset", offset, 0, null)
        }

        return service
            .getCdrs(dateFrom, dateTo, offset, limit)
            .also { searchResult ->
                searchResult.list.forEach { cdr -> cdr.validate() }
            }
    }
}
