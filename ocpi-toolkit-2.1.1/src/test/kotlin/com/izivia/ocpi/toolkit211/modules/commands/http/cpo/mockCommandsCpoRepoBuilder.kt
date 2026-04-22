package com.izivia.ocpi.toolkit211.modules.commands.http.cpo

import com.izivia.ocpi.toolkit.transport.domain.HttpRequest
import com.izivia.ocpi.toolkit211.common.HttpAuthInterface
import com.izivia.ocpi.toolkit211.common.TestTransportClient
import com.izivia.ocpi.toolkit211.common.TimeProvider
import com.izivia.ocpi.toolkit211.modules.commands.CommandCpoInterface
import com.izivia.ocpi.toolkit211.modules.commands.CommandCpoServer
import com.izivia.ocpi.toolkit211.modules.versions.repositories.InMemoryVersionsRepository
import com.izivia.ocpi.toolkit211.samples.common.Http4kTransportServer
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import java.time.Instant

internal const val nowString = "2015-06-30T21:59:59Z"
private val now = Instant.parse(nowString)

internal fun CommandCpoInterface.buildServer(): TestTransportClient {
    val timeProvider = mockk<TimeProvider>()
    every { timeProvider.now() } returns now

    val httpAuth = mockk<HttpAuthInterface>()
    coEvery { httpAuth.partnerIdFromRequest(any<HttpRequest>()) } returns "partner-1"

    val transportServer = Http4kTransportServer("http://localhost:1234", 1234)

    val repo = this
    runBlocking {
        CommandCpoServer(
            httpAuth = httpAuth,
            service = repo,
            timeProvider = timeProvider,
            versionsRepository = InMemoryVersionsRepository(),
            basePathOverride = "/commands",
        ).registerOn(transportServer)
    }

    return TestTransportClient(transportServer.initRouterAndBuildClient())
}
