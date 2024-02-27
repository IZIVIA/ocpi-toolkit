package com.izivia.ocpi.toolkit.common

import com.izivia.ocpi.toolkit.transport.domain.HttpRequest

/**
 * Example implementation
 *
 *     httpAuth = { req ->
 *       partnerRepository.getPartnerUrlByCredentialsServerToken(req.parseAuthorizationHeader())!!
 *     }
 */
fun interface HttpAuthInterface {
    suspend fun partnerUrlFromRequest(req: HttpRequest): String
}
