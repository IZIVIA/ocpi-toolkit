package ocpi.locations

import com.fasterxml.jackson.core.JsonProcessingException
import common.mapper
import common.toHttpResponse
import ocpi.locations.domain.*
import transport.TransportServer
import transport.domain.FixedPathSegment
import transport.domain.HttpMethod
import transport.domain.HttpResponse
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
            service
                .getLocation(
                    countryCode = req.pathParams["countryCode"]!!,
                    partyId = req.pathParams["partyId"]!!,
                    locationId = req.pathParams["locationId"]!!
                )
                .toHttpResponse()
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
            service
                .getEvse(
                    countryCode = req.pathParams["countryCode"]!!,
                    partyId = req.pathParams["partyId"]!!,
                    locationId = req.pathParams["locationId"]!!,
                    evseUid = req.pathParams["evseUid"]!!
                )
                .toHttpResponse()
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
            service
                .getConnector(
                    countryCode = req.pathParams["countryCode"]!!,
                    partyId = req.pathParams["partyId"]!!,
                    locationId = req.pathParams["locationId"]!!,
                    evseUid = req.pathParams["evseUid"]!!,
                    connectorId = req.pathParams["connectorId"]!!
                )
                .toHttpResponse()
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
            try {
                service
                    .putLocation(
                        countryCode = req.pathParams["countryCode"]!!,
                        partyId = req.pathParams["partyId"]!!,
                        locationId = req.pathParams["locationId"]!!,
                        location = mapper.readValue(req.body, Location::class.java)
                    )
                    .toHttpResponse()
            } catch (e: JsonProcessingException) {
                e.printStackTrace()
                HttpResponse(status = 400)
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
            try {
                service
                    .putEvse(
                        countryCode = req.pathParams["countryCode"]!!,
                        partyId = req.pathParams["partyId"]!!,
                        locationId = req.pathParams["locationId"]!!,
                        evseUid = req.pathParams["evseUid"]!!,
                        evse = mapper.readValue(req.body!!, Evse::class.java)
                    )
                    .toHttpResponse()
            } catch (e: JsonProcessingException) {
                e.printStackTrace()
                HttpResponse(status = 400)
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
            try {
                 service
                     .putConnector(
                        countryCode = req.pathParams["countryCode"]!!,
                        partyId = req.pathParams["partyId"]!!,
                        locationId = req.pathParams["locationId"]!!,
                        evseUid = req.pathParams["evseUid"]!!,
                        connectorId = req.pathParams["connectorId"]!!,
                        connector = mapper.readValue(req.body!!, Connector::class.java)
                    )
                     .toHttpResponse()
            } catch (e: JsonProcessingException) {
                e.printStackTrace()
                HttpResponse(status = 400)
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
            try {
                service
                    .patchLocation(
                        countryCode = req.pathParams["countryCode"]!!,
                        partyId = req.pathParams["partyId"]!!,
                        locationId = req.pathParams["locationId"]!!,
                        location = mapper.readValue(req.body!!, LocationPartial::class.java)
                    )
                    .toHttpResponse()
            } catch (e: JsonProcessingException) {
                e.printStackTrace()
                HttpResponse(status = 400)
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
            try {
               service
                   .patchEvse(
                        countryCode = req.pathParams["countryCode"]!!,
                        partyId = req.pathParams["partyId"]!!,
                        locationId = req.pathParams["locationId"]!!,
                        evseUid = req.pathParams["evseUid"]!!,
                        evse = mapper.readValue(req.body!!, EvsePartial::class.java)
                    )
                   .toHttpResponse()
            } catch (e: JsonProcessingException) {
                e.printStackTrace()
                HttpResponse(status = 400)
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
            try {
                service
                    .patchConnector(
                        countryCode = req.pathParams["countryCode"]!!,
                        partyId = req.pathParams["partyId"]!!,
                        locationId = req.pathParams["locationId"]!!,
                        evseUid = req.pathParams["evseUid"]!!,
                        connectorId = req.pathParams["connectorId"]!!,
                        connector = mapper.readValue(req.body!!, ConnectorPartial::class.java)
                    )
                    .toHttpResponse()
            } catch (e: JsonProcessingException) {
                e.printStackTrace()
                HttpResponse(status = 400)
            }
        }
    }
}
