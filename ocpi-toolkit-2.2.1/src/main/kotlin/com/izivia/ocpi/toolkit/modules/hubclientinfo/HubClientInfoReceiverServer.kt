package com.izivia.ocpi.toolkit.modules.hubclientinfo

import com.izivia.ocpi.toolkit.common.OcpiSelfRegisteringModuleServer
import com.izivia.ocpi.toolkit.common.TimeProvider
import com.izivia.ocpi.toolkit.common.respondNothing
import com.izivia.ocpi.toolkit.common.respondObject
import com.izivia.ocpi.toolkit.modules.hubclientinfo.domain.ClientInfo
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

class HubClientInfoReceiverServer(
    private val service: HubClientInfoReceiverInterface,
    private val timeProvider: TimeProvider = TimeProvider { Instant.now() },
    versionsRepository: MutableVersionsRepository? = null,
    basePathOverride: String? = null,
) : OcpiSelfRegisteringModuleServer(
    ocpiVersion = VersionNumber.V2_2_1,
    moduleID = ModuleID.hubclientinfo,
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
            ),
        ) { req ->
            req.respondObject(timeProvider.now()) {
                service.get(
                    countryCode = req.pathParams["country_code"]!!,
                    partyId = req.pathParams["party_id"]!!,
                )
            }
        }

        transportServer.handle(
            method = HttpMethod.PUT,
            path = basePathSegments + listOf(
                VariablePathSegment("country_code"),
                VariablePathSegment("party_id"),
            ),
        ) { req ->
            req.respondNothing(timeProvider.now()) {
                service.put(
                    countryCode = req.pathParams["country_code"]!!,
                    partyId = req.pathParams["party_id"]!!,
                    clientInfo = mapper.deserializeObject<ClientInfo>(req.body!!),
                )
            }
        }
    }
}
