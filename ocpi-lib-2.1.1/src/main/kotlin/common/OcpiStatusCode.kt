package common

/**
 * - 1xxx: Success
 * - 2xxx: Client errors - Errors detected by a server in the message sent by a client: The client did something wrong
 * - 3xxx: Server errors - Error during processing of the OCPI payload in the server. The message was syntactically
 * correct but could not be processed by the server.
 */
enum class OcpiStatusCode(val code: Int) {
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
    INVALID_OR_MISSING_PARAMETERS(2001),

    /**
     * Not enough information, for example: Authorization request with too little information.
     */
    NOT_ENOUGH_INFORMATION(2002),

    /**
     * Unknown Location, for example: Command: START_SESSION with unknown location.
     */
    UNKNOWN_LOCATION(2003),

    /**
     * Generic server error
     */
    GENERIC_SERVER_ERROR(3000),

    /**
     * Unable to use the client's API. For example during the credentials registration: When the initializing party
     * requests data from the other party during the open POST call to its credentials endpoint. If one of the GETs can
     * not be processed, the party should return this error in the POST response.
     */
    UNABLE_TO_USE_CLIENT_API(3001),

    /**
     * Unsupported version.
     */
    UNSUPPORTED_VERSION(3002),

    /**
     * No matching endpoints or expected endpoints missing between parties. Used during the registration process if the
     * two parties do not have any mutual modules or endpoints available, or the minimum expected by the other party
     * implementation.
     */
    NO_MATCHING_ENDPOINT(3003)
}