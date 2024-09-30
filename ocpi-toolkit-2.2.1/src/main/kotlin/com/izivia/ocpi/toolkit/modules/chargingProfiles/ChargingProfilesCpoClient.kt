package com.izivia.ocpi.toolkit.modules.chargingProfiles

import com.izivia.ocpi.toolkit.common.*
import com.izivia.ocpi.toolkit.modules.chargingProfiles.domain.ActiveChargingProfile
import com.izivia.ocpi.toolkit.modules.chargingProfiles.domain.ActiveChargingProfileResult
import com.izivia.ocpi.toolkit.modules.chargingProfiles.domain.ChargingProfileResult
import com.izivia.ocpi.toolkit.modules.chargingProfiles.domain.ClearProfileResult
import com.izivia.ocpi.toolkit.modules.credentials.repositories.PartnerRepository
import com.izivia.ocpi.toolkit.modules.versions.domain.ModuleID
import com.izivia.ocpi.toolkit.transport.TransportClient
import com.izivia.ocpi.toolkit.transport.TransportClientBuilder
import com.izivia.ocpi.toolkit.transport.domain.HttpException
import com.izivia.ocpi.toolkit.transport.domain.HttpMethod
import com.izivia.ocpi.toolkit.transport.domain.HttpRequest
import com.izivia.ocpi.toolkit.transport.domain.HttpStatus

/**
 * Send calls to the SCSP
 *
 * @property transportClientBuilder used to build transport client
 * @property partnerId used to know which partner to communicate with
 * @property partnerRepository used to get information about the partner (endpoint, token)
 * @property callbackBaseUrl used to build the callback URL sent to the other partner
 */
class ChargingProfilesCpoClient(
    private val transportClientBuilder: TransportClientBuilder,
    private val partnerId: String,
    private val partnerRepository: PartnerRepository,
    private val callbackBaseUrl: String,
) {

    private fun buildCallbackTransport(): TransportClient =
        transportClientBuilder.build(callbackBaseUrl)

    private suspend fun buildTransport(): TransportClient = transportClientBuilder
        .buildFor(
            module = ModuleID.chargingprofiles,
            partnerId = partnerId,
            partnerRepository = partnerRepository,
        )

    suspend fun postCallbackActiveChargingProfile(
        responseUrl: String,
        result: ActiveChargingProfileResult,
    ): OcpiResponseBody<Any> = with(buildCallbackTransport()) {
        send(
            HttpRequest(
                method = HttpMethod.POST,
                path = responseUrl,
                body = mapper.writeValueAsString(result),
            )
                .withRequiredHeaders(
                    requestId = generateRequestId(),
                    correlationId = generateCorrelationId(),
                )
                .authenticate(partnerRepository = partnerRepository, partnerId = partnerId),
        )
            .also {
                if (it.status != HttpStatus.OK) throw HttpException(it.status, "status should be ${HttpStatus.OK}")
            }
            .parseBody()
    }

    suspend fun postCallbackChargingProfile(
        responseUrl: String,
        result: ChargingProfileResult,
    ): OcpiResponseBody<Any> = with(buildCallbackTransport()) {
        send(
            HttpRequest(
                method = HttpMethod.POST,
                path = responseUrl,
                body = mapper.writeValueAsString(result),
            )
                .withRequiredHeaders(
                    requestId = generateRequestId(),
                    correlationId = generateCorrelationId(),
                )
                .authenticate(partnerRepository = partnerRepository, partnerId = partnerId),
        )
            .also {
                if (it.status != HttpStatus.OK) throw HttpException(it.status, "status should be ${HttpStatus.OK}")
            }
            .parseBody()
    }

    suspend fun postCallbackClearProfile(
        responseUrl: String,
        result: ClearProfileResult,
    ): OcpiResponseBody<Any> = with(buildCallbackTransport()) {
        send(
            HttpRequest(
                method = HttpMethod.POST,
                path = responseUrl,
                body = mapper.writeValueAsString(result),
            )
                .withRequiredHeaders(
                    requestId = generateRequestId(),
                    correlationId = generateCorrelationId(),
                )
                .authenticate(partnerRepository = partnerRepository, partnerId = partnerId),
        )
            .also {
                if (it.status != HttpStatus.OK) throw HttpException(it.status, "status should be ${HttpStatus.OK}")
            }
            .parseBody()
    }

    suspend fun putActiveChargingProfile(
        sessionId: CiString,
        activeChargingProfile: ActiveChargingProfile,
    ): OcpiResponseBody<Any> = with(buildTransport()) {
        send(
            HttpRequest(
                method = HttpMethod.PUT,
                path = "/$sessionId",
                body = mapper.writeValueAsString(activeChargingProfile),
            )
                .withRequiredHeaders(
                    requestId = generateRequestId(),
                    correlationId = generateCorrelationId(),
                )
                .authenticate(partnerRepository = partnerRepository, partnerId = partnerId),
        )
            .also {
                if (it.status != HttpStatus.OK) throw HttpException(it.status, "status should be ${HttpStatus.OK}")
            }
            .parseBody()
    }
}
