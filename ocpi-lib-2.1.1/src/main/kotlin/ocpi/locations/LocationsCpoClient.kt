package ocpi.locations

import common.OcpiResponseBody
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

    override fun getLocation(countryCode: String, partyId: String, locationId: String): OcpiResponseBody<Location> =
        transportClient
            .send(
                HttpRequest(
                    method = HttpMethod.GET,
                    path = "/ocpi/emsp/2.1.1/locations/$countryCode/$partyId/$locationId"
                )
            )
            .parseBody()

    override fun getEvse(
        countryCode: String,
        partyId: String,
        locationId: String,
        evseUid: String
    ): OcpiResponseBody<Evse> =
        transportClient
            .send(
                HttpRequest(
                    method = HttpMethod.GET,
                    path = "/ocpi/emsp/2.1.1/locations/$countryCode/$partyId/$locationId/$evseUid"
                )
            )
            .parseBody()

    override fun getConnector(
        countryCode: String,
        partyId: String,
        locationId: String,
        evseUid: String,
        connectorId: String
    ): OcpiResponseBody<Connector> =
        transportClient
            .send(
                HttpRequest(
                    method = HttpMethod.GET,
                    path = "/ocpi/emsp/2.1.1/locations/$countryCode/$partyId/$locationId/$evseUid/$connectorId"
                )
            )
            .parseBody()

    override fun putLocation(
        countryCode: String,
        partyId: String,
        locationId: String,
        location: Location
    ): OcpiResponseBody<Location> =
        transportClient
            .send(
                HttpRequest(
                    method = HttpMethod.PUT,
                    path = "/ocpi/emsp/2.1.1/locations/$countryCode/$partyId/$locationId",
                    body = mapper.writeValueAsString(location)
                )
            )
            .parseBody()

    override fun putEvse(
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
                    path = "/ocpi/emsp/2.1.1/locations/$countryCode/$partyId/$locationId/$evseUid",
                    body = mapper.writeValueAsString(evse)
                )
            )
            .parseBody()

    override fun putConnector(
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
                    path = "/ocpi/emsp/2.1.1/locations/$countryCode/$partyId/$locationId/$evseUid/$connectorId",
                    body = mapper.writeValueAsString(connector)
                )
            )
            .parseBody()

    override fun patchLocation(
        countryCode: String,
        partyId: String,
        locationId: String,
        location: LocationPatch
    ): OcpiResponseBody<Location> = transportClient
        .send(
            HttpRequest(
                method = HttpMethod.PATCH,
                path = "/ocpi/emsp/2.1.1/locations/$countryCode/$partyId/$locationId",
                body = mapper.writeValueAsString(location)
            )
        )
        .parseBody()

    override fun patchEvse(
        countryCode: String,
        partyId: String,
        locationId: String,
        evseUid: String,
        evse: EvsePatch
    ): OcpiResponseBody<Evse> = transportClient
        .send(
            HttpRequest(
                method = HttpMethod.PATCH,
                path = "/ocpi/emsp/2.1.1/locations/$countryCode/$partyId/$locationId",
                body = mapper.writeValueAsString(evse)
            )
        )
        .parseBody()

    override fun patchConnector(
        countryCode: String,
        partyId: String,
        locationId: String,
        evseUid: String,
        connectorId: String,
        connector: ConnectorPatch
    ): OcpiResponseBody<Connector> = transportClient
        .send(
            HttpRequest(
                method = HttpMethod.PATCH,
                path = "/ocpi/emsp/2.1.1/locations/$countryCode/$partyId/$locationId",
                body = mapper.writeValueAsString(connector)
            )
        )
        .parseBody()
}