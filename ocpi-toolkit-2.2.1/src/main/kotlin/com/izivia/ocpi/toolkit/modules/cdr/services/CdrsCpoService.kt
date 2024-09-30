package com.izivia.ocpi.toolkit.modules.cdr.services

import com.izivia.ocpi.toolkit.common.OcpiResponseBody
import com.izivia.ocpi.toolkit.common.SearchResult
import com.izivia.ocpi.toolkit.common.validation.validate
import com.izivia.ocpi.toolkit.common.validation.validateDates
import com.izivia.ocpi.toolkit.common.validation.validateInt
import com.izivia.ocpi.toolkit.modules.cdr.CdrsCpoInterface
import com.izivia.ocpi.toolkit.modules.cdr.domain.Cdr
import com.izivia.ocpi.toolkit.modules.cdr.repositories.CdrsCpoRepository
import java.time.Instant

class CdrsCpoService(
    private val service: CdrsCpoRepository,
) : CdrsCpoInterface {
    override suspend fun getCdrs(
        dateFrom: Instant?,
        dateTo: Instant?,
        offset: Int,
        limit: Int?,
    ): OcpiResponseBody<SearchResult<Cdr>> = OcpiResponseBody.of {
        validate {
            if (dateTo != null && dateFrom != null) validateDates("dateFrom", dateFrom, "dateTo", dateTo)
            if (limit != null) validateInt("limit", limit, 0, null)
            validateInt("offset", offset, 0, null)
        }
        service.getCdrs(dateFrom, dateTo, offset, limit)
            .also { searchResult -> searchResult.list.forEach() { cdr -> cdr.validate() } }
    }
}
