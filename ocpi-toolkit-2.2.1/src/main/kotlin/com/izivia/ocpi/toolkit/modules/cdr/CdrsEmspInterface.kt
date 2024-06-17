package com.izivia.ocpi.toolkit.modules.cdr

import com.izivia.ocpi.toolkit.common.OcpiResponseBody
import com.izivia.ocpi.toolkit.common.URL
import com.izivia.ocpi.toolkit.modules.cdr.domain.Cdr

/**
 * Typically implemented by market roles like: eMSP.
 *
 * The CDRs endpoint can be used to create and retrieve CDRs.
 *
 * - GET: Retrieve an existing CDR.
 * - POST: Send a new CDR.
 * - PUT: n/a (CDRs cannot be replaced)
 * - PATCH: n/a (CDRs cannot be updated)
 * - DELETE: n/a (CDRs cannot be removed)
 */
interface CdrsEmspInterface {
    /**
     * GET Method
     *
     * Fetch CDRs from the receivers' system.
     *
     * Endpoint structure definition:
     *
     * No structure defined. This is open to the eMSP to define, the URL is provided to the CPO by the eMSP in
     * the result of the POST request. Therefore, OCPI does not define variables.
     *
     * @param cdrId (max-length 36) id of the Cdr object to get from the eMSP’s system.
     * @return Cdr Requested Cdr object.
     */
    suspend fun getCdr(cdrId: CiString): OcpiResponseBody<Cdr?>

    /**
     * POST Method
     *
     * Creates a new CDR.
     *
     * The POST method should contain the full and final CDR object.
     *
     * @param cdr New CDR object.
     * @return The response should contain the URL to the just created CDR object in the eMSP’s system.
     */
    suspend fun postCdr(cdr: Cdr): OcpiResponseBody<URL?>
}
