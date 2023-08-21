package com.izivia.ocpi.toolkit.common

import com.fasterxml.jackson.core.type.TypeReference
import com.izivia.ocpi.toolkit.modules.credentials.repositories.PlatformRepository
import com.izivia.ocpi.toolkit.modules.versions.domain.ModuleID
import com.izivia.ocpi.toolkit.transport.TransportClientBuilder
import com.izivia.ocpi.toolkit.transport.domain.HttpException
import com.izivia.ocpi.toolkit.transport.domain.HttpRequest
import com.izivia.ocpi.toolkit.transport.domain.HttpResponse
import com.izivia.ocpi.toolkit.transport.domain.HttpStatus
import java.util.*

typealias AuthenticatedHttpRequest = HttpRequest

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
                    totalCount = headers["X-Total-Count"]!!.toInt(),
                    limit = headers["X-Limit"]!!.toInt(),
                    offset = offset,
                    nextPageUrl = headers["Link"]?.split("<")?.get(1)?.split(">")?.first()
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
fun authorizationHeader(token: String): Pair<String, String> = "Authorization" to "Token ${token.encodeBase64()}"

/**
 * Creates the authorization header by taking the right token in the platform repository
 */
fun PlatformRepository.buildAuthorizationHeader(baseUrl: String, allowTokenAOrTokenB: Boolean = false) =
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
fun HttpRequest.authenticate(
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
 * For debugging issues, OCPI implementations are required to include unique IDs via HTTP headers in every
 * request/response
 *
 * - X-Request-ID: Every request SHALL contain a unique request ID, the response to this request SHALL contain the same
 * ID.
 * - X-Correlation-ID: Every request/response SHALL contain a unique correlation ID, every response to this request
 * SHALL contain the same ID.
 *
 * This method should be called when doing the first request from a client.
 *
 * Dev note: When the server does a request (not a response), it must keep the same X-Correlation-ID but generate a new
 * X-Request-ID. So don't call this method in that case.
 */
fun HttpRequest.withDebugHeaders(): HttpRequest =
    withHeaders(
        headers = headers
            .plus("X-Request-ID" to generateUUIDv4Token())
            .plus("X-Correlation-ID" to generateUUIDv4Token())
    )

/**
 * For debugging issues, OCPI implementations are required to include unique IDs via HTTP headers in every
 * request/response
 *
 * - X-Request-ID: Every request SHALL contain a unique request ID, the response to this request SHALL contain the same
 * ID.
 * - X-Correlation-ID: Every request/response SHALL contain a unique correlation ID, every response to this request
 * SHALL contain the same ID.
 *
 * This method should be called when doing the a request from a server.
 *
 * TODO: test debug headers with integration tests
 *
 * @param headers Headers of the caller. It will re-use the X-Correlation-ID header and regenerate X-Request-ID
 */
fun HttpRequest.withUpdatedDebugHeaders(headers: Map<String, String>): HttpRequest =
    withHeaders(
        headers = headers
            .plus("X-Request-ID" to generateUUIDv4Token())
            .plus(
                "X-Correlation-ID" to headers.getOrDefault(
                    "X-Correlation-ID",
                    "error - could not get X-Correlation-ID header"
                )
            )
    )

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
    headers["X-Request-ID"]?.let { "X-Request-ID" to it },
    headers["X-Correlation-ID"]?.let { "X-Correlation-ID" to it }
).toMap()

/**
 * Parses authorization header from the HttpRequest
 *
 * @throws OcpiClientNotEnoughInformationException if the token is missing
 * @throws HttpException if the authorization header is missing
 */
fun HttpRequest.parseAuthorizationHeader() = (headers["Authorization"] ?: headers["authorization"])
    ?.let {
        if (it.startsWith("Token ")) it
        else throw OcpiClientInvalidParametersException("Unkown token format: $it")
    }
    ?.removePrefix("Token ")
    ?.decodeBase64()
    ?: throw HttpException(HttpStatus.UNAUTHORIZED, "Authorization header missing")

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
fun PlatformRepository.tokenFilter(httpRequest: HttpRequest) {
    val token = httpRequest.parseAuthorizationHeader()

    if (getPlatformByTokenA(token) == null &&
        getPlatformByTokenB(token) == null &&
        getPlatformByTokenC(token) == null
    ) {

        throw OcpiClientInvalidParametersException("Invalid token: $token")
    }
}

fun TransportClientBuilder.buildFor(module: ModuleID, platform: String, platformRepository: PlatformRepository) =
    platformRepository
        .getEndpoints(platformUrl = platform)
        .find { it.identifier == module }
        ?.let { build(baseUrl = it.url) }
        ?: throw OcpiToolkitUnknownEndpointException(endpointName = module.name)
