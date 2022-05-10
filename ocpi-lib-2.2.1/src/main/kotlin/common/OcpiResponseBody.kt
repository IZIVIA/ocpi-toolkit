package common

import com.fasterxml.jackson.core.JsonProcessingException
import common.validation.toReadableString
import org.apache.logging.log4j.LogManager
import org.valiktor.ConstraintViolationException
import transport.domain.HttpException
import transport.domain.HttpRequest
import transport.domain.HttpResponse
import transport.domain.HttpStatus
import java.time.Instant

/**
 * When the status code is in the success range (1xxx), the data field in the response message should contain the
 * information as specified in the protocol. Otherwise the data field is unspecified and may be omitted, null or
 * something else that could help to debug the problem from a programmer's perspective. For example, it could specify
 * which fields contain an error or are missing.
 *
 * The content that is sent with all the response messages is an 'application/json' type and contains a JSON object with
 * the following properties:
 *
 * @property data Contains the actual response data object or list of objects from each request, depending on the
 * cardinality of the response data, this is an array (card. * or +), or a single object (card. 1 or ?)
 * @property status_code Response code, as listed in Status Codes, indicates how the request was handled. To avoid
 * confusion with HTTP codes, at least four digits are used.
 * @property status_message An optional status message which may help when debugging.
 * @property timestamp The time this message was generated.
 */
data class OcpiResponseBody<T>(
    val data: T?,
    val status_code: Int,
    val status_message: String?,
    val timestamp: Instant
) {
    companion object {
        fun <T> success(data: T) = OcpiResponseBody(
            data = data,
            status_code = OcpiStatus.SUCCESS.code,
            status_message = null,
            timestamp = Instant.now()
        )

        fun <T> invalid(message: String) = OcpiResponseBody<T>(
            data = null,
            status_code = OcpiStatus.CLIENT_INVALID_PARAMETERS.code,
            status_message = message,
            timestamp = Instant.now()
        )

        fun <T> of(data: () -> T) =
            try {
                success(data = data())
            } catch (e: ConstraintViolationException) {
                invalid(message = e.toReadableString())
            }
    }
}

private val logger = LogManager.getLogger(OcpiResponseBody::class.java)

fun OcpiResponseBody<SearchResult<*>>.getPaginatedHeaders(request: HttpRequest) =
    if (data != null) {
        val nextPageOffset = (data.offset + data.limit).takeIf { it <= data.totalCount }

        val queries = request
            .queryParams
            .filter { it.key != "offset" && it.value != null }
            .plus("offset" to (data.limit + data.offset))
            .map { "${it.key}=${it.value}" }
            .joinToString("&", "?")

        listOfNotNull(
            nextPageOffset?.let { "Link" to "<${request.baseUrl}${request.path}$queries>; rel=\"next\"" },
            "X-Total-Count" to data.totalCount.toString(),
            "X-Limit" to data.limit.toString()
        ).toMap()
    } else {
        emptyMap()
    }

fun OcpiException.toHttpResponse(): HttpResponse =
    HttpResponse(
        status = httpStatus,
        body = mapper.writeValueAsString(
            OcpiResponseBody(
                data = null,
                status_code = ocpiStatus.code,
                status_message = message,
                timestamp = Instant.now()
            )
        ),
        headers = if (httpStatus == HttpStatus.UNAUTHORIZED) mapOf("WWW-Authenticate" to "Token") else emptyMap()
    )

@Suppress("UNCHECKED_CAST")
fun <T> HttpRequest.httpResponse(fn: () -> OcpiResponseBody<T>): HttpResponse =
    try {
        val ocpiResponseBody = fn()
        val isPaginated = ocpiResponseBody.data is SearchResult<*>

        HttpResponse(
            status = when (ocpiResponseBody.status_code) {
                OcpiStatus.SUCCESS.code -> if (ocpiResponseBody.data != null) HttpStatus.OK else HttpStatus.NOT_FOUND
                OcpiStatus.CLIENT_INVALID_PARAMETERS.code -> HttpStatus.BAD_REQUEST
                else -> HttpStatus.INTERNAL_SERVER_ERROR
            },
            body = mapper.writeValueAsString(
                if (isPaginated) OcpiResponseBody(
                    data = (ocpiResponseBody.data as SearchResult<*>?)?.list,
                    status_code = ocpiResponseBody.status_code,
                    status_message = ocpiResponseBody.status_message,
                    timestamp = ocpiResponseBody.timestamp
                )
                else ocpiResponseBody
            )
        ).let {
            if (isPaginated) it.copy(
                headers = (ocpiResponseBody as OcpiResponseBody<SearchResult<*>>)
                    .getPaginatedHeaders(request = this)
            )
            else it
        }
    } catch (e: OcpiException) {
        e.toHttpResponse()
    } catch (e: HttpException) {
        logger.error(e)
        HttpResponse(
            status = e.status
        )
    } catch (e: JsonProcessingException) {
        logger.error(e)
        HttpResponse(
            status = HttpStatus.BAD_REQUEST
        )
    }