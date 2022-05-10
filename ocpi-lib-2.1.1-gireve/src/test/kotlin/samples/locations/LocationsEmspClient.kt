package samples.locations

import ocpi.locations.LocationsEmspClient
import samples.common.Http4kTransportClient

/**
 * Example on how to use the eMSP client
 */
fun main() {
    // We specify the transport client to communicate with the CPO
    val transportClient = Http4kTransportClient(baseUrl = cpoServerUrl)

    // We instantiate the clients that we want to use
    val locationsEmspClient = LocationsEmspClient(
        transportClient = transportClient,
        platformRepository = DUMMY_PLATFORM_REPOSITORY
    )

    // We can use it
    println(
        locationsEmspClient.getConnector(locationId = "location1", evseUid = "evse1", connectorId = "connector1")
    )
}