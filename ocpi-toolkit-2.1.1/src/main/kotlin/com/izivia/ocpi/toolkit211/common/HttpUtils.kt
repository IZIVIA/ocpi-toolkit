package com.izivia.ocpi.toolkit211.common

import com.izivia.ocpi.toolkit.transport.decodeBase64
import com.izivia.ocpi.toolkit.transport.domain.HttpException
import com.izivia.ocpi.toolkit.transport.domain.HttpMethod
import com.izivia.ocpi.toolkit.transport.domain.HttpRequest
import com.izivia.ocpi.toolkit.transport.domain.HttpResponse
import com.izivia.ocpi.toolkit.transport.domain.HttpStatus
import com.izivia.ocpi.toolkit211.modules.credentials.repositories.PartnerRepository
import com.izivia.ocpi.toolkit211.modules.versions.domain.ModuleID

object Header {
    const val AUTHORIZATION = "Authorization"
    const val X_REQUEST_ID = "X-Request-ID"
    const val X_CORRELATION_ID = "X-Correlation-ID"
    const val X_TOTAL_COUNT = "X-Total-Count"
    const val X_LIMIT = "X-Limit"
    const val LINK = "Link"
    const val LOCATION = "Location"
    const val CONTENT_TYPE = "Content-Type"
}

object ContentType {
    const val APPLICATION_JSON = "application/json"
}

/**
 * Returns the value of a header by its key. The key is not case-sensitive.
 */
fun HttpResponse.getHeader(key: String): String? =
    headers.getByNormalizedKey(key)

/**
 *  Returns the value of a map entry by its key. The key is not case-sensitive.
 */
fun Map<String, String>.getByNormalizedKey(key: String): String? =
    mapKeys { it.key.lowercase() }[key.lowercase()]

/**
 * Parses authorization header from the HttpRequest
 *
 * @throws OcpiClientNotEnoughInformationException if the token is missing
 * @throws HttpException if the authorization header is missing
 */
fun HttpRequest.parseAuthorizationHeader() = getHeader(Header.AUTHORIZATION)
    ?.let {
        if (it.startsWith("Token ")) {
            it
        } else {
            throw OcpiClientInvalidParametersException("Unkown token format: $it")
        }
    }
    ?.removePrefix("Token ")
    ?.let {
        try {
            it.decodeBase64()
        } catch (_: Exception) {
            throw HttpException(HttpStatus.BAD_REQUEST, "${Header.AUTHORIZATION} token cannot be decoded")
        }
    }
    ?: throw HttpException(HttpStatus.UNAUTHORIZED, "${Header.AUTHORIZATION} header missing")

/**
 * Throws an exception if the token is invalid. Does nothing otherwise.
 *
 * @throws OcpiClientInvalidParametersException if the token is invalid, otherwise does nothing
 * @throws OcpiClientNotEnoughInformationException if the token is missing
 * @throws HttpException if the authorization header is missing
 *
 */
suspend fun PartnerRepository.checkToken(
    httpRequest: HttpRequest,
) {
    val token = httpRequest.parseAuthorizationHeader()

    /**
     * From OCPI 2.2.1 doc:
     * When a server receives a request with a valid CREDENTIALS_TOKEN_A, on another module than: credentials or
     * versions, the server SHALL respond with an HTTP 401 - Unauthorized status code.
     *
     * So, we allow token A only if we are in this case.
     */

    val allowTokenA = httpRequest.path.contains(ModuleID.versions.name) ||
        httpRequest.path.contains("/{versionNumber}") ||
        httpRequest.path.contains(ModuleID.credentials.name)

    val validToken = (allowTokenA && isCredentialsTokenAValid(token)) ||
        isCredentialsServerTokenValid(token)

    // 7. Credentials module
    // 7.2.3. PUT Method
    // This method MUST return a HTTP status code 405: method not allowed if the client has not been registered yet
    // 7.2.4. DELETE Method
    // This method MUST return a HTTP starting code 405: method not allowed if the client has not been registered before.
    if (!validToken && httpRequest.path.contains(ModuleID.credentials.name) && httpRequest.method in listOf(
            HttpMethod.PUT,
            HttpMethod.DELETE,
        )
    ) {
        throw HttpException(HttpStatus.METHOD_NOT_ALLOWED, "Method not allowed")
    }

    if (!validToken) {
        throw HttpException(HttpStatus.UNAUTHORIZED, "Invalid server token (token A allowed: $allowTokenA)")
    }
}
