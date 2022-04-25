package samples

import ocpi.locations.LocationsCpoServer
import ocpi.locations.LocationsEmspInterface
import ocpi.locations.domain.*

val cpoServer = "localhost:8080"
val cpoServerPort = 8080

/**
 * Example on how to serve a CPO server
 */
fun main() {
    // We specify the transport client to communicate with the eMSP
    val transportServer = Http4kTransportServer(emspServerPort)

    // We specify callbacks for the server
    val callbacks = LocationsCpoServerCallbacks()

    // We implement callbacks for the server
    LocationsCpoServer(transportServer, callbacks)

    // It is recommended to start the server after setting up the routes to handle
    transportServer.start()
}

class LocationsCpoServerCallbacks: LocationsEmspInterface {
    override fun getLocation(countryCode: String, partyId: String, locationId: String): Location {
        TODO("Not yet implemented")
    }

    override fun getEvse(countryCode: String, partyId: String, locationId: String, evseUid: String): Evse {
        TODO("Not yet implemented")
    }

    override fun getConnector(
        countryCode: String,
        partyId: String,
        locationId: String,
        evseUid: String,
        connectorId: String
    ): Connector {
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
        location: LocationPatch
    ): Location {
        TODO("Not yet implemented")
    }

    override fun patchEvse(
        countryCode: String,
        partyId: String,
        locationId: String,
        evseUid: String,
        evse: EvsePatch
    ): Evse {
        TODO("Not yet implemented")
    }

    override fun patchConnector(
        countryCode: String,
        partyId: String,
        locationId: String,
        evseUid: String,
        connectorId: String,
        connector: ConnectorPatch
    ): Connector {
        TODO("Not yet implemented")
    }
}