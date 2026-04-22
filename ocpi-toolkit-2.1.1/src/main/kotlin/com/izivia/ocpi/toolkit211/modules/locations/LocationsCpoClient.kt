package com.izivia.ocpi.toolkit211.modules.locations

import com.izivia.ocpi.toolkit.transport.TransportClient
import com.izivia.ocpi.toolkit.transport.domain.HttpMethod
import com.izivia.ocpi.toolkit.transport.domain.HttpRequest
import com.izivia.ocpi.toolkit211.common.*
import com.izivia.ocpi.toolkit211.modules.locations.domain.*
import com.izivia.ocpi.toolkit211.modules.versions.domain.ModuleID
import com.izivia.ocpi.toolkit211.serialization.mapper
import com.izivia.ocpi.toolkit211.serialization.serializeObject

/**
 * Sends calls to an eMSP server
 * @property transportClientBuilder used to build transport client
 * @property partnerId used to know which partner to communicate with
 */
class LocationsCpoClient(
    private val transportClientBuilder: TransportClientBuilder,
    private val partnerId: String,
) : LocationsEmspInterface {

    private suspend fun buildTransport(): TransportClient = transportClientBuilder
        .buildFor(
            partnerId = partnerId,
            module = ModuleID.locations,
        )

    override suspend fun getLocation(
        countryCode: CiString,
        partyId: CiString,
        locationId: CiString,
    ): Location? = with(buildTransport()) {
        send(
            HttpRequest(
                method = HttpMethod.GET,
                path = "/$countryCode/$partyId/$locationId",
            ),
        ).parseOptionalResult()
    }

    override suspend fun getEvse(
        countryCode: CiString,
        partyId: CiString,
        locationId: CiString,
        evseUid: CiString,
    ): Evse? = with(buildTransport()) {
        send(
            HttpRequest(
                method = HttpMethod.GET,
                path = "/$countryCode/$partyId/$locationId/$evseUid",
            ),
        ).parseOptionalResult()
    }

    override suspend fun getConnector(
        countryCode: CiString,
        partyId: CiString,
        locationId: CiString,
        evseUid: CiString,
        connectorId: CiString,
    ): Connector? = with(buildTransport()) {
        send(
            HttpRequest(
                method = HttpMethod.GET,
                path = "/$countryCode/$partyId/$locationId/$evseUid/$connectorId",
            ),
        ).parseOptionalResult()
    }

    override suspend fun putLocation(
        countryCode: CiString,
        partyId: CiString,
        locationId: CiString,
        location: Location,
    ): LocationPartial = with(buildTransport()) {
        send(
            HttpRequest(
                method = HttpMethod.PUT,
                path = "/$countryCode/$partyId/$locationId",
                body = mapper.serializeObject(location),
            ),
        ).parseResultOrNull() ?: LocationPartial()
    }

    override suspend fun putEvse(
        countryCode: CiString,
        partyId: CiString,
        locationId: CiString,
        evseUid: CiString,
        evse: Evse,
    ): EvsePartial = with(buildTransport()) {
        send(
            HttpRequest(
                method = HttpMethod.PUT,
                path = "/$countryCode/$partyId/$locationId/$evseUid",
                body = mapper.serializeObject(evse),
            ),
        ).parseResultOrNull() ?: EvsePartial()
    }

    override suspend fun putConnector(
        countryCode: CiString,
        partyId: CiString,
        locationId: CiString,
        evseUid: CiString,
        connectorId: CiString,
        connector: Connector,
    ): ConnectorPartial = with(buildTransport()) {
        send(
            HttpRequest(
                method = HttpMethod.PUT,
                path = "/$countryCode/$partyId/$locationId/$evseUid/$connectorId",
                body = mapper.serializeObject(connector),
            ),
        ).parseResultOrNull() ?: ConnectorPartial()
    }

    override suspend fun patchLocation(
        countryCode: CiString,
        partyId: CiString,
        locationId: CiString,
        location: LocationPartial,
    ): LocationPartial? = with(buildTransport()) {
        send(
            HttpRequest(
                method = HttpMethod.PATCH,
                path = "/$countryCode/$partyId/$locationId",
                body = mapper.serializeObject(location),
            ),
        ).parseResultOrNull()
    }

    override suspend fun patchEvse(
        countryCode: CiString,
        partyId: CiString,
        locationId: CiString,
        evseUid: CiString,
        evse: EvsePartial,
    ): EvsePartial? = with(buildTransport()) {
        send(
            HttpRequest(
                method = HttpMethod.PATCH,
                path = "/$countryCode/$partyId/$locationId/$evseUid",
                body = mapper.serializeObject(evse),
            ),
        ).parseResultOrNull()
    }

    override suspend fun patchConnector(
        countryCode: CiString,
        partyId: CiString,
        locationId: CiString,
        evseUid: CiString,
        connectorId: CiString,
        connector: ConnectorPartial,
    ): ConnectorPartial? = with(buildTransport()) {
        send(
            HttpRequest(
                method = HttpMethod.PATCH,
                path = "/$countryCode/$partyId/$locationId/$evseUid/$connectorId",
                body = mapper.serializeObject(connector),
            ),
        ).parseResultOrNull()
    }
}
