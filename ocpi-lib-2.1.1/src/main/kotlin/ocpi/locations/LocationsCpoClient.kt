package ocpi.locations

import common.OcpiResponseBody
import common.authorizationHeader
import common.mapper
import common.parseBody
import ocpi.locations.domain.*
import transport.TransportClient
import transport.domain.HttpMethod
import transport.domain.HttpRequest

/**
 * Sends calls to an eMSP server
 * @property transportClient
 */
class LocationsCpoClient(
    private val transportClient: TransportClient
) : LocationsEmspInterface {

    override fun getLocation(
        token: String,
        countryCode: String,
        partyId: String,
        locationId: String
    ): OcpiResponseBody<Location?> =
        transportClient
            .send(
                HttpRequest(
                    method = HttpMethod.GET,
                    path = "/locations/$countryCode/$partyId/$locationId",
                    headers = mapOf(authorizationHeader(token = token))
                )
            )
            .parseBody()

    override fun getEvse(
        token: String,
        countryCode: String,
        partyId: String,
        locationId: String,
        evseUid: String
    ): OcpiResponseBody<Evse?> =
        transportClient
            .send(
                HttpRequest(
                    method = HttpMethod.GET,
                    path = "/locations/$countryCode/$partyId/$locationId/$evseUid",
                    headers = mapOf(authorizationHeader(token = token))
                )
            )
            .parseBody()

    override fun getConnector(
        token: String,
        countryCode: String,
        partyId: String,
        locationId: String,
        evseUid: String,
        connectorId: String
    ): OcpiResponseBody<Connector?> =
        transportClient
            .send(
                HttpRequest(
                    method = HttpMethod.GET,
                    path = "/locations/$countryCode/$partyId/$locationId/$evseUid/$connectorId",
                    headers = mapOf(authorizationHeader(token = token))
                )
            )
            .parseBody()

    override fun putLocation(
        token: String,
        countryCode: String,
        partyId: String,
        locationId: String,
        location: Location
    ): OcpiResponseBody<Location> =
        transportClient
            .send(
                HttpRequest(
                    method = HttpMethod.PUT,
                    path = "/locations/$countryCode/$partyId/$locationId",
                    body = mapper.writeValueAsString(location),
                    headers = mapOf(authorizationHeader(token = token))
                )
            )
            .parseBody()

    override fun putEvse(
        token: String,
        countryCode: String,
        partyId: String,
        locationId: String,
        evseUid: String,
        evse: Evse
    ): OcpiResponseBody<Evse> =
        transportClient
            .send(
                HttpRequest(
                    method = HttpMethod.PUT,
                    path = "/locations/$countryCode/$partyId/$locationId/$evseUid",
                    body = mapper.writeValueAsString(evse),
                    headers = mapOf(authorizationHeader(token = token))
                )
            )
            .parseBody()

    override fun putConnector(
        token: String,
        countryCode: String,
        partyId: String,
        locationId: String,
        evseUid: String,
        connectorId: String,
        connector: Connector
    ): OcpiResponseBody<Connector> =
        transportClient
            .send(
                HttpRequest(
                    method = HttpMethod.PUT,
                    path = "/locations/$countryCode/$partyId/$locationId/$evseUid/$connectorId",
                    body = mapper.writeValueAsString(connector),
                    headers = mapOf(authorizationHeader(token = token))
                )
            )
            .parseBody()

    override fun patchLocation(
        token: String,
        countryCode: String,
        partyId: String,
        locationId: String,
        location: LocationPartial
    ): OcpiResponseBody<Location?> = transportClient
        .send(
            HttpRequest(
                method = HttpMethod.PATCH,
                path = "/locations/$countryCode/$partyId/$locationId",
                body = mapper.writeValueAsString(location),
                headers = mapOf(authorizationHeader(token = token))
            )
        )
        .parseBody()

    override fun patchEvse(
        token: String,
        countryCode: String,
        partyId: String,
        locationId: String,
        evseUid: String,
        evse: EvsePartial
    ): OcpiResponseBody<Evse?> = transportClient
        .send(
            HttpRequest(
                method = HttpMethod.PATCH,
                path = "/locations/$countryCode/$partyId/$locationId",
                body = mapper.writeValueAsString(evse),
                headers = mapOf(authorizationHeader(token = token))
            )
        )
        .parseBody()

    override fun patchConnector(
        token: String,
        countryCode: String,
        partyId: String,
        locationId: String,
        evseUid: String,
        connectorId: String,
        connector: ConnectorPartial
    ): OcpiResponseBody<Connector?> = transportClient
        .send(
            HttpRequest(
                method = HttpMethod.PATCH,
                path = "/locations/$countryCode/$partyId/$locationId",
                body = mapper.writeValueAsString(connector)
            )
        )
        .parseBody()
}