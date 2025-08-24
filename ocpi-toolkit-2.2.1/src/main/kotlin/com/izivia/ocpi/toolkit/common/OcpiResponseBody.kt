package com.izivia.ocpi.toolkit.common

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
 * @property statusCode Response code, as listed in Status Codes, indicates how the request was handled. To avoid
 * confusion with HTTP codes, at least four digits are used.
 * @property statusMessage An optional status message which may help when debugging.
 * @property timestamp The time this message was generated.
 */
data class OcpiResponseBody<T>(
    val data: T?,
    val statusCode: Int,
    val statusMessage: String? = null,
    val timestamp: Instant,
)
