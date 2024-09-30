package com.izivia.ocpi.toolkit.samples.locations

import com.izivia.ocpi.toolkit.common.checkToken
import com.izivia.ocpi.toolkit.modules.locations.LocationsEmspServer
import com.izivia.ocpi.toolkit.modules.locations.domain.*
import com.izivia.ocpi.toolkit.modules.locations.repositories.LocationsEmspRepository
import com.izivia.ocpi.toolkit.modules.locations.services.LocationsEmspService
import com.izivia.ocpi.toolkit.modules.versions.repositories.InMemoryVersionsRepository
import com.izivia.ocpi.toolkit.samples.common.Http4kTransportServer
import com.izivia.ocpi.toolkit.samples.common.validLocation
import kotlinx.coroutines.runBlocking

val emspServerUrl = "http://localhost:8081"
val emspServerVersionsUrl = "http://localhost:8081/versions"
val emspServerPort = 8081

/**
 * Example on how to server an eMSP server
 */
fun main() {
    // We specify the transport to serve the eMSP server
    val transportServer = Http4kTransportServer(
        baseUrl = emspServerUrl,
        port = emspServerPort,
        secureFilter = DUMMY_PLATFORM_REPOSITORY::checkToken,
    )

    // We specify service for the validation service
    val service = CacheLocationsEmspRepository()

    // We implement callbacks for the server using the built-in service and our repository implementation
    runBlocking {
        LocationsEmspServer(
            service = LocationsEmspService(service = service),
            versionsRepository = InMemoryVersionsRepository(),
        ).registerOn(transportServer)
    }

    // It is recommended to start the server after setting up the routes to handle
    transportServer.start()
}

class CacheLocationsEmspRepository : LocationsEmspRepository {
    override suspend fun getLocation(countryCode: String, partyId: String, locationId: String): Location {
        return validLocation
    }

    override suspend fun getEvse(countryCode: String, partyId: String, locationId: String, evseUid: String): Evse? {
        TODO("Not yet implemented")
    }

    override suspend fun getConnector(
        countryCode: String,
        partyId: String,
        locationId: String,
        evseUid: String,
        connectorId: String,
    ): Connector? {
        TODO("Not yet implemented")
    }

    override suspend fun putLocation(
        countryCode: String,
        partyId: String,
        locationId: String,
        location: Location,
    ): Location {
        TODO("Not yet implemented")
    }

    override suspend fun putEvse(
        countryCode: String,
        partyId: String,
        locationId: String,
        evseUid: String,
        evse: Evse,
    ): Evse {
        TODO("Not yet implemented")
    }

    override suspend fun putConnector(
        countryCode: String,
        partyId: String,
        locationId: String,
        evseUid: String,
        connectorId: String,
        connector: Connector,
    ): Connector {
        TODO("Not yet implemented")
    }

    override suspend fun patchLocation(
        countryCode: String,
        partyId: String,
        locationId: String,
        location: LocationPartial,
    ): Location? {
        TODO("Not yet implemented")
    }

    override suspend fun patchEvse(
        countryCode: String,
        partyId: String,
        locationId: String,
        evseUid: String,
        evse: EvsePartial,
    ): Evse? {
        TODO("Not yet implemented")
    }

    override suspend fun patchConnector(
        countryCode: String,
        partyId: String,
        locationId: String,
        evseUid: String,
        connectorId: String,
        connector: ConnectorPartial,
    ): Connector? {
        TODO("Not yet implemented")
    }
}
