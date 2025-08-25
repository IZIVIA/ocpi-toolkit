package com.izivia.ocpi.toolkit.modules.tokens.http.cpo

import com.izivia.ocpi.toolkit.common.TimeProvider
import com.izivia.ocpi.toolkit.modules.tokens.TokensCpoServer
import com.izivia.ocpi.toolkit.modules.tokens.repositories.TokensCpoRepository
import com.izivia.ocpi.toolkit.modules.tokens.services.TokensCpoService
import com.izivia.ocpi.toolkit.modules.versions.repositories.InMemoryVersionsRepository
import com.izivia.ocpi.toolkit.samples.common.Http4kTransportServer
import com.izivia.ocpi.toolkit.transport.TransportClient
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import java.time.Instant

internal const val nowString = "2015-06-30T21:59:59Z"
private val now = Instant.parse(nowString)

internal fun TokensCpoRepository.buildServer(): TransportClient {
    val timeProvider = mockk<TimeProvider>()
    every { timeProvider.now() } returns now

    val transportServer = Http4kTransportServer("http://localhost:1234", 1234)

    val repo = this
    runBlocking {
        TokensCpoServer(
            service = TokensCpoService(repo),
            timeProvider = timeProvider,
            versionsRepository = InMemoryVersionsRepository(),
            basePathOverride = "/tokens",
        ).registerOn(transportServer)
    }

    return transportServer.initRouterAndBuildClient()
}
