package ocpi.locations

import com.fasterxml.jackson.module.kotlin.readValue
import common.mapper
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

    override fun getLocation(countryCode: String, partyId: String, locationId: String): Location =
        transportClient
            .send(
                HttpRequest(
                    method = HttpMethod.GET,
                    path = "/ocpi/emsp/2.0/$countryCode/$partyId/$locationId"
                )
            )
            .run {
                mapper.readValue(this.body)
            }

    override fun getEvse(countryCode: String, partyId: String, locationId: String, evseUid: String): Evse =
        transportClient
            .send(
                HttpRequest(
                    method = HttpMethod.GET,
                    path = "/ocpi/emsp/2.0/$countryCode/$partyId/$locationId/$evseUid"
                )
            )
            .run {
                mapper.readValue(this.body)
            }

    override fun getConnector(
        countryCode: String,
        partyId: String,
        locationId: String,
        evseUid: String,
        connectorId: String
    ): Connector =
        transportClient
            .send(
                HttpRequest(
                    method = HttpMethod.GET,
                    path = "/ocpi/emsp/2.0/$countryCode/$partyId/$locationId/$evseUid/$connectorId"
                )
            )
            .run {
                mapper.readValue(this.body)
            }

    override fun putLocation(countryCode: String, partyId: String, locationId: String, location: Location): Location =
        transportClient
            .send(
                HttpRequest(
                    method = HttpMethod.PUT,
                    path = "/ocpi/emsp/2.0/$countryCode/$partyId/$locationId",
                    body = mapper.writeValueAsString(location)
                )
            )
            .run {
                mapper.readValue(this.body)
            }

    override fun putEvse(countryCode: String, partyId: String, locationId: String, evseUid: String, evse: Evse): Evse =
        transportClient
            .send(
                HttpRequest(
                    method = HttpMethod.PUT,
                    path = "/ocpi/emsp/2.0/$countryCode/$partyId/$locationId/$evseUid",
                    body = mapper.writeValueAsString(evse)
                )
            )
            .run {
                mapper.readValue(this.body)
            }

    override fun putConnector(
        countryCode: String,
        partyId: String,
        locationId: String,
        evseUid: String,
        connectorId: String,
        connector: Connector
    ): Connector =
        transportClient
            .send(
                HttpRequest(
                    method = HttpMethod.PUT,
                    path = "/ocpi/emsp/2.0/$countryCode/$partyId/$locationId/$evseUid/$connectorId",
                    body = mapper.writeValueAsString(connector)
                )
            )
            .run {
                mapper.readValue(this.body)
            }

    override fun patchLocation(
        countryCode: String,
        partyId: String,
        locationId: String,
        location: LocationPatch
    ): Location = transportClient
        .send(
            HttpRequest(
                method = HttpMethod.PATCH,
                path = "/ocpi/emsp/2.0/$countryCode/$partyId/$locationId",
                body = mapper.writeValueAsString(location)
            )
        )
        .run {
            mapper.readValue(this.body)
        }

    override fun patchEvse(
        countryCode: String,
        partyId: String,
        locationId: String,
        evseUid: String,
        evse: EvsePatch
    ): Evse = transportClient
        .send(
            HttpRequest(
                method = HttpMethod.PATCH,
                path = "/ocpi/emsp/2.0/$countryCode/$partyId/$locationId",
                body = mapper.writeValueAsString(evse)
            )
        )
        .run {
            mapper.readValue(this.body)
        }

    override fun patchConnector(
        countryCode: String,
        partyId: String,
        locationId: String,
        evseUid: String,
        connectorId: String,
        connector: ConnectorPatch
    ): Connector = transportClient
        .send(
            HttpRequest(
                method = HttpMethod.PATCH,
                path = "/ocpi/emsp/2.0/$countryCode/$partyId/$locationId",
                body = mapper.writeValueAsString(connector)
            )
        )
        .run {
            mapper.readValue(this.body)
        }
}