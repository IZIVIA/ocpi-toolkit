package samples

import ocpi.locations.LocationsEmspServer
import ocpi.locations.domain.*
import ocpi.locations.repositories.LocationsEmspRepository
import ocpi.locations.services.LocationsEmspService
import java.time.Instant

val emspServerUrl = "http://localhost:8081"
val emspServerPort = 8081

/**
 * Example on how to server an eMSP server
 */
fun main() {
    // We specify the transport to serve the eMSP server
    val transportServer = Http4kTransportServer(emspServerUrl, emspServerPort)

    // We specify repository for the service
    val repository = CacheLocationsEmspRepository()

    // We implement callbacks for the server using the built-in service and our repository implementation
    LocationsEmspServer(transportServer, LocationsEmspService(repository))

    // It is recommended to start the server after setting up the routes to handle
    transportServer.start()
}

class CacheLocationsEmspRepository: LocationsEmspRepository {
    override fun getLocation(countryCode: String, partyId: String, locationId: String): Location? {
        return Location(
            id = locationId,
            type = LocationType.ON_STREET,
            name = null,
            address = "1 avenue des satellites",
            city = "Bruges",
            postal_code = "33520",
            country = "france",
            coordinates = GeoLocation(latitude = "1.0", longitude = "2.56"),
            related_locations = emptyList(),
            evses = emptyList(),
            directions = emptyList(),
            operator = null,
            suboperator = null,
            owner = null,
            facilities = emptyList(),
            time_zone = null,
            opening_times = null,
            charging_when_closed = null,
            images = emptyList(),
            energy_mix = null,
            last_updated = Instant.now()
        )
    }

    override fun getEvse(countryCode: String, partyId: String, locationId: String, evseUid: String): Evse? {
        TODO("Not yet implemented")
    }

    override fun getConnector(
        countryCode: String,
        partyId: String,
        locationId: String,
        evseUid: String,
        connectorId: String
    ): Connector? {
        TODO("Not yet implemented")
    }

    override fun putLocation(countryCode: String, partyId: String, locationId: String, location: Location): Location {
        TODO("Not yet implemented")
    }

    override fun putEvse(countryCode: String, partyId: String, locationId: String, evseUid: String, evse: Evse): Evse {
        TODO("Not yet implemented")
    }

    override fun putConnector(
        countryCode: String,
        partyId: String,
        locationId: String,
        evseUid: String,
        connectorId: String,
        connector: Connector
    ): Connector {
        TODO("Not yet implemented")
    }

    override fun patchLocation(
        countryCode: String,
        partyId: String,
        locationId: String,
        location: LocationPartial
    ): Location? {
        TODO("Not yet implemented")
    }

    override fun patchEvse(
        countryCode: String,
        partyId: String,
        locationId: String,
        evseUid: String,
        evse: EvsePartial
    ): Evse? {
        TODO("Not yet implemented")
    }

    override fun patchConnector(
        countryCode: String,
        partyId: String,
        locationId: String,
        evseUid: String,
        connectorId: String,
        connector: ConnectorPartial
    ): Connector? {
        TODO("Not yet implemented")
    }
}