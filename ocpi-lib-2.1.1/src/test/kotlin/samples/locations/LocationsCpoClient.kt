package samples.locations

import ocpi.locations.LocationsCpoClient
import samples.common.Http4kTransportClient

/**
 * Example on how to use the CPO client
 */
fun main() {
    // We specify the transport client to communicate with the eMSP
    val transportClient = Http4kTransportClient(emspServerUrl)

    // We instantiate the clients that we want to use
    val locationsCpoClient = LocationsCpoClient(transportClient)

    // We can use it
    println(
        locationsCpoClient.getLocation(CREDENTIALS_TOKEN_C,"fr", "abc", "location1")
    )
}