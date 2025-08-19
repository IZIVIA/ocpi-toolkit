package com.izivia.ocpi.toolkit.modules.cdr.services

import com.izivia.ocpi.toolkit.common.URL
import com.izivia.ocpi.toolkit.common.validation.validateLength
import com.izivia.ocpi.toolkit.common.validation.validateParams
import com.izivia.ocpi.toolkit.common.validation.validateUrl
import com.izivia.ocpi.toolkit.modules.cdr.CdrsEmspInterface
import com.izivia.ocpi.toolkit.modules.cdr.domain.Cdr

class CdrsEmspValidator(
    private val service: CdrsEmspInterface<String>,
) : CdrsEmspInterface<String> {

    override suspend fun getCdr(param: String): Cdr? {
        validateParams {
            validateLength("cdrId", param, 36)
        }

        return service
            .getCdr(param)
            ?.validate()
    }

    override suspend fun postCdr(cdr: Cdr): URL? {
        validateParams {
            cdr.validate()
        }

        return service
            .postCdr(cdr)
            ?.validateUrl()
    }
}
