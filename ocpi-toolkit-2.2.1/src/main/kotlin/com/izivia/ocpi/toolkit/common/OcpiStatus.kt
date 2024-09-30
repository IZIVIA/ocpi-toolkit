package com.izivia.ocpi.toolkit.common

/**
 * - 1xxx: Success
 * - 2xxx: Client errors - Errors detected by a server in the message sent by a client: The client did something wrong
 * - 3xxx: Server errors - Error during processing of the OCPI payload in the server. The message was syntactically
 * correct but could not be processed by the server.
 */
enum class OcpiStatus(val code: Int) {
    /**
     * Generic success code
     */
    SUCCESS(1000),

    /**
     * Generic client error
     */
    CLIENT_ERROR(2000),

    /**
     * Invalid or missing parameters
     */
    CLIENT_INVALID_PARAMETERS(2001),

    /**
     * Not enough information, for example: Authorization request with too little information.
     */
    CLIENT_NOT_ENOUGH_INFORMATION(2002),

    /**
     * Unknown Location, for example: Command: START_SESSION with unknown location.
     */
    CLIENT_UNKNOWN_LOCATION(2003),

    /**
     * Unknown Token, for example: 'real-time' authorization of an unknown Token.
     */
    CLIENT_UNKNOWN_TOKEN(2004),

    /**
     * Generic server error
     */
    SERVER_ERROR(3000),

    /**
     * Unable to use the client's API. For example during the credentials registration: When the initializing party
     * requests data from the other party during the open POST call to its credentials endpoint. If one of the GETs can
     * not be processed, the party should return this error in the POST response.
     */
    SERVER_UNUSABLE_API(3001),

    /**
     * Unsupported version.
     */
    SERVER_UNSUPPORTED_VERSION(3002),

    /**
     * No matching endpoints or expected endpoints missing between parties. Used during the registration process if the
     * two parties do not have any mutual modules or endpoints available, or the minimum expected by the other party
     * implementation.
     */
    SERVER_NO_MATCHING_ENDPOINTS(3003),

    /**
     * Generic error
     */
    HUB_ERROR(4001),

    /**
     * Unknown receiver (TO address is unknown)
     */
    HUB_UNKNOWN_RECEIVER(4001),

    /**
     * Timeout on forwarded request (message is forwarded, but request times out)
     */
    HUB_REQUEST_TIMEOUT(4002),

    /**
     * Connection problem (receiving party is not connected)
     */
    HUB_CONNECTION_PROBLEM(4003),
}

fun Int.toOcpiStatus(): OcpiStatus? = OcpiStatus.values().firstOrNull { it.code == this }
