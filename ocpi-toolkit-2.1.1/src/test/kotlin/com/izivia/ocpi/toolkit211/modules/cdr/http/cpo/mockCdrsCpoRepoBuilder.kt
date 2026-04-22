package com.izivia.ocpi.toolkit211.modules.cdr.http.cpo

import com.izivia.ocpi.toolkit211.common.TestTransportClient
import com.izivia.ocpi.toolkit211.common.TimeProvider
import com.izivia.ocpi.toolkit211.modules.cdr.CdrsCpoInterface
import com.izivia.ocpi.toolkit211.modules.cdr.CdrsCpoServer
import com.izivia.ocpi.toolkit211.modules.cdr.services.CdrsCpoValidator
import com.izivia.ocpi.toolkit211.modules.versions.repositories.InMemoryVersionsRepository
import com.izivia.ocpi.toolkit211.samples.common.Http4kTransportServer
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import java.time.Instant

internal const val nowString = "2015-06-30T21:59:59Z"
private val now = Instant.parse(nowString)

internal fun CdrsCpoInterface.buildServer(): TestTransportClient {
    val timeProvider = mockk<TimeProvider>()
    every { timeProvider.now() } returns now

    val transportServer = Http4kTransportServer("http://localhost:1234", 1234)

    val repo = this
    runBlocking {
        CdrsCpoServer(
            service = CdrsCpoValidator(repo),
            timeProvider = timeProvider,
            versionsRepository = InMemoryVersionsRepository(),
            basePathOverride = "/cdrs",
        ).registerOn(transportServer)
    }

    return TestTransportClient(transportServer.initRouterAndBuildClient())
}
