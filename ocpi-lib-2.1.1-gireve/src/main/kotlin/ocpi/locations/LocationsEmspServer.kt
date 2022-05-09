package ocpi.locations

import common.httpResponse
import common.mapper
import common.parseAuthorizationHeader
import ocpi.locations.domain.*
import transport.TransportServer
import transport.domain.FixedPathSegment
import transport.domain.HttpMethod
import transport.domain.VariablePathSegment

/**
 * Receives calls from a CPO
 * @property transportServer
 */
class LocationsEmspServer(
    private val transportServer: TransportServer,
    private val service: LocationsEmspInterface
) {
    init {
        transportServer.handle(
            method = HttpMethod.GET,
            path = listOf(
                FixedPathSegment("/ocpi/emsp/2.1.1/locations"),
                VariablePathSegment("countryCode"),
                VariablePathSegment("partyId"),
                VariablePathSegment("locationId")
            )
        ) { req ->
            req.httpResponse {
                service
                    .getLocation(
                        token = req.parseAuthorizationHeader(),
                        countryCode = req.pathParams["countryCode"]!!,
                        partyId = req.pathParams["partyId"]!!,
                        locationId = req.pathParams["locationId"]!!
                    )
            }
        }

        transportServer.handle(
            method = HttpMethod.GET,
            path = listOf(
                FixedPathSegment("/ocpi/emsp/2.1.1/locations"),
                VariablePathSegment("countryCode"),
                VariablePathSegment("partyId"),
                VariablePathSegment("locationId"),
                VariablePathSegment("evseUid")
            )
        ) { req ->
            req.httpResponse {
                service
                    .getEvse(
                        token = req.parseAuthorizationHeader(),
                        countryCode = req.pathParams["countryCode"]!!,
                        partyId = req.pathParams["partyId"]!!,
                        locationId = req.pathParams["locationId"]!!,
                        evseUid = req.pathParams["evseUid"]!!
                    )
            }
        }

        transportServer.handle(
            method = HttpMethod.GET,
            path = listOf(
                FixedPathSegment("/ocpi/emsp/2.1.1/locations"),
                VariablePathSegment("countryCode"),
                VariablePathSegment("partyId"),
                VariablePathSegment("locationId"),
                VariablePathSegment("evseUid"),
                VariablePathSegment("connectorId")
            )
        ) { req ->
            req.httpResponse {
                service
                    .getConnector(
                        token = req.parseAuthorizationHeader(),
                        countryCode = req.pathParams["countryCode"]!!,
                        partyId = req.pathParams["partyId"]!!,
                        locationId = req.pathParams["locationId"]!!,
                        evseUid = req.pathParams["evseUid"]!!,
                        connectorId = req.pathParams["connectorId"]!!
                    )
            }
        }

        transportServer.handle(
            method = HttpMethod.PUT,
            path = listOf(
                FixedPathSegment("/ocpi/emsp/2.1.1/locations"),
                VariablePathSegment("countryCode"),
                VariablePathSegment("partyId"),
                VariablePathSegment("locationId")
            )
        ) { req ->
            req.httpResponse {
                service
                    .putLocation(
                        token = req.parseAuthorizationHeader(),
                        countryCode = req.pathParams["countryCode"]!!,
                        partyId = req.pathParams["partyId"]!!,
                        locationId = req.pathParams["locationId"]!!,
                        location = mapper.readValue(req.body, Location::class.java)
                    )
            }
        }

        transportServer.handle(
            method = HttpMethod.PUT,
            path = listOf(
                FixedPathSegment("/ocpi/emsp/2.1.1/locations"),
                VariablePathSegment("countryCode"),
                VariablePathSegment("partyId"),
                VariablePathSegment("locationId"),
                VariablePathSegment("evseUid")
            )
        ) { req ->
            req.httpResponse {
                service
                    .putEvse(
                        token = req.parseAuthorizationHeader(),
                        countryCode = req.pathParams["countryCode"]!!,
                        partyId = req.pathParams["partyId"]!!,
                        locationId = req.pathParams["locationId"]!!,
                        evseUid = req.pathParams["evseUid"]!!,
                        evse = mapper.readValue(req.body!!, Evse::class.java)
                    )
            }
        }

        transportServer.handle(
            method = HttpMethod.PUT,
            path = listOf(
                FixedPathSegment("/ocpi/emsp/2.1.1/locations"),
                VariablePathSegment("countryCode"),
                VariablePathSegment("partyId"),
                VariablePathSegment("locationId"),
                VariablePathSegment("evseUid"),
                VariablePathSegment("connectorId")
            )
        ) { req ->
            req.httpResponse {
                service
                    .putConnector(
                        token = req.parseAuthorizationHeader(),
                        countryCode = req.pathParams["countryCode"]!!,
                        partyId = req.pathParams["partyId"]!!,
                        locationId = req.pathParams["locationId"]!!,
                        evseUid = req.pathParams["evseUid"]!!,
                        connectorId = req.pathParams["connectorId"]!!,
                        connector = mapper.readValue(req.body!!, Connector::class.java)
                    )
            }
        }

        transportServer.handle(
            method = HttpMethod.PATCH,
            path = listOf(
                FixedPathSegment("/ocpi/emsp/2.1.1/locations"),
                VariablePathSegment("countryCode"),
                VariablePathSegment("partyId"),
                VariablePathSegment("locationId")
            )
        ) { req ->
            req.httpResponse {
                service
                    .patchLocation(
                        token = req.parseAuthorizationHeader(),
                        countryCode = req.pathParams["countryCode"]!!,
                        partyId = req.pathParams["partyId"]!!,
                        locationId = req.pathParams["locationId"]!!,
                        location = mapper.readValue(req.body!!, LocationPartial::class.java)
                    )
            }
        }

        transportServer.handle(
            method = HttpMethod.PATCH,
            path = listOf(
                FixedPathSegment("/ocpi/emsp/2.1.1/locations"),
                VariablePathSegment("countryCode"),
                VariablePathSegment("partyId"),
                VariablePathSegment("locationId"),
                VariablePathSegment("evseUid")
            )
        ) { req ->
            req.httpResponse {
                service
                    .patchEvse(
                        token = req.parseAuthorizationHeader(),
                        countryCode = req.pathParams["countryCode"]!!,
                        partyId = req.pathParams["partyId"]!!,
                        locationId = req.pathParams["locationId"]!!,
                        evseUid = req.pathParams["evseUid"]!!,
                        evse = mapper.readValue(req.body!!, EvsePartial::class.java)
                    )
            }
        }

        transportServer.handle(
            method = HttpMethod.PATCH,
            path = listOf(
                FixedPathSegment("/ocpi/emsp/2.1.1/locations"),
                VariablePathSegment("countryCode"),
                VariablePathSegment("partyId"),
                VariablePathSegment("locationId"),
                VariablePathSegment("evseUid"),
                VariablePathSegment("connectorId")
            )
        ) { req ->
            req.httpResponse {
                service
                    .patchConnector(
                        token = req.parseAuthorizationHeader(),
                        countryCode = req.pathParams["countryCode"]!!,
                        partyId = req.pathParams["partyId"]!!,
                        locationId = req.pathParams["locationId"]!!,
                        evseUid = req.pathParams["evseUid"]!!,
                        connectorId = req.pathParams["connectorId"]!!,
                        connector = mapper.readValue(req.body!!, ConnectorPartial::class.java)
                    )
            }
        }
    }
}
