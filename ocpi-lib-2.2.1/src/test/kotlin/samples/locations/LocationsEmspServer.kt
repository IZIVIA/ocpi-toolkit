package samples.locations

import ocpi.locations.LocationsEmspServer
import ocpi.locations.domain.*
import ocpi.locations.services.LocationsEmspService
import ocpi.locations.validation.LocationsEmspValidationService
import samples.common.Http4kTransportServer
import samples.common.validLocation
import java.time.Instant

val emspServerUrl = "http://localhost:8081"
val emspServerPort = 8081

/**
 * Example on how to server an eMSP server
 */
fun main() {
    // We specify the transport to serve the eMSP server
    val transportServer = Http4kTransportServer(baseUrl = emspServerUrl, port = emspServerPort)

    // We specify service for the validation service
    val service = CacheLocationsEmspService()

    // We implement callbacks for the server using the built-in service and our repository implementation
    LocationsEmspServer(
        transportServer = transportServer,
        platformRepository = DUMMY_PLATFORM_REPOSITORY,
        service = LocationsEmspValidationService(service = service)
    )

    // It is recommended to start the server after setting up the routes to handle
    transportServer.start()
}

class CacheLocationsEmspService : LocationsEmspService {
    override fun getLocation(countryCode: String, partyId: String, locationId: String): Location? {
        return validLocation
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
