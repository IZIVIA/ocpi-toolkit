package com.izivia.ocpi.toolkit.modules.chargingProfiles

import com.izivia.ocpi.toolkit.common.OcpiSelfRegisteringModuleServer
import com.izivia.ocpi.toolkit.common.httpResponse
import com.izivia.ocpi.toolkit.common.mapper
import com.izivia.ocpi.toolkit.modules.chargingProfiles.domain.SetChargingProfile
import com.izivia.ocpi.toolkit.modules.versions.domain.InterfaceRole
import com.izivia.ocpi.toolkit.modules.versions.domain.ModuleID
import com.izivia.ocpi.toolkit.modules.versions.domain.VersionNumber
import com.izivia.ocpi.toolkit.modules.versions.repositories.MutableVersionsRepository
import com.izivia.ocpi.toolkit.transport.TransportServer
import com.izivia.ocpi.toolkit.transport.domain.HttpMethod
import com.izivia.ocpi.toolkit.transport.domain.VariablePathSegment

class ChargingProfilesCpoServer(
    private val service: ChargingProfilesCpoInterface,
    versionsRepository: MutableVersionsRepository? = null,
    basePathOverride: String? = null,
) : OcpiSelfRegisteringModuleServer(
    ocpiVersion = VersionNumber.V2_2_1,
    moduleID = ModuleID.chargingprofiles,
    interfaceRole = InterfaceRole.RECEIVER,
    versionsRepository = versionsRepository,
    basePathOverride = basePathOverride,
) {

    override suspend fun doRegisterOn(transportServer: TransportServer) {
        transportServer.handle(
            method = HttpMethod.GET,
            path = basePathSegments + listOf(
                VariablePathSegment("sessionId"),
            ),
            queryParams = listOf("duration", "response_url"),
        ) { req ->
            req.httpResponse {
                service
                    .getActiveChargingProfile(
                        sessionId = req.pathParams["sessionId"]!!,
                        duration = req.queryParams["duration"]!!.toInt(),
                        responseUrl = req.queryParams["response_url"]!!,
                    )
            }
        }

        transportServer.handle(
            method = HttpMethod.PUT,
            path = basePathSegments + listOf(
                VariablePathSegment("sessionId"),
            ),
        ) { req ->
            req.httpResponse {
                service
                    .putChargingProfile(
                        sessionId = req.pathParams["sessionId"]!!,
                        setChargingProfile = mapper.readValue(req.body, SetChargingProfile::class.java),
                    )
            }
        }

        transportServer.handle(
            method = HttpMethod.DELETE,
            path = basePathSegments + listOf(
                VariablePathSegment("sessionId"),
            ),
            queryParams = listOf("response_url"),
        ) { req ->
            req.httpResponse {
                service
                    .deleteChargingProfile(
                        sessionId = req.pathParams["sessionId"]!!,
                        responseUrl = req.queryParams["response_url"]!!,
                    )
            }
        }
    }
}
