package com.izivia.ocpi.toolkit211.modules.cdr

import com.izivia.ocpi.toolkit211.common.URL
import com.izivia.ocpi.toolkit211.modules.cdr.domain.Cdr

/**
 * Typically implemented by market roles like: eMSP.
 *
 * - GET: Retrieve an existing CDR.
 * - POST: Send a new CDR.
 */
interface CdrsEmspInterface<T> {

    suspend fun getCdr(param: T): Cdr?

    suspend fun postCdr(cdr: Cdr): URL?
}
