package com.izivia.ocpi.toolkit.common

import com.izivia.ocpi.toolkit.transport.domain.HttpRequest
import java.time.Instant

fun HttpRequest.pathParam(key: String) =
    pathParams[key]
        ?: throw OcpiServerGenericException("Misconfigured endpoint. Missing path parameter '$key'")

fun HttpRequest.queryParam(key: String) =
    queryParams[key]
        ?: throw OcpiClientInvalidParametersException("Missing required '$key' query parameter")

fun HttpRequest.queryParamAsInstant(key: String) =
    optionalQueryParamAsInstant(key)
        ?: throw OcpiClientInvalidParametersException("Missing required '$key' query parameter")

fun HttpRequest.optionalQueryParamAsInstant(key: String) =
    optionalQueryParamAs(key) { Instant.parse(it) }

fun HttpRequest.queryParamAsInt(key: String) =
    optionalQueryParamAsInt(key)
        ?: throw OcpiClientInvalidParametersException("Missing required '$key' query parameter")

fun HttpRequest.optionalQueryParamAsInt(key: String) =
    optionalQueryParamAs(key) { it.toInt() }

fun <T> HttpRequest.optionalQueryParamAs(key: String, fn: (String) -> T): T? =
    queryParams[key]?.let {
        try {
            fn(it)
        } catch (e: Throwable) {
            throw OcpiClientInvalidParametersException("Invalid value for param '$key': '$it'")
        }
    }
