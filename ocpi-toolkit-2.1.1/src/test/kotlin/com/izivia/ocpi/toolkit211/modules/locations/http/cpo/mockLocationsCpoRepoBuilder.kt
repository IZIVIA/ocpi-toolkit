package com.izivia.ocpi.toolkit211.modules.locations.http.cpo

import com.izivia.ocpi.toolkit211.common.TestTransportClient
import com.izivia.ocpi.toolkit211.common.TimeProvider
import com.izivia.ocpi.toolkit211.modules.locations.LocationsCpoInterface
import com.izivia.ocpi.toolkit211.modules.locations.LocationsCpoServer
import com.izivia.ocpi.toolkit211.modules.locations.services.LocationsCpoValidator
import com.izivia.ocpi.toolkit211.modules.versions.repositories.InMemoryVersionsRepository
import com.izivia.ocpi.toolkit211.samples.common.Http4kTransportServer
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import java.time.Instant

internal const val nowString = "2015-06-30T21:59:59Z"
private val now = Instant.parse(nowString)

internal fun LocationsCpoInterface.buildServer(): TestTransportClient {
    val timeProvider = mockk<TimeProvider>()
    every { timeProvider.now() } returns now

    val transportServer = Http4kTransportServer("http://localhost:1234", 1234)

    val repo = this
    runBlocking {
        LocationsCpoServer(
            service = LocationsCpoValidator(repo),
            timeProvider = timeProvider,
            versionsRepository = InMemoryVersionsRepository(),
            basePathOverride = "/locations",
        ).registerOn(transportServer)
    }

    return TestTransportClient(transportServer.initRouterAndBuildClient())
}
