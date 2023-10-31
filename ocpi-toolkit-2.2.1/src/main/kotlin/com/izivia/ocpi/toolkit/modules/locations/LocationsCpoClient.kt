package com.izivia.ocpi.toolkit.modules.locations

import com.izivia.ocpi.toolkit.common.*
import com.izivia.ocpi.toolkit.modules.credentials.repositories.PlatformRepository
import com.izivia.ocpi.toolkit.modules.locations.domain.*
import com.izivia.ocpi.toolkit.modules.versions.domain.ModuleID
import com.izivia.ocpi.toolkit.transport.TransportClient
import com.izivia.ocpi.toolkit.transport.TransportClientBuilder
import com.izivia.ocpi.toolkit.transport.domain.HttpMethod
import com.izivia.ocpi.toolkit.transport.domain.HttpRequest

/**
 * Sends calls to an eMSP server
 * @property transportClientBuilder used to build transport client
 * @property serverVersionsEndpointUrl used to know which platform to communicate with
 * @property platformRepository used to get information about the platform (endpoint, token)
 */
class LocationsCpoClient(
    private val transportClientBuilder: TransportClientBuilder,
    private val serverVersionsEndpointUrl: String,
    private val platformRepository: PlatformRepository
) : LocationsEmspInterface {

    private suspend fun buildTransport(): TransportClient = transportClientBuilder
        .buildFor(
            module = ModuleID.locations,
            platform = serverVersionsEndpointUrl,
            platformRepository = platformRepository
        )

    override suspend fun getLocation(
        countryCode: CiString,
        partyId: CiString,
        locationId: CiString
    ): OcpiResponseBody<Location?> = with(buildTransport()) {
        send(
            HttpRequest(
                method = HttpMethod.GET,
                path = "/$countryCode/$partyId/$locationId"
            )
                .withRequiredHeaders(
                    requestId = generateRequestId(),
                    correlationId = generateCorrelationId()
                )
                .authenticate(platformRepository = platformRepository, platformUrl = serverVersionsEndpointUrl)
        )
            .parseBody()
    }

    override suspend fun getEvse(
        countryCode: CiString,
        partyId: CiString,
        locationId: CiString,
        evseUid: CiString
    ): OcpiResponseBody<Evse?> = with(buildTransport()) {
        send(
            HttpRequest(
                method = HttpMethod.GET,
                path = "/$countryCode/$partyId/$locationId/$evseUid"
            )
                .withRequiredHeaders(
                    requestId = generateRequestId(),
                    correlationId = generateCorrelationId()
                )
                .authenticate(platformRepository = platformRepository, platformUrl = serverVersionsEndpointUrl)
        )
            .parseBody()
    }

    override suspend fun getConnector(
        countryCode: CiString,
        partyId: CiString,
        locationId: CiString,
        evseUid: CiString,
        connectorId: CiString
    ): OcpiResponseBody<Connector?> = with(buildTransport()) {
        send(
            HttpRequest(
                method = HttpMethod.GET,
                path = "/$countryCode/$partyId/$locationId/$evseUid/$connectorId"
            )
                .withRequiredHeaders(
                    requestId = generateRequestId(),
                    correlationId = generateCorrelationId()
                )
                .authenticate(platformRepository = platformRepository, platformUrl = serverVersionsEndpointUrl)
        )
            .parseBody()
    }

    override suspend fun putLocation(
        countryCode: CiString,
        partyId: CiString,
        locationId: CiString,
        location: Location
    ): OcpiResponseBody<Location> = with(buildTransport()) {
        send(
            HttpRequest(
                method = HttpMethod.PUT,
                path = "/$countryCode/$partyId/$locationId",
                body = mapper.writeValueAsString(location)
            )
                .withRequiredHeaders(
                    requestId = generateRequestId(),
                    correlationId = generateCorrelationId()
                )
                .authenticate(platformRepository = platformRepository, platformUrl = serverVersionsEndpointUrl)
        )
            .parseBody()
    }

    override suspend fun putEvse(
        countryCode: CiString,
        partyId: CiString,
        locationId: CiString,
        evseUid: CiString,
        evse: Evse
    ): OcpiResponseBody<Evse> = with(buildTransport()) {
        send(
            HttpRequest(
                method = HttpMethod.PUT,
                path = "/$countryCode/$partyId/$locationId/$evseUid",
                body = mapper.writeValueAsString(evse)
            )
                .withRequiredHeaders(
                    requestId = generateRequestId(),
                    correlationId = generateCorrelationId()
                )
                .authenticate(platformRepository = platformRepository, platformUrl = serverVersionsEndpointUrl)
        )
            .parseBody()
    }

    override suspend fun putConnector(
        countryCode: CiString,
        partyId: CiString,
        locationId: CiString,
        evseUid: CiString,
        connectorId: CiString,
        connector: Connector
    ): OcpiResponseBody<Connector> = with(buildTransport()) {
        send(
            HttpRequest(
                method = HttpMethod.PUT,
                path = "/$countryCode/$partyId/$locationId/$evseUid/$connectorId",
                body = mapper.writeValueAsString(connector)
            )
                .withRequiredHeaders(
                    requestId = generateRequestId(),
                    correlationId = generateCorrelationId()
                )
                .authenticate(platformRepository = platformRepository, platformUrl = serverVersionsEndpointUrl)
        )
            .parseBody()
    }

    override suspend fun patchLocation(
        countryCode: CiString,
        partyId: CiString,
        locationId: CiString,
        location: LocationPartial
    ): OcpiResponseBody<Location?> = with(buildTransport()) {
        send(
            HttpRequest(
                method = HttpMethod.PATCH,
                path = "/$countryCode/$partyId/$locationId",
                body = mapper.writeValueAsString(location)
            )
                .withRequiredHeaders(
                    requestId = generateRequestId(),
                    correlationId = generateCorrelationId()
                )
                .authenticate(platformRepository = platformRepository, platformUrl = serverVersionsEndpointUrl)
        )
            .parseBody()
    }

    override suspend fun patchEvse(
        countryCode: CiString,
        partyId: CiString,
        locationId: CiString,
        evseUid: CiString,
        evse: EvsePartial
    ): OcpiResponseBody<Evse?> = with(buildTransport()) {
        send(
            HttpRequest(
                method = HttpMethod.PATCH,
                path = "/$countryCode/$partyId/$locationId",
                body = mapper.writeValueAsString(evse)
            )
                .withRequiredHeaders(
                    requestId = generateRequestId(),
                    correlationId = generateCorrelationId()
                )
                .authenticate(platformRepository = platformRepository, platformUrl = serverVersionsEndpointUrl)
        )
            .parseBody()
    }

    override suspend fun patchConnector(
        countryCode: CiString,
        partyId: CiString,
        locationId: CiString,
        evseUid: CiString,
        connectorId: CiString,
        connector: ConnectorPartial
    ): OcpiResponseBody<Connector?> = with(buildTransport()) {
        send(
            HttpRequest(
                method = HttpMethod.PATCH,
                path = "/$countryCode/$partyId/$locationId",
                body = mapper.writeValueAsString(connector)
            )
                .withRequiredHeaders(
                    requestId = generateRequestId(),
                    correlationId = generateCorrelationId()
                )
                .authenticate(platformRepository = platformRepository, platformUrl = serverVersionsEndpointUrl)
        )
            .parseBody()
    }
}
