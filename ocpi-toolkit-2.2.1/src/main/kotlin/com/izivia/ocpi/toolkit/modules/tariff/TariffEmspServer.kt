package com.izivia.ocpi.toolkit.modules.tariff

import com.izivia.ocpi.toolkit.common.OcpiSelfRegisteringModuleServer
import com.izivia.ocpi.toolkit.common.TimeProvider
import com.izivia.ocpi.toolkit.common.respondNothing
import com.izivia.ocpi.toolkit.common.respondObject
import com.izivia.ocpi.toolkit.modules.tariff.domain.Tariff
import com.izivia.ocpi.toolkit.modules.versions.domain.InterfaceRole
import com.izivia.ocpi.toolkit.modules.versions.domain.ModuleID
import com.izivia.ocpi.toolkit.modules.versions.domain.VersionNumber
import com.izivia.ocpi.toolkit.modules.versions.repositories.MutableVersionsRepository
import com.izivia.ocpi.toolkit.serialization.deserializeObject
import com.izivia.ocpi.toolkit.serialization.mapper
import com.izivia.ocpi.toolkit.transport.TransportServer
import com.izivia.ocpi.toolkit.transport.domain.HttpMethod
import com.izivia.ocpi.toolkit.transport.domain.VariablePathSegment
import java.time.Instant

class TariffEmspServer(
    private val service: TariffEmspInterface,
    private val timeProvider: TimeProvider = TimeProvider { Instant.now() },
    versionsRepository: MutableVersionsRepository? = null,
    basePathOverride: String? = null,
) : OcpiSelfRegisteringModuleServer(
    ocpiVersion = VersionNumber.V2_2_1,
    moduleID = ModuleID.tariffs,
    interfaceRole = InterfaceRole.RECEIVER,
    versionsRepository = versionsRepository,
    basePathOverride = basePathOverride,
) {

    override suspend fun doRegisterOn(transportServer: TransportServer) {
        transportServer.handle(
            method = HttpMethod.GET,
            path = basePathSegments + listOf(
                VariablePathSegment("country_code"),
                VariablePathSegment("party_id"),
                VariablePathSegment("tariff_id"),
            ),
        ) { req ->
            req.respondObject(timeProvider.now()) {
                service
                    .getTariff(
                        countryCode = req.pathParams["country_code"]!!,
                        partyId = req.pathParams["party_id"]!!,
                        tariffId = req.pathParams["tariff_id"]!!,
                    )
            }
        }

        transportServer.handle(
            method = HttpMethod.PUT,
            path = basePathSegments + listOf(
                VariablePathSegment("country_code"),
                VariablePathSegment("party_id"),
                VariablePathSegment("tariff_id"),
            ),
        ) { req ->
            req.respondObject(timeProvider.now()) {
                service
                    .putTariff(
                        countryCode = req.pathParams["country_code"]!!,
                        partyId = req.pathParams["party_id"]!!,
                        tariffId = req.pathParams["tariff_id"]!!,
                        tariff = mapper.deserializeObject<Tariff>(req.body),
                    )
            }
        }

        transportServer.handle(
            method = HttpMethod.DELETE,
            path = basePathSegments + listOf(
                VariablePathSegment("country_code"),
                VariablePathSegment("party_id"),
                VariablePathSegment("tariff_id"),
            ),
        ) { req ->
            req.respondNothing(timeProvider.now()) {
                service
                    .deleteTariff(
                        countryCode = req.pathParams["country_code"]!!,
                        partyId = req.pathParams["party_id"]!!,
                        tariffId = req.pathParams["tariff_id"]!!,
                    )
            }
        }
    }
}
