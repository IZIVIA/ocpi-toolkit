package com.izivia.ocpi.toolkit.modules.locations

import com.izivia.ocpi.toolkit.common.*
import com.izivia.ocpi.toolkit.modules.credentials.repositories.PartnerRepository
import com.izivia.ocpi.toolkit.modules.locations.domain.Connector
import com.izivia.ocpi.toolkit.modules.locations.domain.Evse
import com.izivia.ocpi.toolkit.modules.locations.domain.Location
import com.izivia.ocpi.toolkit.modules.versions.domain.ModuleID
import com.izivia.ocpi.toolkit.transport.TransportClient
import com.izivia.ocpi.toolkit.transport.TransportClientBuilder
import com.izivia.ocpi.toolkit.transport.domain.HttpMethod
import com.izivia.ocpi.toolkit.transport.domain.HttpRequest
import java.time.Instant

/**
 * Sends calls to the CPO
 * @property transportClientBuilder used to build transport client
 * @property serverVersionsEndpointUrl used to know which partner to communicate with
 * @property partnerRepository used to get information about the partner (endpoint, token)
 */
class LocationsEmspClient(
    private val transportClientBuilder: TransportClientBuilder,
    private val serverVersionsEndpointUrl: String,
    private val partnerRepository: PartnerRepository
) : LocationsCpoInterface {

    private suspend fun buildTransport(): TransportClient = transportClientBuilder
        .buildFor(
            module = ModuleID.locations,
            partnerUrl = serverVersionsEndpointUrl,
            partnerRepository = partnerRepository
        )

    override suspend fun getLocations(
        dateFrom: Instant?,
        dateTo: Instant?,
        offset: Int,
        limit: Int?,
        countryCode: String?,
        partyId: String?
    ): OcpiResponseBody<SearchResult<Location>> = with(buildTransport()) {
        send(
            HttpRequest(
                method = HttpMethod.GET,
                queryParams = listOfNotNull(
                    dateFrom?.let { "date_from" to dateFrom.toString() },
                    dateTo?.let { "date_to" to dateTo.toString() },
                    "offset" to offset.toString(),
                    limit?.let { "limit" to limit.toString() }
                ).toMap(),
                headers = emptyMap<String, String>()
                    .add(Header.OCPI_TO_COUNTRY_CODE, countryCode)
                    .add(Header.OCPI_TO_PARTY_ID, partyId)
            )
                .withRequiredHeaders(
                    requestId = generateRequestId(),
                    correlationId = generateCorrelationId()
                )
                .authenticate(partnerRepository = partnerRepository, partnerUrl = serverVersionsEndpointUrl)
        )
            .parsePaginatedBody(offset)
    }

    override suspend fun getLocation(
        locationId: CiString,
        countryCode: String?,
        partyId: String?
    ): OcpiResponseBody<Location?> = with(buildTransport()) {
        send(
            HttpRequest(
                method = HttpMethod.GET,
                path = "/$locationId",
                headers = emptyMap<String, String>()
                    .add(Header.OCPI_TO_COUNTRY_CODE, countryCode)
                    .add(Header.OCPI_TO_PARTY_ID, partyId)
            )
                .withRequiredHeaders(
                    requestId = generateRequestId(),
                    correlationId = generateCorrelationId()
                )
                .authenticate(partnerRepository = partnerRepository, partnerUrl = serverVersionsEndpointUrl)
        )
            .parseBody()
    }

    override suspend fun getEvse(
        locationId: CiString,
        evseUid: CiString,
        countryCode: String?,
        partyId: String?
    ): OcpiResponseBody<Evse?> =
        with(buildTransport()) {
            send(
                HttpRequest(
                    method = HttpMethod.GET,
                    path = "/$locationId/$evseUid",
                    headers = emptyMap<String, String>()
                        .add(Header.OCPI_TO_COUNTRY_CODE, countryCode)
                        .add(Header.OCPI_TO_PARTY_ID, partyId)
                )
                    .withRequiredHeaders(
                        requestId = generateRequestId(),
                        correlationId = generateCorrelationId()
                    )
                    .authenticate(partnerRepository = partnerRepository, partnerUrl = serverVersionsEndpointUrl)
            )
                .parseBody()
        }

    override suspend fun getConnector(
        locationId: CiString,
        evseUid: CiString,
        connectorId: CiString,
        countryCode: String?,
        partyId: String?
    ): OcpiResponseBody<Connector?> = with(buildTransport()) {
        send(
            HttpRequest(
                method = HttpMethod.GET,
                path = "/$locationId/$evseUid/$connectorId",
                headers = emptyMap<String, String>()
                    .add(Header.OCPI_TO_COUNTRY_CODE, countryCode)
                    .add(Header.OCPI_TO_PARTY_ID, partyId)
            )
                .withRequiredHeaders(
                    requestId = generateRequestId(),
                    correlationId = generateCorrelationId()
                )
                .authenticate(partnerRepository = partnerRepository, partnerUrl = serverVersionsEndpointUrl)
        )
            .parseBody()
    }
}
