package com.izivia.ocpi.toolkit.common

import com.fasterxml.jackson.core.type.TypeReference
import com.izivia.ocpi.toolkit.common.Header.OCPI_FROM_COUNTRY_CODE
import com.izivia.ocpi.toolkit.common.Header.OCPI_FROM_PARTY_ID
import com.izivia.ocpi.toolkit.common.Header.OCPI_TO_COUNTRY_CODE
import com.izivia.ocpi.toolkit.common.Header.OCPI_TO_PARTY_ID
import com.izivia.ocpi.toolkit.common.context.*
import com.izivia.ocpi.toolkit.common.validation.validate
import com.izivia.ocpi.toolkit.common.validation.validateLength
import com.izivia.ocpi.toolkit.modules.credentials.repositories.PartnerRepository
import com.izivia.ocpi.toolkit.modules.versions.domain.ModuleID
import com.izivia.ocpi.toolkit.transport.TransportClient
import com.izivia.ocpi.toolkit.transport.TransportClientBuilder
import com.izivia.ocpi.toolkit.transport.domain.*
import java.util.*

typealias AuthenticatedHttpRequest = HttpRequest

object Header {
    const val AUTHORIZATION = "Authorization"
    const val X_REQUEST_ID = "X-Request-ID"
    const val X_CORRELATION_ID = "X-Correlation-ID"
    const val X_TOTAL_COUNT = "X-Total-Count"
    const val X_LIMIT = "X-Limit"
    const val LINK = "Link"
    const val CONTENT_TYPE = "Content-Type"
    const val OCPI_TO_PARTY_ID = "OCPI-to-party-id"
    const val OCPI_TO_COUNTRY_CODE = "OCPI-to-country-code"
    const val OCPI_FROM_PARTY_ID = "OCPI-from-party-id"
    const val OCPI_FROM_COUNTRY_CODE = "OCPI-from-country-code"
}

object ContentType {
    const val APPLICATION_JSON = "application/json"
}

fun Map<String, String>.validateMessageRoutingHeaders() {
    validate {
        validateLength(OCPI_TO_PARTY_ID, getByNormalizedKey(OCPI_TO_PARTY_ID).orEmpty(), 3)
        validateLength(OCPI_TO_COUNTRY_CODE, getByNormalizedKey(OCPI_TO_COUNTRY_CODE).orEmpty(), 2)
        validateLength(OCPI_FROM_PARTY_ID, getByNormalizedKey(OCPI_FROM_PARTY_ID).orEmpty(), 3)
        validateLength(OCPI_FROM_COUNTRY_CODE, getByNormalizedKey(OCPI_FROM_COUNTRY_CODE).orEmpty(), 2)
    }
}

/**
 * Parse body of a paginated request. The result will be stored in a SearchResult which contains all pagination
 * information.
 * @param offset
 */
inline fun <reified T> HttpResponse.parsePaginatedBody(offset: Int): OcpiResponseBody<SearchResult<T>> =
    mapper.readValue(body, object : TypeReference<OcpiResponseBody<List<T>>>() {})
        .let { parsedBody ->
            OcpiResponseBody(
                data = parsedBody.data?.toSearchResult(
                    totalCount = getHeader(Header.X_TOTAL_COUNT)?.toInt()
                        ?: throw OcpiToolkitMissingRequiredResponseHeaderException(Header.X_TOTAL_COUNT),
                    limit = getHeader(Header.X_LIMIT)?.toInt()
                        ?: throw OcpiToolkitMissingRequiredResponseHeaderException(Header.X_LIMIT),
                    offset = offset,
                    nextPageUrl = getHeader(Header.LINK)?.split("<")?.elementAtOrNull(1)?.split(">")?.first(),
                ),
                statusCode = parsedBody.statusCode,
                statusMessage = parsedBody.statusMessage,
                timestamp = parsedBody.timestamp,
            )
        }

/**
 * Parses the body of the given HttpResponse to the specified type
 *
 * @throws StreamReadException – if underlying input contains invalid content of type JsonParser supports (JSON for
 * default case)
 * @throws DatabindException – if the input JSON structure does not match structure expected for result type (or has
 * other mismatch issues)
 */
inline fun <reified T> HttpResponse.parseBody(): T = mapper.readValue(body!!, object : TypeReference<T>() {})

/**
 * Encode a string in base64, also @see String#decodeBase64()
 */
fun String.encodeBase64(): String = Base64.getEncoder().encodeToString(this.encodeToByteArray())

