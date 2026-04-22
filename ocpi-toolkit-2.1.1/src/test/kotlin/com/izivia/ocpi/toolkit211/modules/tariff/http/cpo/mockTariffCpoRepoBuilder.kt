package com.izivia.ocpi.toolkit211.modules.tariff.http.cpo

import com.izivia.ocpi.toolkit211.common.TestTransportClient
import com.izivia.ocpi.toolkit211.common.TimeProvider
import com.izivia.ocpi.toolkit211.modules.tariff.TariffCpoInterface
import com.izivia.ocpi.toolkit211.modules.tariff.TariffCpoServer
import com.izivia.ocpi.toolkit211.modules.tariff.services.TariffCpoValidator
import com.izivia.ocpi.toolkit211.modules.versions.repositories.InMemoryVersionsRepository
import com.izivia.ocpi.toolkit211.samples.common.Http4kTransportServer
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import java.time.Instant

internal const val nowString = "2015-06-30T21:59:59Z"
private val now = Instant.parse(nowString)

internal fun TariffCpoInterface.buildServer(): TestTransportClient {
    val timeProvider = mockk<TimeProvider>()
    every { timeProvider.now() } returns now

    val transportServer = Http4kTransportServer("http://localhost:1234", 1234)

    val repo = this
    runBlocking {
        TariffCpoServer(
            service = TariffCpoValidator(repo),
            timeProvider = timeProvider,
            versionsRepository = InMemoryVersionsRepository(),
            basePathOverride = "/tariffs",
        ).registerOn(transportServer)
    }

    return TestTransportClient(transportServer.initRouterAndBuildClient())
}
