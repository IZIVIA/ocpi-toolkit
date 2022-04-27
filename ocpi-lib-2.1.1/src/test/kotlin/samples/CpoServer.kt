package samples

import common.OcpiResponseBody
import ocpi.locations.LocationsCpoInterface
import ocpi.locations.LocationsCpoServer
import ocpi.locations.domain.Connector
import ocpi.locations.domain.Evse
import ocpi.locations.domain.Location
import java.time.Instant

val cpoServerUrl = "http://localhost:8080"
val cpoServerPort = 8080

/**
 * Example on how to serve a CPO server
 */
fun main() {
    // We specify the transport to serve the cpo server
    val transportServer = Http4kTransportServer("http://localhost", cpoServerPort)

    // We specify callbacks for the server
    val callbacks = LocationsCpoServerCallbacks()

    // We implement callbacks for the server
    LocationsCpoServer(transportServer, callbacks)

    // It is recommended to start the server after setting up the routes to handle
    transportServer.start()
}

class LocationsCpoServerCallbacks : LocationsCpoInterface {
    override fun getLocations(
        dateFrom: Instant?,
        dateTo: Instant?,
        offset: Int?,
        limit: Int?
    ): OcpiResponseBody<List<Location>> {
        TODO("Not yet implemented")
    }

    override fun getLocation(locationId: String): OcpiResponseBody<Location?> {
        TODO("Not yet implemented")
    }

    override fun getEvse(locationId: String, evseUid: String): OcpiResponseBody<Evse?> {
        TODO("Not yet implemented")
    }

    override fun getConnector(locationId: String, evseUid: String, connectorId: String): OcpiResponseBody<Connector?> {
        TODO("Not yet implemented")
    }
}