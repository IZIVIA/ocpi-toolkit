package com.izivia.ocpi.toolkit.modules.cdr.services

import com.izivia.ocpi.toolkit.common.OcpiResponseBody
import com.izivia.ocpi.toolkit.common.URL
import com.izivia.ocpi.toolkit.common.validation.validate
import com.izivia.ocpi.toolkit.common.validation.validateLength
import com.izivia.ocpi.toolkit.common.validation.validateUrl
import com.izivia.ocpi.toolkit.modules.cdr.CdrsEmspInterface
import com.izivia.ocpi.toolkit.modules.cdr.domain.Cdr
import com.izivia.ocpi.toolkit.modules.cdr.repositories.CdrsEmspRepository

class CdrsEmspService(
    private val service: CdrsEmspRepository,
) : CdrsEmspInterface<String> {
    /**
     * GET Method
     *
     * Fetch CDRs from the receivers' system.
     *
     * Endpoint structure definition:
     *
     * @param param Id of the requested Cdr
     * @return Cdr Requested Cdr object.
     */
    override suspend fun getCdr(param: String): OcpiResponseBody<Cdr?> = OcpiResponseBody.of {
        validate {
            validateLength("cdrId", param, 36)
        }
        service.getCdr(param)?.validate()
    }

    override suspend fun postCdr(cdr: Cdr): OcpiResponseBody<URL?> = OcpiResponseBody.of {
        validate {
            cdr.validate()
        }
        service.postCdr(cdr)?.validateUrl()
    }
}
