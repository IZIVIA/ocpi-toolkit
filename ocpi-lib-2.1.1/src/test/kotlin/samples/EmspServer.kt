package samples

import common.OcpiResponseBody
import ocpi.locations.LocationsEmspInterface
import ocpi.locations.LocationsEmspServer
import ocpi.locations.domain.*
import java.time.Instant

val emspServerUrl = "http://localhost:8081"
val emspServerPort = 8081

/**
 * Example on how to server an eMSP server
 */
fun main() {
    // We specify the transport to serve the eMSP server
    val transportServer = Http4kTransportServer("http://localhost", emspServerPort)

    // We specify callbacks for the server
    val callbacks = LocationsEmspServerCallbacks()

    // We implement callbacks for the server
    LocationsEmspServer(transportServer, callbacks)

    // It is recommended to start the server after setting up the routes to handle
    transportServer.start()
}

class LocationsEmspServerCallbacks: LocationsEmspInterface {
    override fun getLocation(countryCode: String, partyId: String, locationId: String): OcpiResponseBody<Location?> {
        return OcpiResponseBody.success(
            Location(
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
        )
    }

    override fun getEvse(
        countryCode: String,
        partyId: String,
        locationId: String,
        evseUid: String
    ): OcpiResponseBody<Evse?> {
        TODO("Not yet implemented")
    }

    override fun getConnector(
        countryCode: String,
        partyId: String,
        locationId: String,
        evseUid: String,
        connectorId: String
    ): OcpiResponseBody<Connector?> {
        TODO("Not yet implemented")
    }

    override fun putLocation(
        countryCode: String,
        partyId: String,
        locationId: String,
        location: Location
    ): OcpiResponseBody<Location> {
        TODO("Not yet implemented")
    }

    override fun putEvse(
        countryCode: String,
        partyId: String,
        locationId: String,
        evseUid: String,
        evse: Evse
    ): OcpiResponseBody<Evse> {
        TODO("Not yet implemented")
    }

    override fun putConnector(
        countryCode: String,
        partyId: String,
        locationId: String,
        evseUid: String,
        connectorId: String,
        connector: Connector
    ): OcpiResponseBody<Connector> {
        TODO("Not yet implemented")
    }

    override fun patchLocation(
        countryCode: String,
        partyId: String,
        locationId: String,
        location: LocationPatch
    ): OcpiResponseBody<Location?> {
        TODO("Not yet implemented")
    }

    override fun patchEvse(
        countryCode: String,
        partyId: String,
        locationId: String,
        evseUid: String,
        evse: EvsePatch
    ): OcpiResponseBody<Evse?> {
        TODO("Not yet implemented")
    }

    override fun patchConnector(
        countryCode: String,
        partyId: String,
        locationId: String,
        evseUid: String,
        connectorId: String,
        connector: ConnectorPatch
    ): OcpiResponseBody<Connector?> {
        TODO("Not yet implemented")
    }
}