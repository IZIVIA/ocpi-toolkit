package com.izivia.ocpi.toolkit.samples.locations

import com.izivia.ocpi.toolkit.common.SearchResult
import com.izivia.ocpi.toolkit.common.checkToken
import com.izivia.ocpi.toolkit.modules.locations.LocationsCpoServer
import com.izivia.ocpi.toolkit.modules.locations.domain.Connector
import com.izivia.ocpi.toolkit.modules.locations.domain.Evse
import com.izivia.ocpi.toolkit.modules.locations.domain.Location
import com.izivia.ocpi.toolkit.modules.locations.repositories.LocationsCpoRepository
import com.izivia.ocpi.toolkit.modules.locations.services.LocationsCpoService
import com.izivia.ocpi.toolkit.modules.versions.repositories.InMemoryVersionsRepository
import com.izivia.ocpi.toolkit.samples.common.Http4kTransportServer
import kotlinx.coroutines.runBlocking
import java.time.Instant

val cpoServerUrl = "http://localhost:8080"
val cpoServerVersionsUrl = "http://localhost:8080/versions"
val cpoServerPort = 8080

/**
 * Example on how to serve a CPO server
 */
fun main() {
    // We specify the transport to serve the cpo server
    val transportServer = Http4kTransportServer(
        baseUrl = cpoServerUrl,
        port = cpoServerPort,
        secureFilter = DUMMY_PLATFORM_REPOSITORY::checkToken,
    )

    // We specify service for the validation service
    val service = CacheLocationsCpoRepository()

    // We implement callbacks for the server using the built-in service and our service implementation
    runBlocking {
        LocationsCpoServer(
            service = LocationsCpoService(service = service),
            versionsRepository = InMemoryVersionsRepository(),
        ).registerOn(transportServer)
    }

    // It is recommended to start the server after setting up the routes to handle
    transportServer.start()
}

class CacheLocationsCpoRepository : LocationsCpoRepository {
    override suspend fun getLocations(
        dateFrom: Instant?,
        dateTo: Instant?,
        offset: Int,
        limit: Int?,
    ): SearchResult<Location> {
        TODO("Not yet implemented")
    }

    override suspend fun getLocation(locationId: String): Location? {
        TODO("Not yet implemented")
    }

    override suspend fun getEvse(locationId: String, evseUid: String): Evse? {
        TODO("Not yet implemented")
    }

    override suspend fun getConnector(locationId: String, evseUid: String, connectorId: String): Connector? {
        TODO("Not yet implemented")
    }
}
