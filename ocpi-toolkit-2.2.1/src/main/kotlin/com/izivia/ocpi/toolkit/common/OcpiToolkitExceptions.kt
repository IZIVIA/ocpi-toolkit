package com.izivia.ocpi.toolkit.common

open class OcpiToolkitException(message: String, cause: Throwable? = null) : RuntimeException(message, cause)

class OcpiToolkitUnknownEndpointException(
    endpointName: String,
) : OcpiToolkitException(
    """
        Endpoint '$endpointName' is unknown. Make sure you registered (credentialClient#register) first. During
        registration process, the available endpoints are saved in partnerRepository to be used later on. If you
        already registered, make sure that the partnerRepository works properly.
    """.trimIndent(),
)

class OcpiToolkitResponseParsingException(
    message: String,
    cause: Throwable? = null,
) : OcpiToolkitException(
    message,
    cause,
)

class OcpiToolkitMissingRequiredResponseHeaderException(
    header: String,
) : OcpiToolkitException(
    """Required header "$header" not found in the response from the server""",
)
