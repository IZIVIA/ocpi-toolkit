package samples

import ocpi.locations.LocationsEmspClient

/**
 * Example on how to use the eMSP client
 */
fun main() {
    // We specify the transport client to communicate with the CPO
    val transportClient = Http4kTransportClient(cpoServerUrl)

    // We instantiate the clients that we want to use
    val locationsEmspClient = LocationsEmspClient(transportClient)

    // We can use it
    println(
        locationsEmspClient.getConnector("location1", "evse1", "connector1")
    )
}