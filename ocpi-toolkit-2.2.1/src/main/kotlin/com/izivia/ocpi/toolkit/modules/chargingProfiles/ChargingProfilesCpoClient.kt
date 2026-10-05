package com.izivia.ocpi.toolkit.modules.chargingProfiles

import com.izivia.ocpi.toolkit.common.CiString
import com.izivia.ocpi.toolkit.common.TransportClientBuilder
import com.izivia.ocpi.toolkit.common.parseResultIgnoringData
import com.izivia.ocpi.toolkit.common.pathOf
import com.izivia.ocpi.toolkit.modules.chargingProfiles.domain.ActiveChargingProfile
import com.izivia.ocpi.toolkit.modules.chargingProfiles.domain.ActiveChargingProfileResult
import com.izivia.ocpi.toolkit.modules.chargingProfiles.domain.ChargingProfileResult
import com.izivia.ocpi.toolkit.modules.chargingProfiles.domain.ClearProfileResult
import com.izivia.ocpi.toolkit.modules.versions.domain.InterfaceRole
import com.izivia.ocpi.toolkit.modules.versions.domain.ModuleID
import com.izivia.ocpi.toolkit.serialization.mapper
import com.izivia.ocpi.toolkit.serialization.serializeObject
import com.izivia.ocpi.toolkit.transport.TransportClient
import com.izivia.ocpi.toolkit.transport.domain.HttpMethod
import com.izivia.ocpi.toolkit.transport.domain.HttpRequest

/**
 * Send calls to the SCSP
 * Acknowledgement data is ignored; nullable return types are retained for compatibility.
 *
 * @property transportClientBuilder used to build transport client
 * @property partnerId used to know which partner to communicate with
 * @property callbackBaseUrl used to build the callback URL sent to the other partner
 */
class ChargingProfilesCpoClient(
    private val transportClientBuilder: TransportClientBuilder,
    private val partnerId: String,
    private val callbackBaseUrl: String,
) {

    private suspend fun buildCallbackTransport(): TransportClient =
        transportClientBuilder.buildFor(partnerId, callbackBaseUrl)

    private suspend fun buildTransport(): TransportClient = transportClientBuilder
        .buildFor(
            partnerId = partnerId,
            module = ModuleID.chargingprofiles,
            role = InterfaceRole.SENDER,
        )

    suspend fun postCallbackActiveChargingProfile(
        responseUrl: String,
        result: ActiveChargingProfileResult,
    ): String? = with(buildCallbackTransport()) {
        send(
            HttpRequest(
                method = HttpMethod.POST,
                path = responseUrl,
                body = mapper.serializeObject(result),
            ),
        )
            .parseResultIgnoringData()
        null
    }

    suspend fun postCallbackChargingProfile(
        responseUrl: String,
        result: ChargingProfileResult,
    ): String? = with(buildCallbackTransport()) {
        send(
            HttpRequest(
                method = HttpMethod.POST,
                path = responseUrl,
                body = mapper.serializeObject(result),
            ),
        )
            .parseResultIgnoringData()
        null
    }

    suspend fun postCallbackClearProfile(
        responseUrl: String,
        result: ClearProfileResult,
    ): String? = with(buildCallbackTransport()) {
        send(
            HttpRequest(
                method = HttpMethod.POST,
                path = responseUrl,
                body = mapper.serializeObject(result),
            ),
        )
            .parseResultIgnoringData()
        null
    }

    suspend fun putActiveChargingProfile(
        sessionId: CiString,
        activeChargingProfile: ActiveChargingProfile,
    ): String? = with(buildTransport()) {
        send(
            HttpRequest(
                method = HttpMethod.PUT,
                path = pathOf(sessionId),
                body = mapper.serializeObject(activeChargingProfile),
            ),
        )
            .parseResultIgnoringData()
        null
    }
}
