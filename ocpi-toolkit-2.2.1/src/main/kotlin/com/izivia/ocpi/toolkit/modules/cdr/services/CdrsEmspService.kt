package com.izivia.ocpi.toolkit.modules.cdr.services

import com.izivia.ocpi.toolkit.common.CiString
import com.izivia.ocpi.toolkit.common.OcpiResponseBody
import com.izivia.ocpi.toolkit.common.URL
import com.izivia.ocpi.toolkit.common.validation.isUrl
import com.izivia.ocpi.toolkit.common.validation.validate
import com.izivia.ocpi.toolkit.common.validation.validateLength
import com.izivia.ocpi.toolkit.modules.cdr.CdrsEmspInterface
import com.izivia.ocpi.toolkit.modules.cdr.domain.Cdr
import com.izivia.ocpi.toolkit.modules.cdr.repositories.CdrsEmspRepository

class CdrsEmspService(
    private val service: CdrsEmspRepository
) : CdrsEmspInterface {
    override suspend fun getCdr(cdrId: CiString): OcpiResponseBody<Cdr?> = OcpiResponseBody.of {
        validate {
            validateLength("cdrId", cdrId, 39)
        }
        service
            .getCdr(cdrId)
            ?.validate()
    }

    override suspend fun postCdr(cdr: Cdr): OcpiResponseBody<URL?> = OcpiResponseBody.of {
        validate {
            cdr.validate()
        }
        service.postCdr(cdr)?.isUrl()
    }
}
