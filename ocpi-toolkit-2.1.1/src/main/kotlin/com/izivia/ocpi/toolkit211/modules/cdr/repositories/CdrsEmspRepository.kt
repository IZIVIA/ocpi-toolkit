package com.izivia.ocpi.toolkit211.modules.cdr.repositories

import com.izivia.ocpi.toolkit211.common.URL
import com.izivia.ocpi.toolkit211.modules.cdr.domain.Cdr

interface CdrsEmspRepository {

    suspend fun getCdr(cdrId: String): Cdr?

    suspend fun postCdr(cdr: Cdr): URL?
}
