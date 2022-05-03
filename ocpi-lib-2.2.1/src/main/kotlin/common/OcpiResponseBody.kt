package common

import org.valiktor.ConstraintViolationException
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
        private fun <T> success(data: T) = OcpiResponseBody(
            data = data,
            status_code = OcpiStatus.SUCCESS.code,
            status_message = null,
            timestamp = Instant.now()
        )

        private fun <T> invalid(message: String) = OcpiResponseBody<T>(
            data = null,
            status_code = OcpiStatus.CLIENT_INVALID_PARAMETERS.code,
            status_message = message,
            timestamp = Instant.now()
        )

        fun <T> of(data: () -> T) =
            try {
                success(data = data())
            } catch (e: ConstraintViolationException) {
                invalid(message = e.constraintViolations.toString())
            }
    }
}

fun <T> OcpiResponseBody<T>.toHttpResponse() =
    HttpResponse(
        status = when (status_code) {
            OcpiStatus.SUCCESS.code -> if(data != null) HttpStatus.OK else HttpStatus.NOT_FOUND
            OcpiStatus.CLIENT_INVALID_PARAMETERS.code -> HttpStatus.BAD_REQUEST
            else -> HttpStatus.INTERNAL_SERVER_ERROR
        },
        body = mapper.writeValueAsString(this)
    )