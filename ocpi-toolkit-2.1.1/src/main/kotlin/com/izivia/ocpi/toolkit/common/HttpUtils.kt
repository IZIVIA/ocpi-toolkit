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
fun String.encodeBase64(): String = this // Base64.getEncoder().encodeToString(this.encodeToByteArray())

/**
 * Decodes a base64-encoded string, also @see String#encodeBase64()
 */
fun String.decodeBase64(): String = this // Base64.getDecoder().decode(this).decodeToString()

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
    copy(
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
    copy(headers = headers.plus(authorizationHeader(token = token)))

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
fun PlatformRepository.checkToken(httpRequest: HttpRequest) {
    val token = httpRequest.parseAuthorizationHeader()

    if (!platformExistsWithTokenA(token) &&
        !platformExistsWithTokenB(token) &&
        getPlatformUrlByTokenC(token) == null) {

        throw OcpiClientInvalidParametersException("Invalid token: $token")
    }
}

fun TransportClientBuilder.buildFor(module: ModuleID, platformUrl: String, platformRepository: PlatformRepository) =
    platformRepository
        .getEndpoints(platformUrl = platformUrl)
        .find { it.identifier == module }
        ?.let { build(baseUrl = it.url) }
        ?: throw OcpiToolkitUnknownEndpointException(endpointName = module.name)
