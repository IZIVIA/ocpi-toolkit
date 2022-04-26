package ocpi.locations

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
    private val controller: LocationsEmspInterface
) {

    init {
        transportServer.handle(
            HttpMethod.GET,
            listOf(
                FixedPathSegment("/ocpi/emsp/2.0"),
                VariablePathSegment("countryCode"),
                VariablePathSegment("partyId"),
                VariablePathSegment("locationId")
            )
        ) { req ->
            val location = controller.getLocation(
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
            HttpMethod.GET,
            listOf(
                FixedPathSegment("/ocpi/emsp/2.0"),
                VariablePathSegment("countryCode"),
                VariablePathSegment("partyId"),
                VariablePathSegment("locationId"),
                VariablePathSegment("evseUid")
            )
        ) { req ->
            val evse = controller.getEvse(
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
            HttpMethod.GET,
            listOf(
                FixedPathSegment("/ocpi/emsp/2.0"),
                VariablePathSegment("countryCode"),
                VariablePathSegment("partyId"),
                VariablePathSegment("locationId"),
                VariablePathSegment("evseUid"),
                VariablePathSegment("connectorId")
            )
        ) { req ->
            val connector = controller.getConnector(
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
            HttpMethod.PUT,
            listOf(
                FixedPathSegment("/ocpi/emsp/2.0"),
                VariablePathSegment("countryCode"),
                VariablePathSegment("partyId"),
                VariablePathSegment("locationId")
            )
        ) { req ->
            val location = controller.putLocation(
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
            HttpMethod.PUT,
            listOf(
                FixedPathSegment("/ocpi/emsp/2.0"),
                VariablePathSegment("countryCode"),
                VariablePathSegment("partyId"),
                VariablePathSegment("locationId"),
                VariablePathSegment("evseUid")
            )
        ) { req ->
            val evse = controller.putEvse(
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
            HttpMethod.PUT,
            listOf(
                FixedPathSegment("/ocpi/emsp/2.0"),
                VariablePathSegment("countryCode"),
                VariablePathSegment("partyId"),
                VariablePathSegment("locationId"),
                VariablePathSegment("evseUid"),
                VariablePathSegment("connectorId")
            )
        ) { req ->
            val connector = controller.putConnector(
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
            HttpMethod.PATCH,
            listOf(
                FixedPathSegment("/ocpi/emsp/2.0"),
                VariablePathSegment("countryCode"),
                VariablePathSegment("partyId"),
                VariablePathSegment("locationId")
            )
        ) { req ->
            val location = controller.patchLocation(
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
            HttpMethod.PATCH,
            listOf(
                FixedPathSegment("/ocpi/emsp/2.0"),
                VariablePathSegment("countryCode"),
                VariablePathSegment("partyId"),
                VariablePathSegment("locationId"),
                VariablePathSegment("evseUid")
            )
        ) { req ->
            val evse = controller.patchEvse(
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
            HttpMethod.PATCH,
            listOf(
                FixedPathSegment("/ocpi/emsp/2.0"),
                VariablePathSegment("countryCode"),
                VariablePathSegment("partyId"),
                VariablePathSegment("locationId"),
                VariablePathSegment("evseUid"),
                VariablePathSegment("connectorId")
            )
        ) { req ->
            val connector = controller.patchConnector(
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