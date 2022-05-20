package ocpi.locations

import common.httpResponse
import common.mapper
import common.tokenFilter
import ocpi.credentials.repositories.PlatformRepository
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
    private val platformRepository: PlatformRepository,
    private val service: LocationsEmspInterface,
    basePath: List<FixedPathSegment> = listOf(
        FixedPathSegment("/locations")
    )
) {
    init {
        transportServer.handle(
            method = HttpMethod.GET,
            path = basePath + listOf(
                VariablePathSegment("countryCode"),
                VariablePathSegment("partyId"),
                VariablePathSegment("locationId")
            ),
            filters = listOf(platformRepository::tokenFilter)
        ) { req ->
            req.httpResponse {
                service
                    .getLocation(
                        countryCode = req.pathParams["countryCode"]!!,
                        partyId = req.pathParams["partyId"]!!,
                        locationId = req.pathParams["locationId"]!!
                    )
            }
        }

        transportServer.handle(
            method = HttpMethod.GET,
            path = basePath + listOf(
                VariablePathSegment("countryCode"),
                VariablePathSegment("partyId"),
                VariablePathSegment("locationId"),
                VariablePathSegment("evseUid")
            ),
            filters = listOf(platformRepository::tokenFilter)
        ) { req ->
            req.httpResponse {
                service
                    .getEvse(
                        countryCode = req.pathParams["countryCode"]!!,
                        partyId = req.pathParams["partyId"]!!,
                        locationId = req.pathParams["locationId"]!!,
                        evseUid = req.pathParams["evseUid"]!!
                    )
            }
        }

        transportServer.handle(
            method = HttpMethod.GET,
            path = basePath + listOf(
                VariablePathSegment("countryCode"),
                VariablePathSegment("partyId"),
                VariablePathSegment("locationId"),
                VariablePathSegment("evseUid"),
                VariablePathSegment("connectorId")
            ),
            filters = listOf(platformRepository::tokenFilter)
        ) { req ->
            req.httpResponse {
                service
                    .getConnector(
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
            path = basePath + listOf(
                VariablePathSegment("countryCode"),
                VariablePathSegment("partyId"),
                VariablePathSegment("locationId")
            ),
            filters = listOf(platformRepository::tokenFilter)
        ) { req ->
            req.httpResponse {
                service
                    .putLocation(
                        countryCode = req.pathParams["countryCode"]!!,
                        partyId = req.pathParams["partyId"]!!,
                        locationId = req.pathParams["locationId"]!!,
                        location = mapper.readValue(req.body, Location::class.java)
                    )
            }
        }

        transportServer.handle(
            method = HttpMethod.PUT,
            path = basePath + listOf(
                VariablePathSegment("countryCode"),
                VariablePathSegment("partyId"),
                VariablePathSegment("locationId"),
                VariablePathSegment("evseUid")
            ),
            filters = listOf(platformRepository::tokenFilter)
        ) { req ->
            req.httpResponse {
                service
                    .putEvse(
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
            path = basePath + listOf(
                VariablePathSegment("countryCode"),
                VariablePathSegment("partyId"),
                VariablePathSegment("locationId"),
                VariablePathSegment("evseUid"),
                VariablePathSegment("connectorId")
            ),
            filters = listOf(platformRepository::tokenFilter)
        ) { req ->
            req.httpResponse {
                service
                    .putConnector(
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
            path = basePath + listOf(
                VariablePathSegment("countryCode"),
                VariablePathSegment("partyId"),
                VariablePathSegment("locationId")
            ),
            filters = listOf(platformRepository::tokenFilter)
        ) { req ->
            req.httpResponse {
                service
                    .patchLocation(
                        countryCode = req.pathParams["countryCode"]!!,
                        partyId = req.pathParams["partyId"]!!,
                        locationId = req.pathParams["locationId"]!!,
                        location = mapper.readValue(req.body!!, LocationPartial::class.java)
                    )
            }
        }

        transportServer.handle(
            method = HttpMethod.PATCH,
            path = basePath + listOf(
                VariablePathSegment("countryCode"),
                VariablePathSegment("partyId"),
                VariablePathSegment("locationId"),
                VariablePathSegment("evseUid")
            ),
            filters = listOf(platformRepository::tokenFilter)
        ) { req ->
            req.httpResponse {
                service
                    .patchEvse(
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
            path = basePath + listOf(
                VariablePathSegment("countryCode"),
                VariablePathSegment("partyId"),
                VariablePathSegment("locationId"),
                VariablePathSegment("evseUid"),
                VariablePathSegment("connectorId")
            ),
            filters = listOf(platformRepository::tokenFilter)
        ) { req ->
            req.httpResponse {
                service
                    .patchConnector(
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
