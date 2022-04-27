package ocpi.locations

import common.OcpiResponseBody
import common.mapper
import ocpi.locations.domain.*
import transport.TransportServer
import transport.domain.*

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
            val location = service.getLocation(
                countryCode = req.pathParams["countryCode"]!!,
                partyId = req.pathParams["partyId"]!!,
                locationId = req.pathParams["locationId"]!!
            )

            HttpResponse(
                status = 200,
                body = mapper.writeValueAsString(location)
            )
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
            val evse = service.getEvse(
                countryCode = req.pathParams["countryCode"]!!,
                partyId = req.pathParams["partyId"]!!,
                locationId = req.pathParams["locationId"]!!,
                evseUid = req.pathParams["evseUid"]!!
            )

            HttpResponse(
                status = 200,
                body = mapper.writeValueAsString(evse)
            )
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
            val connector = service.getConnector(
                countryCode = req.pathParams["countryCode"]!!,
                partyId = req.pathParams["partyId"]!!,
                locationId = req.pathParams["locationId"]!!,
                evseUid = req.pathParams["evseUid"]!!,
                connectorId = req.pathParams["connectorId"]!!
            )

            HttpResponse(
                status = 200,
                body = mapper.writeValueAsString(connector)
            )
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
            val location = service.putLocation(
                countryCode = req.pathParams["countryCode"]!!,
                partyId = req.pathParams["partyId"]!!,
                locationId = req.pathParams["locationId"]!!,
                location = mapper.readValue(req.body!!, Location::class.java)
            )

            HttpResponse(
                status = 200,
                body = mapper.writeValueAsString(location)
            )
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
            val evse = service.putEvse(
                countryCode = req.pathParams["countryCode"]!!,
                partyId = req.pathParams["partyId"]!!,
                locationId = req.pathParams["locationId"]!!,
                evseUid = req.pathParams["evseUid"]!!,
                evse = mapper.readValue(req.body!!, Evse::class.java)
            )

            HttpResponse(
                status = 200,
                body = mapper.writeValueAsString(evse)
            )
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
            val connector = service.putConnector(
                countryCode = req.pathParams["countryCode"]!!,
                partyId = req.pathParams["partyId"]!!,
                locationId = req.pathParams["locationId"]!!,
                evseUid = req.pathParams["evseUid"]!!,
                connectorId = req.pathParams["connectorId"]!!,
                connector = mapper.readValue(req.body!!, Connector::class.java)
            )

            HttpResponse(
                status = 200,
                body = mapper.writeValueAsString(connector)
            )
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
            val location = service.patchLocation(
                countryCode = req.pathParams["countryCode"]!!,
                partyId = req.pathParams["partyId"]!!,
                locationId = req.pathParams["locationId"]!!,
                location = mapper.readValue(req.body!!, LocationPatch::class.java)
            )

            HttpResponse(
                status = 200,
                body = mapper.writeValueAsString(location)
            )
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
            val evse = service.patchEvse(
                countryCode = req.pathParams["countryCode"]!!,
                partyId = req.pathParams["partyId"]!!,
                locationId = req.pathParams["locationId"]!!,
                evseUid = req.pathParams["evseUid"]!!,
                evse = mapper.readValue(req.body!!, EvsePatch::class.java)
            )

            HttpResponse(
                status = 200,
                body = mapper.writeValueAsString(evse)
            )
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
            val connector = service.patchConnector(
                countryCode = req.pathParams["countryCode"]!!,
                partyId = req.pathParams["partyId"]!!,
                locationId = req.pathParams["locationId"]!!,
                evseUid = req.pathParams["evseUid"]!!,
                connectorId = req.pathParams["connectorId"]!!,
                connector = mapper.readValue(req.body!!, ConnectorPatch::class.java)
            )

            HttpResponse(
                status = 200,
                body = mapper.writeValueAsString(connector)
            )
        }
    }
}