/**
 * Decodes a base64-encoded string, also @see String#encodeBase64()
 */
fun String.decodeBase64(): String = Base64.getDecoder().decode(this).decodeToString()

/**
 * Creates the authorization header from the given token
 */
fun authorizationHeader(token: String): Pair<String, String> = Header.AUTHORIZATION to "Token ${token.encodeBase64()}"

/**
 * Creates the authorization header by taking the client token (or the token A if allowed) in the partner repository
 *
 * @receiver PlatformRepository used to retrieve tokens
 * @param partnerId partner identifier
 * @param allowTokenA only true when called on versions / credentials module
 * @return Pair<String, String>
 */
suspend fun PartnerRepository.buildAuthorizationHeader(
    partnerId: String,
    allowTokenA: Boolean = false,
): Pair<String, String> =
    if (allowTokenA) {
        getCredentialsClientToken(partnerId = partnerId)
            ?: getCredentialsTokenA(partnerId = partnerId)
            ?: throw throw OcpiClientUnknownTokenException(
                "Could not find token A or client token associated with partner $partnerId",
            )
    } else {
        getCredentialsClientToken(partnerId = partnerId)
            ?: throw throw OcpiClientUnknownTokenException(
                "Could not find client token associated with partner $partnerId",
            )
    }
        .let { token -> authorizationHeader(token = token) }

/**
 * Adds the authorization header to the request by taking the client token (or the token A if allowed) in the partner
 * repository.
 *
 * @param partnerRepository use to retrieve tokens
 * @param partnerId partner identifier
 * @param allowTokenA only true when called on versions / credentials module
 */
suspend fun HttpRequest.authenticate(
    partnerRepository: PartnerRepository,
    partnerId: String,
    allowTokenA: Boolean = false,
): AuthenticatedHttpRequest =
    withHeaders(
        headers = headers.plus(
            partnerRepository.buildAuthorizationHeader(
                partnerId = partnerId,
                allowTokenA = allowTokenA,
            ),
        ),
    )

/**
 * Adds the authentification header to the request.
 *
 * @param token the token to use to authenticate
 */
fun HttpRequest.authenticate(token: String): AuthenticatedHttpRequest =
    withHeaders(headers = headers.plus(authorizationHeader(token = token)))

/**
 * It adds Content-Type header as "application/json" if the body is not null.
 */
private fun HttpRequest.withContentTypeHeaderIfNeeded(): HttpRequest =
    if (body != null) {
        withHeaders(headers = headers.plus(Header.CONTENT_TYPE to ContentType.APPLICATION_JSON))
    } else {
        this
    }

/**
 * It adds message routing header if they are set in the current coroutine context
 */
private suspend fun HttpRequest.withRequestMessageRoutingHeadersIfPresent(): HttpRequest {
    val requestMessageRoutingHeaders = currentRequestMessageRoutingHeadersOrNull()

    return if (requestMessageRoutingHeaders != null) {
        withHeaders(headers = headers.plus(requestMessageRoutingHeaders.httpHeaders()))
    } else {
        this
    }
}

/**
 * It builds MessageRoutingHeaders from the headers of the request.
 */
fun HttpRequest.messageRoutingHeaders(): RequestMessageRoutingHeaders =
    RequestMessageRoutingHeaders(
        toPartyId = headers.getByNormalizedKey(OCPI_TO_PARTY_ID),
        toCountryCode = headers.getByNormalizedKey(OCPI_TO_COUNTRY_CODE),
        fromPartyId = headers.getByNormalizedKey(OCPI_FROM_PARTY_ID),
        fromCountryCode = headers.getByNormalizedKey(OCPI_FROM_COUNTRY_CODE),
    )

/**
 * It builds headers from a ResponseMessageRoutingHeaders
 */
private fun RequestMessageRoutingHeaders.httpHeaders(): Map<String, String> =
    mapOf(
        OCPI_TO_PARTY_ID to toPartyId,
        OCPI_TO_COUNTRY_CODE to toCountryCode,
        OCPI_FROM_PARTY_ID to fromPartyId,
        OCPI_FROM_COUNTRY_CODE to fromCountryCode,
    )
        .filter { it.value != null }
        .mapValues { it.value!! }

fun ResponseMessageRoutingHeaders.httpHeaders(): Map<String, String> =
    mapOf(
        OCPI_TO_PARTY_ID to toPartyId,
        OCPI_TO_COUNTRY_CODE to toCountryCode,
        OCPI_FROM_PARTY_ID to fromPartyId,
        OCPI_FROM_COUNTRY_CODE to fromCountryCode,
    )
        .filter { it.value != null }
        .mapValues { it.value!! }

