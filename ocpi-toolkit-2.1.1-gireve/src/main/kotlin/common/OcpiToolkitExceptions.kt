package common

class OcpiToolkitUnknownEndpointException(
    endpointName: String
) : Exception(
    """
        Endpoint '$endpointName' is unknown. Make sure you registered (credentialClient#register) first. During
        registration process, the available endpoints are saved in platformRepository to be used later on. If you
        already registered, make sure that the platformRepository works properly.
    """.trimIndent()
)