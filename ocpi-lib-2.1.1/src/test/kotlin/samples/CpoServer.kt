package samples

import common.SearchResult
import ocpi.locations.LocationsCpoServer
import ocpi.locations.domain.Connector
import ocpi.locations.domain.Evse
import ocpi.locations.domain.Location
import ocpi.locations.repositories.LocationsCpoRepository
import ocpi.locations.services.LocationsCpoService
import java.time.Instant

val cpoServerUrl = "http://localhost:8080"
val cpoServerPort = 8080

/**
 * Example on how to serve a CPO server
 */
fun main() {
    // We specify the transport to serve the cpo server
    val transportServer = Http4kTransportServer(cpoServerPort)

    // We specify repository for the service
    val repository = CacheLocationsCpoRepository()

    // We implement callbacks for the server using the built-in service and our repository implementation
    LocationsCpoServer(transportServer, LocationsCpoService(repository))

    // It is recommended to start the server after setting up the routes to handle
    transportServer.start()
}

class CacheLocationsCpoRepository : LocationsCpoRepository {
    override fun getLocations(dateFrom: Instant?, dateTo: Instant?, offset: Int, limit: Int?): SearchResult<Location> {
        TODO("Not yet implemented")
    }

    override fun getLocation(locationId: String): Location? {
        TODO("Not yet implemented")
    }

    override fun getEvse(locationId: String, evseUid: String): Evse? {
        TODO("Not yet implemented")
    }

    override fun getConnector(locationId: String, evseUid: String, connectorId: String): Connector? {
        TODO("Not yet implemented")
    }
}