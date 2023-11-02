package com.izivia.ocpi.toolkit.common

import com.fasterxml.jackson.core.type.TypeReference
import com.izivia.ocpi.toolkit.modules.credentials.repositories.PlatformRepository
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
 * Creates the authorization header by taking the client token (or the token A if allowed) in the platform repository
 *
 * @receiver PlatformRepository used to retrieve tokens
 * @param platformUrl partner /versions url
 * @param allowTokenA only true when called on versions / credentials module
 * @return Pair<String, String>
 */
suspend fun PlatformRepository.buildAuthorizationHeader(
    platformUrl: String,
    allowTokenA: Boolean = false
): Pair<String, String> =
    if (allowTokenA) {
        getCredentialsTokenA(platformUrl = platformUrl)
            ?: getCredentialsClientToken(platformUrl = platformUrl)
            ?: throw throw OcpiClientUnknownTokenException(
                "Could not find token A or client token associated with platform $platformUrl"
            )
    } else {
        getCredentialsClientToken(platformUrl = platformUrl)
            ?: throw throw OcpiClientUnknownTokenException(
                "Could not find client token associated with platform $platformUrl"
            )
    }
        .let { token -> authorizationHeader(token = token) }

/**
 * Adds the authorization header to the request by taking the client token (or the token A if allowed) in the platform
 * repository.
 *
 * @param platformRepository use to retrieve tokens
 * @param platformUrl partner /versions url
 * @param allowTokenA only true when called on versions / credentials module
 */
suspend fun HttpRequest.authenticate(
    platformRepository: PlatformRepository,
    platformUrl: String,
    allowTokenA: Boolean = false
): AuthenticatedHttpRequest =
    withHeaders(
        headers = headers.plus(
            platformRepository.buildAuthorizationHeader(
                platformUrl = platformUrl,
                allowTokenA = allowTokenA
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
fun HttpRequest.withRequiredHeaders(
    requestId: String,
    correlationId: String
): HttpRequest =
    withHeaders(
        headers = headers
            .plus(Header.X_REQUEST_ID to requestId)
            .plus(Header.X_CORRELATION_ID to correlationId)
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
 * @param headers Headers of the caller. It will re-use the X-Correlation-ID header and regenerate X-Request-ID
 */
fun HttpRequest.withUpdatedRequiredHeaders(
    headers: Map<String, String>,
    generatedRequestId: String
): HttpRequest =
    withHeaders(
        headers = headers
            .plus(Header.X_REQUEST_ID to generatedRequestId) // it replaces existing X_REQUEST_ID header
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
        if (it.startsWith("Token ")) {
            it
        } else {
            throw OcpiClientInvalidParametersException("Unkown token format: $it")
        }
    }
    ?.removePrefix("Token ")
    ?.decodeBase64()
    ?: throw HttpException(HttpStatus.UNAUTHORIZED, "${Header.AUTHORIZATION} header missing")

/**
 * Throws an exception if the token is invalid. Does nothing otherwise.
 *
 * @throws OcpiClientInvalidParametersException if the token is invalid, otherwise does nothing
 * @throws OcpiClientNotEnoughInformationException if the token is missing
 * @throws HttpException if the authorization header is missing
 *
 */
suspend fun PlatformRepository.checkToken(
    httpRequest: HttpRequest
) {
    val token = httpRequest.parseAuthorizationHeader()

    /**
     * From OCPI 2.2.1 doc:
     * When a server receives a request with a valid CREDENTIALS_TOKEN_A, on another module than: credentials or
     * versions, the server SHALL respond with an HTTP 401 - Unauthorized status code.
     *
     * So, we allow token A only if we are in this case.
     */

    val allowTokenA = httpRequest.path.contains("versions") ||
        httpRequest.path.contains("/{versionNumber}") ||
        httpRequest.path.contains("credentials")

    val validToken = (allowTokenA && isCredentialsTokenAValid(token)) ||
        isCredentialsServerTokenValid(token)

    if (!validToken) {
        throw OcpiClientInvalidParametersException("Invalid server token (token A allowed: $allowTokenA): $token")
    }
}

suspend fun TransportClientBuilder.buildFor(
    module: ModuleID,
    platformUrl: String,
    platformRepository: PlatformRepository
): TransportClient =
    platformRepository
        .getEndpoints(platformUrl = platformUrl)
        .find { it.identifier == module }
        ?.let { build(baseUrl = it.url) }
        ?: throw OcpiToolkitUnknownEndpointException(endpointName = module.name)
