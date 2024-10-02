package com.izivia.ocpi.toolkit.common

class OcpiToolkitUnknownEndpointException(
    endpointName: String,
) : Exception(
    """
        Endpoint '$endpointName' is unknown. Make sure you registered (credentialClient#register) first. During
        registration process, the available endpoints are saved in partnerRepository to be used later on. If you
        already registered, make sure that the partnerRepository works properly.
    """.trimIndent(),
)

class OcpiToolkitResponseParsingException(
    urlCalled: String,
    cause: Throwable,
) : Exception(
    "Response cannot be parsed. URL='$urlCalled', error='${cause.message}'",
)

class OcpiToolkitMissingRequiredResponseHeaderException(
    header: String,
) : Exception(
    """Required header "$header" not found in the response from the server""",
)
