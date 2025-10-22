package com.izivia.ocpi.toolkit.modules.chargingProfiles

import com.izivia.ocpi.toolkit.common.*
import com.izivia.ocpi.toolkit.modules.chargingProfiles.domain.ChargingProfile
import com.izivia.ocpi.toolkit.modules.chargingProfiles.domain.ChargingProfileResponse
import com.izivia.ocpi.toolkit.modules.chargingProfiles.domain.SetChargingProfile
import com.izivia.ocpi.toolkit.modules.credentials.repositories.PartnerRepository
import com.izivia.ocpi.toolkit.modules.versions.domain.ModuleID
import com.izivia.ocpi.toolkit.serialization.mapper
import com.izivia.ocpi.toolkit.serialization.serializeObject
import com.izivia.ocpi.toolkit.transport.TransportClient
import com.izivia.ocpi.toolkit.transport.TransportClientBuilder
import com.izivia.ocpi.toolkit.transport.domain.HttpMethod
import com.izivia.ocpi.toolkit.transport.domain.HttpRequest

class ChargingProfilesScspClient(
    private val transportClientBuilder: TransportClientBuilder,
    private val partnerId: String,
    private val partnerRepository: PartnerRepository,
    private val callbackBaseUrl: URL,
) {

    private suspend fun buildTransport(): TransportClient = transportClientBuilder
        .buildFor(
            module = ModuleID.chargingprofiles,
            partnerId = partnerId,
            partnerRepository = partnerRepository,
        )

    suspend fun getActiveChargingProfile(
        sessionId: CiString,
        duration: Int,
        requestId: String,
    ): ChargingProfileResponse = with(buildTransport()) {
        send(
            HttpRequest(
                method = HttpMethod.GET,
                path = "/$sessionId",
                queryParams = mapOf(
                    "duration" to duration.toString(),
                    "response_url" to "$callbackBaseUrl/" +
                        "${ChargingProfilesScspServer.ACTIVE_CHARGING_PROFILE_CALLBACK_URL}/$requestId",
                ),
            )
                .withRequiredHeaders(
                    requestId = generateRequestId(),
                    correlationId = generateCorrelationId(),
                )
                .authenticate(partnerRepository = partnerRepository, partnerId = partnerId),
        )
            .parseResult()
    }

    suspend fun putChargingProfile(
        sessionId: CiString,
        chargingProfile: ChargingProfile,
        requestId: String,
    ): ChargingProfileResponse = with(buildTransport()) {
        send(
            HttpRequest(
                method = HttpMethod.PUT,
                path = "/$sessionId",
                body = mapper.serializeObject(
                    SetChargingProfile(
                        chargingProfile = chargingProfile,
                        responseUrl = "$callbackBaseUrl/" +
                            "${ChargingProfilesScspServer.CHARGING_PROFILE_CALLBACK_URL}/$requestId",
                    ),
                ),
            )
                .withRequiredHeaders(
                    requestId = generateRequestId(),
                    correlationId = generateCorrelationId(),
                )
                .authenticate(partnerRepository = partnerRepository, partnerId = partnerId),
        )
            .parseResult()
    }

    suspend fun deleteChargingProfile(
        sessionId: CiString,
        requestId: String,
    ): ChargingProfileResponse = with(buildTransport()) {
        send(
            HttpRequest(
                method = HttpMethod.DELETE,
                path = "/$sessionId",
                queryParams = mapOf(
                    "response_url" to "$callbackBaseUrl/" +
                        "${ChargingProfilesScspServer.CLEAR_PROFILE_CALLBACK_URL}/$requestId",
                ),
            )
                .withRequiredHeaders(
                    requestId = generateRequestId(),
                    correlationId = generateCorrelationId(),
                )
                .authenticate(partnerRepository = partnerRepository, partnerId = partnerId),
        )
            .parseResult()
    }
}
