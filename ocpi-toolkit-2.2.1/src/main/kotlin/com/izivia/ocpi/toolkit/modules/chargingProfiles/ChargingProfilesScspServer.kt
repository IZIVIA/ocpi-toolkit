package com.izivia.ocpi.toolkit.modules.chargingProfiles

import com.izivia.ocpi.toolkit.common.OcpiSelfRegisteringModuleServer
import com.izivia.ocpi.toolkit.common.httpResponse
import com.izivia.ocpi.toolkit.common.mapper
import com.izivia.ocpi.toolkit.modules.chargingProfiles.domain.ActiveChargingProfile
import com.izivia.ocpi.toolkit.modules.chargingProfiles.domain.ActiveChargingProfileResult
import com.izivia.ocpi.toolkit.modules.chargingProfiles.domain.ChargingProfileResult
import com.izivia.ocpi.toolkit.modules.chargingProfiles.domain.ClearProfileResult
import com.izivia.ocpi.toolkit.modules.versions.domain.InterfaceRole
import com.izivia.ocpi.toolkit.modules.versions.domain.ModuleID
import com.izivia.ocpi.toolkit.modules.versions.domain.VersionNumber
import com.izivia.ocpi.toolkit.modules.versions.repositories.MutableVersionsRepository
import com.izivia.ocpi.toolkit.transport.TransportServer
import com.izivia.ocpi.toolkit.transport.domain.FixedPathSegment
import com.izivia.ocpi.toolkit.transport.domain.HttpMethod
import com.izivia.ocpi.toolkit.transport.domain.VariablePathSegment

class ChargingProfilesScspServer(
    private val service: ChargingProfilesScspInterface,
    versionsRepository: MutableVersionsRepository? = null,
    basePathOverride: String? = null,
) : OcpiSelfRegisteringModuleServer(
    ocpiVersion = VersionNumber.V2_2_1,
    moduleID = ModuleID.chargingprofiles,
    interfaceRole = InterfaceRole.SENDER,
    versionsRepository = versionsRepository,
    basePathOverride = basePathOverride,
) {

    companion object {
        const val ACTIVE_CHARGING_PROFILE_CALLBACK_URL = "activechargingprofile/callback"
        const val CHARGING_PROFILE_CALLBACK_URL = "chargingprofile/callback"
        const val CLEAR_PROFILE_CALLBACK_URL = "clearprofile/callback"
        const val PUT_ACTIVE_CHARGING_PROFILE_URL = "activechargingprofile"
    }

    override suspend fun doRegisterOn(transportServer: TransportServer) {
        transportServer.handle(
            method = HttpMethod.POST,
            path = basePathSegments + listOf(
                FixedPathSegment(ACTIVE_CHARGING_PROFILE_CALLBACK_URL),
                VariablePathSegment("requestId"),
            ),
        ) { req ->
            req.httpResponse {
                service
                    .postCallbackActiveChargingProfile(
                        requestId = req.pathParams["requestId"].orEmpty(),
                        result = mapper.readValue(req.body, ActiveChargingProfileResult::class.java),
                    )
            }
        }

        transportServer.handle(
            method = HttpMethod.POST,
            path = basePathSegments + listOf(
                FixedPathSegment(CHARGING_PROFILE_CALLBACK_URL),
                VariablePathSegment("requestId"),
            ),
        ) { req ->
            req.httpResponse {
                service
                    .postCallbackChargingProfile(
                        requestId = req.pathParams["requestId"].orEmpty(),
                        result = mapper.readValue(req.body, ChargingProfileResult::class.java),
                    )
            }
        }

        transportServer.handle(
            method = HttpMethod.POST,
            path = basePathSegments + listOf(
                FixedPathSegment(CLEAR_PROFILE_CALLBACK_URL),
                VariablePathSegment("requestId"),
            ),
        ) { req ->
            req.httpResponse {
                service
                    .postCallbackClearProfile(
                        requestId = req.pathParams["requestId"].orEmpty(),
                        result = mapper.readValue(req.body, ClearProfileResult::class.java),
                    )
            }
        }

        transportServer.handle(
            method = HttpMethod.PUT,
            path = basePathSegments + listOf(
                FixedPathSegment(PUT_ACTIVE_CHARGING_PROFILE_URL),
                VariablePathSegment("sessionId"),
            ),
        ) { req ->
            req.httpResponse {
                service
                    .putActiveChargingProfile(
                        sessionId = req.pathParams["sessionId"].orEmpty(),
                        activeChargingProfile = mapper.readValue(req.body, ActiveChargingProfile::class.java),
                    )
            }
        }
    }
}