/**
 * It builds TokenHeader from the header of the request (it can be used in coroutine context)
 */
fun HttpRequest.extractTokenHeader(): TokenHeader =
    TokenHeader(
        token = parseAuthorizationHeader(),
    )

/**
 * For debugging issues, OCPI implementations are required to include unique IDs via HTTP headers in every
 * request/response.
 *
 * - X-Request-ID: Every request SHALL contain a unique request ID, the response to this request SHALL contain the same
 * ID.
 * - X-Correlation-ID: Every request/response SHALL contain a unique correlation ID, every response to this request
 * SHALL contain the same ID.
 *
 * Moreover, for requests, Content-Type SHALL be set to application/json for any request that contains a
 * message body: POST, PUT and PATCH. When no body is present, probably in a GET or DELETE, then the Content-Type
 * header MAY be omitted.
 *
 * This method should be called when doing the first request from a client.
 *
 * Dev note: When the server does a request (not a response), it must keep the same X-Correlation-ID but generate a new
 * X-Request-ID. So don't call this method in that case.
 */
suspend fun HttpRequest.withRequiredHeaders(
    requestId: String,
    correlationId: String,
): HttpRequest =
    withHeaders(
        headers = headers
            .plus(Header.X_REQUEST_ID to requestId)
            .plus(Header.X_CORRELATION_ID to correlationId),
    )
        .withContentTypeHeaderIfNeeded()
        .withRequestMessageRoutingHeadersIfPresent()

/**
 * For debugging issues, OCPI implementations are required to include unique IDs via HTTP headers in every
 * request/response
 *
 * - X-Request-ID: Every request SHALL contain a unique request ID, the response to this request SHALL contain the same
 * ID.
 * - X-Correlation-ID: Every request/response SHALL contain a unique correlation ID, every response to this request
 * SHALL contain the same ID.
 *
 * Moreover, for requests, Content-Type SHALL be set to application/json for any request that contains a
 * message body: POST, PUT and PATCH. When no body is present, probably in a GET or DELETE, then the Content-Type
 * header MAY be omitted.
 *
 * This method should be called when doing the a request from a server.
 *
 * @param headers Headers of the caller. It will re-use the X-Correlation-ID header and regenerate X-Request-ID
 */
fun HttpRequest.withUpdatedRequiredHeaders(
    headers: Map<String, String>,
    generatedRequestId: String,
): HttpRequest =
    withHeaders(
        headers = headers
            .plus(Header.X_REQUEST_ID to generatedRequestId) // it replaces existing X_REQUEST_ID header
            .plus(
                Header.X_CORRELATION_ID to (
                    headers.getByNormalizedKey(Header.X_CORRELATION_ID)
                        ?: "error - could not get ${Header.X_CORRELATION_ID} header"
                    ),
            ),
    ).withContentTypeHeaderIfNeeded()

/**
 * For debugging issues, OCPI implementations are required to include unique IDs via HTTP headers in every
 * request/response
 *
 * - X-Request-ID: Every request SHALL contain a unique request ID, the response to this request SHALL contain the same
 * ID.
 * - X-Correlation-ID: Every request/response SHALL contain a unique correlation ID, every response to this request
 * SHALL contain the same ID.
 *
 * This method should be called when responding to a request from a client.
 */
fun HttpRequest.getDebugHeaders() = listOfNotNull(
    getHeader(Header.X_REQUEST_ID)?.let { Header.X_REQUEST_ID to it },
    getHeader(Header.X_CORRELATION_ID)?.let { Header.X_CORRELATION_ID to it },
).toMap()

/**
 * Returns the value of a header by its key. The key is not case-sensitive.
 */
fun HttpRequest.getHeader(key: String): String? =
    headers.getByNormalizedKey(key)

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
        } catch (e: Exception) {
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
    // This method MUST return a HTTP status code 405: method not allowed if the client has not been registered before.
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

suspend fun TransportClientBuilder.buildFor(
    module: ModuleID,
    partnerId: String,
    partnerRepository: PartnerRepository,
): TransportClient =
    partnerRepository
        .getEndpoints(partnerId = partnerId)
        .find { it.identifier == module }
        ?.let { build(baseUrl = it.url) }
        ?: throw OcpiToolkitUnknownEndpointException(endpointName = module.name)
