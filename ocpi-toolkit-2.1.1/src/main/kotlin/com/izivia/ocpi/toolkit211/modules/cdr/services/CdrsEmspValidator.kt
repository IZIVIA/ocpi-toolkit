package com.izivia.ocpi.toolkit211.modules.cdr.services

import com.izivia.ocpi.toolkit211.common.URL
import com.izivia.ocpi.toolkit211.common.validation.validate
import com.izivia.ocpi.toolkit211.common.validation.validateLength
import com.izivia.ocpi.toolkit211.common.validation.validateUrl
import com.izivia.ocpi.toolkit211.modules.cdr.CdrsEmspInterface
import com.izivia.ocpi.toolkit211.modules.cdr.domain.Cdr

class CdrsEmspValidator(
    private val service: CdrsEmspInterface<String>,
) : CdrsEmspInterface<String> {

    override suspend fun getCdr(param: String): Cdr? {
        validate {
            validateLength("cdrId", param, 36)
        }

        return service.getCdr(param)?.validate()
    }

    override suspend fun postCdr(cdr: Cdr): URL? {
        validate {
            cdr.validate()
        }

        return service.postCdr(cdr)?.validateUrl()
    }
}
