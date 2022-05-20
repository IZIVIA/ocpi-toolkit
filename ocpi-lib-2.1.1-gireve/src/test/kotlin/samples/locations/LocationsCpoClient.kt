package samples.locations

import ocpi.locations.LocationsCpoClient
import samples.common.Http4kTransportClientBuilder

/**
 * Example on how to use the CPO client
 */
fun main() {
    // We instantiate the clients that we want to use
    val locationsCpoClient = LocationsCpoClient(
        transportClientBuilder = Http4kTransportClientBuilder(),
        serverVersionsEndpointUrl = emspServerVersionsUrl,
        platformRepository = DUMMY_PLATFORM_REPOSITORY
    )

    // We can use it
    println(
        locationsCpoClient.getLocation(countryCode = "fr", partyId = "abc", locationId = "location1")
    )
}