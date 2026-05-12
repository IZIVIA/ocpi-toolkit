package com.izivia.ocpi.toolkit211.modules.tariff

import com.izivia.ocpi.toolkit.transport.TransportServer
import com.izivia.ocpi.toolkit.transport.domain.HttpMethod
import com.izivia.ocpi.toolkit.transport.domain.VariablePathSegment
import com.izivia.ocpi.toolkit211.common.*
import com.izivia.ocpi.toolkit211.modules.tariff.domain.Tariff
import com.izivia.ocpi.toolkit211.modules.versions.domain.ModuleID
import com.izivia.ocpi.toolkit211.modules.versions.domain.VersionNumber
import com.izivia.ocpi.toolkit211.modules.versions.repositories.MutableVersionsRepository
import com.izivia.ocpi.toolkit211.serialization.deserializeObject
import com.izivia.ocpi.toolkit211.serialization.mapper
import java.time.Instant

class TariffEmspServer(
    private val service: TariffEmspInterface,
    private val timeProvider: TimeProvider = TimeProvider { Instant.now() },
    versionsRepository: MutableVersionsRepository? = null,
    basePathOverride: String? = null,
) : OcpiSelfRegisteringModuleServer(
    ocpiVersion = VersionNumber.V2_1_1,
    moduleID = ModuleID.tariffs,
    versionsRepository = versionsRepository,
    basePathOverride = basePathOverride,
) {

    override suspend fun doRegisterOn(transportServer: TransportServer) {
        transportServer.handle(
            method = HttpMethod.GET,
            path = basePathSegments + listOf(
                VariablePathSegment("countryCode"),
                VariablePathSegment("partyId"),
                VariablePathSegment("tariffId"),
            ),
        ) { req ->
            req.respondObject(timeProvider.now()) {
                service.getTariff(
                    countryCode = req.pathParam("countryCode"),
                    partyId = req.pathParam("partyId"),
                    tariffId = req.pathParam("tariffId"),
                )
            }
        }

        transportServer.handle(
            method = HttpMethod.PUT,
            path = basePathSegments + listOf(
                VariablePathSegment("countryCode"),
                VariablePathSegment("partyId"),
                VariablePathSegment("tariffId"),
            ),
        ) { req ->
            req.respondObject(timeProvider.now()) {
                service.putTariff(
                    countryCode = req.pathParam("countryCode"),
                    partyId = req.pathParam("partyId"),
                    tariffId = req.pathParam("tariffId"),
                    tariff = mapper.deserializeObject<Tariff>(req.body),
                )
            }
        }

        transportServer.handle(
            method = HttpMethod.DELETE,
            path = basePathSegments + listOf(
                VariablePathSegment("countryCode"),
                VariablePathSegment("partyId"),
                VariablePathSegment("tariffId"),
            ),
        ) { req ->
            req.respondNothing(timeProvider.now()) {
                service.deleteTariff(
                    countryCode = req.pathParam("countryCode"),
                    partyId = req.pathParam("partyId"),
                    tariffId = req.pathParam("tariffId"),
                )
            }
        }
    }
}
