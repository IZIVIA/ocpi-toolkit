package com.izivia.ocpi.toolkit.common

import com.fasterxml.jackson.core.type.TypeReference
import com.izivia.ocpi.toolkit.modules.credentials.repositories.PlatformRepository
import com.izivia.ocpi.toolkit.modules.versions.domain.ModuleID
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
}

object ContentType {
    const val APPLICATION_JSON = "application/json"
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
                    totalCount = getHeader(Header.X_TOTAL_COUNT)!!.toInt(),
                    limit = getHeader(Header.X_LIMIT)!!.toInt(),
                    offset = offset,
                    nextPageUrl = getHeader(Header.LINK)?.split("<")?.get(1)?.split(">")?.first()
                ),
                status_code = parsedBody.status_code,
                status_message = parsedBody.status_message,
                timestamp = parsedBody.timestamp
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
inline fun <reified T> HttpResponse.parseBody(): T = mapper.readValue(body!!, object: TypeReference<T>() {})

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
 * Creates the authorization header by taking the right token in the platform repository
 */
suspend fun PlatformRepository.buildAuthorizationHeader(baseUrl: String, allowTokenAOrTokenB: Boolean = false) =
    if (allowTokenAOrTokenB) {
        getCredentialsTokenC(platformUrl = baseUrl)
            ?: getCredentialsTokenB(platformUrl = baseUrl)
            ?: getCredentialsTokenA(platformUrl = baseUrl)
            ?: throw throw OcpiClientGenericException(
                "Could not find CREDENTIALS TOKEN A OR B OR C associated with platform $baseUrl"
            )
    } else {
        getCredentialsTokenC(platformUrl = baseUrl)
            ?: throw throw OcpiClientGenericException(
                "Could not find CREDENTIALS TOKEN C associated with platform $baseUrl"
            )
    }.let { token -> authorizationHeader(token = token) }

/**
 * Adds the authentification header to the request.
 *
 * @param platformRepository use to retrieve tokens
 * @param baseUrl used to know what platform is being requested
 * @param allowTokenAOrTokenB true if we can authenticate using token A
 */
suspend fun HttpRequest.authenticate(
    platformRepository: PlatformRepository,
    baseUrl: String,
    allowTokenAOrTokenB: Boolean = false
): AuthenticatedHttpRequest =
    withHeaders(
        headers = headers.plus(
            platformRepository.buildAuthorizationHeader(
                baseUrl = baseUrl,
                allowTokenAOrTokenB = allowTokenAOrTokenB
            )
        )
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
    withHeaders(
        headers = if (body != null) {
            headers.plus(Header.CONTENT_TYPE to ContentType.APPLICATION_JSON)
        } else {
            headers
        }
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
fun HttpRequest.withRequiredHeaders(): HttpRequest =
    withHeaders(
        headers = headers
            .plus(Header.X_REQUEST_ID to generateUUIDv4Token())
            .plus(Header.X_CORRELATION_ID to generateUUIDv4Token())
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
 * Moreover, for requests, Content-Type SHALL be set to application/json for any request that contains a
 * message body: POST, PUT and PATCH. When no body is present, probably in a GET or DELETE, then the Content-Type
 * header MAY be omitted.
 *
 * This method should be called when doing the a request from a server.
 *
 * TODO: test debug headers with integration tests
 *
 * @param headers Headers of the caller. It will re-use the X-Correlation-ID header and regenerate X-Request-ID
 */
fun HttpRequest.withUpdatedRequiredHeaders(headers: Map<String, String>): HttpRequest =
    withHeaders(
        headers = headers
            .plus(Header.X_REQUEST_ID to generateUUIDv4Token())
            .plus(
                Header.X_CORRELATION_ID to (
                    headers.getByNormalizedKey(Header.X_CORRELATION_ID)
                        ?: "error - could not get ${Header.X_CORRELATION_ID} header"
                    )
            )
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
    getHeader(Header.X_CORRELATION_ID)?.let { Header.X_CORRELATION_ID to it }
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
        if (it.startsWith("Token ")) it
        else throw OcpiClientInvalidParametersException("Unkown token format: $it")
    }
    ?.removePrefix("Token ")
    ?.decodeBase64()
    ?: throw HttpException(HttpStatus.UNAUTHORIZED, "${Header.AUTHORIZATION} header missing")

/**
 * Throws an exception if the token is invalid. Does nothing otherwise.
 *
 * TODO: is it the good behaviour given:
 * - tokenA: Valid in receiver context, during sender registration (only for sender -> receiver calls)
 * - tokenB: Valid in sender context, during sender registration (only for receiver -> sender calls)
 * - tokenC: Valid when the sender is registered with the receiver (only for sender -> receiver)
 *
 * @throws OcpiClientInvalidParametersException if the token is invalid, otherwise does nothing
 * @throws OcpiClientNotEnoughInformationException if the token is missing
 * @throws HttpException if the authorization header is missing
 */
suspend fun PlatformRepository.tokenFilter(httpRequest: HttpRequest) {
    val token = httpRequest.parseAuthorizationHeader()

    if (!platformExistsWithTokenA(token) &&
        !platformExistsWithTokenB(token) &&
        getPlatformUrlByTokenC(token) == null
    ) {
        throw OcpiClientInvalidParametersException("Invalid token: $token")
    }
}

suspend fun TransportClientBuilder.buildFor(module: ModuleID, platform: String, platformRepository: PlatformRepository) =
    platformRepository
        .getEndpoints(platformUrl = platform)
        .find { it.identifier == module }
        ?.let { build(baseUrl = it.url) }
        ?: throw OcpiToolkitUnknownEndpointException(endpointName = module.name)
