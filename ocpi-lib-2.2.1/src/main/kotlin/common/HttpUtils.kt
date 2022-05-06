package common

import com.fasterxml.jackson.module.kotlin.readValue
import transport.domain.HttpException
import transport.domain.HttpRequest
import transport.domain.HttpResponse
import transport.domain.HttpStatus
import java.util.*

/**
 * Parses the body of the given HttpResponse to the specified type
 *
 * @throws StreamReadException – if underlying input contains invalid content of type JsonParser supports (JSON for
 * default case)
 * @throws DatabindException – if the input JSON structure does not match structure expected for result type (or has
 * other mismatch issues)
 */
inline fun <reified T> HttpResponse.parseBody(): T = mapper.readValue(body!!)

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
 * Parses authorization header from the HttpRequest
 *
 * @throws OcpiClientUnknownTokenException if the token format is invalid (does not start by "Token ")
 * @throws OcpiClientNotEnoughInformationException if the token is missing
 */
fun HttpRequest.parseAuthorizationHeader() = headers["Authorization"]
    ?.let {
        if (it.startsWith("Token ")) it
        else throw OcpiClientUnknownTokenException("Unkown token format: $it")
    }
    ?.removePrefix("Token ")
    ?.decodeBase64()
    ?: throw HttpException(HttpStatus.UNAUTHORIZED, "Authorization header missing")