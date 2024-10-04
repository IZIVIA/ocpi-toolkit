package com.izivia.ocpi.toolkit.common

import com.izivia.ocpi.toolkit.transport.domain.HttpRequest

/**
 * Example implementation
 *
 *     httpAuth = { req ->
 *       partnerRepository.getPartnerIdByCredentialsServerToken(req.parseAuthorizationHeader())!!
 *     }
 */
fun interface HttpAuthInterface {
    suspend fun partnerIdFromRequest(req: HttpRequest): String
}
