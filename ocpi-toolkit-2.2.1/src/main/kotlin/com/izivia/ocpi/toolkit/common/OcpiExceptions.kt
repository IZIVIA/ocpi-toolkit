package com.izivia.ocpi.toolkit.common

import com.izivia.ocpi.toolkit.transport.domain.HttpStatus

sealed class OcpiException(
    message: String,
    open val httpStatus: HttpStatus,
    open val ocpiStatus: OcpiStatus,
) : Exception(message)

class OcpiResponseException(
    val statusCode: Int,
    val statusMessage: String,
) : Exception("Ocpi error: ${statusCode.toOcpiStatus()} $statusCode ($statusMessage)")

// 2xxx: Client errors
class OcpiClientGenericException(
    message: String,
    override val httpStatus: HttpStatus = HttpStatus.BAD_REQUEST,
    override val ocpiStatus: OcpiStatus = OcpiStatus.CLIENT_ERROR,
) : OcpiException(message, httpStatus, ocpiStatus)

class OcpiClientInvalidParametersException(
    message: String = "Invalid or missing parameters",
    override val httpStatus: HttpStatus = HttpStatus.BAD_REQUEST,
    override val ocpiStatus: OcpiStatus = OcpiStatus.CLIENT_INVALID_PARAMETERS,
) : OcpiException(message, httpStatus, ocpiStatus)

class OcpiClientNotEnoughInformationException(
    message: String = "Not enough information",
    override val httpStatus: HttpStatus = HttpStatus.BAD_REQUEST,
    override val ocpiStatus: OcpiStatus = OcpiStatus.CLIENT_NOT_ENOUGH_INFORMATION,
) : OcpiException(message, httpStatus, ocpiStatus)

class OcpiClientUnknownLocationException(
    message: String = "Unknown location",
    override val httpStatus: HttpStatus = HttpStatus.NOT_FOUND,
    override val ocpiStatus: OcpiStatus = OcpiStatus.CLIENT_UNKNOWN_LOCATION,
) : OcpiException(message, httpStatus, ocpiStatus)

class OcpiClientUnknownTokenException(
    message: String = "Unknown token",
    override val httpStatus: HttpStatus = HttpStatus.BAD_REQUEST,
    override val ocpiStatus: OcpiStatus = OcpiStatus.CLIENT_UNKNOWN_TOKEN,
) : OcpiException(message, httpStatus, ocpiStatus)

class OcpiClientMethodNotAllowedException(
    message: String = "Method not allowed",
    override val httpStatus: HttpStatus = HttpStatus.METHOD_NOT_ALLOWED,
    override val ocpiStatus: OcpiStatus = OcpiStatus.CLIENT_UNKNOWN_TOKEN,
) : OcpiException(message, httpStatus, ocpiStatus)

// 3xxx: Server errors
class OcpiServerGenericException(
    message: String,
    override val httpStatus: HttpStatus = HttpStatus.OK,
    override val ocpiStatus: OcpiStatus = OcpiStatus.SERVER_ERROR,
) : OcpiException(message, httpStatus, ocpiStatus)

class OcpiServerUnusableApiException(
    message: String = "Unable to use client's API",
    override val httpStatus: HttpStatus = HttpStatus.OK,
    override val ocpiStatus: OcpiStatus = OcpiStatus.SERVER_UNUSABLE_API,
) : OcpiException(message, httpStatus, ocpiStatus)

class OcpiServerUnsupportedVersionException(
    message: String = "Unsupported version",
    override val httpStatus: HttpStatus = HttpStatus.OK,
    override val ocpiStatus: OcpiStatus = OcpiStatus.SERVER_UNSUPPORTED_VERSION,
) : OcpiException(message, httpStatus, ocpiStatus)

class OcpiServerNoMatchingEndpointsException(
    message: String = "No matching endpoints or expected endpoints missing between parties",
    override val httpStatus: HttpStatus = HttpStatus.OK,
    override val ocpiStatus: OcpiStatus = OcpiStatus.SERVER_NO_MATCHING_ENDPOINTS,
) : OcpiException(message, httpStatus, ocpiStatus)

// 4xxx: Hub errors
class OcpiHubUnknownReceiverException(
    message: String = "Unknown receiver",
    override val httpStatus: HttpStatus = HttpStatus.OK,
    override val ocpiStatus: OcpiStatus = OcpiStatus.HUB_UNKNOWN_RECEIVER,
) : OcpiException(message, httpStatus, ocpiStatus)

class OcpiHubTimeoutOnRequestException(
    message: String = "Timeout on forwarded request",
    override val httpStatus: HttpStatus = HttpStatus.OK,
    override val ocpiStatus: OcpiStatus = OcpiStatus.HUB_REQUEST_TIMEOUT,
) : OcpiException(message, httpStatus, ocpiStatus)

class OcpiHubConnectionProblemException(
    message: String = "Connection problem",
    override val httpStatus: HttpStatus = HttpStatus.OK,
    override val ocpiStatus: OcpiStatus = OcpiStatus.HUB_CONNECTION_PROBLEM,
) : OcpiException(message, httpStatus, ocpiStatus)
