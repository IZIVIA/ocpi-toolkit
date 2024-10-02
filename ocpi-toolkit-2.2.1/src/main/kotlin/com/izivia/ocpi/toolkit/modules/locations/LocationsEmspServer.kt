package com.izivia.ocpi.toolkit.modules.locations

import com.izivia.ocpi.toolkit.common.OcpiSelfRegisteringModuleServer
import com.izivia.ocpi.toolkit.common.httpResponse
import com.izivia.ocpi.toolkit.common.mapper
import com.izivia.ocpi.toolkit.modules.locations.domain.*
import com.izivia.ocpi.toolkit.modules.versions.domain.InterfaceRole
import com.izivia.ocpi.toolkit.modules.versions.domain.ModuleID
import com.izivia.ocpi.toolkit.modules.versions.domain.VersionNumber
import com.izivia.ocpi.toolkit.modules.versions.repositories.MutableVersionsRepository
import com.izivia.ocpi.toolkit.transport.TransportServer
import com.izivia.ocpi.toolkit.transport.domain.HttpMethod
import com.izivia.ocpi.toolkit.transport.domain.VariablePathSegment

class LocationsEmspServer(
    private val service: LocationsEmspInterface,
    versionsRepository: MutableVersionsRepository? = null,
    basePathOverride: String? = null,
) : OcpiSelfRegisteringModuleServer(
    ocpiVersion = VersionNumber.V2_2_1,
    moduleID = ModuleID.locations,
    interfaceRole = InterfaceRole.RECEIVER,
    versionsRepository = versionsRepository,
    basePathOverride = basePathOverride,
) {

    override suspend fun doRegisterOn(transportServer: TransportServer) {
        transportServer.handle(
            method = HttpMethod.GET,
            path = basePathSegments + listOf(
                VariablePathSegment("countryCode"),
                VariablePathSegment("partyId"),
                VariablePathSegment("locationId"),
            ),
        ) { req ->
            req.httpResponse {
                service
                    .getLocation(
                        countryCode = req.pathParams["countryCode"]!!,
                        partyId = req.pathParams["partyId"]!!,
                        locationId = req.pathParams["locationId"]!!,
                    )
            }
        }

        transportServer.handle(
            method = HttpMethod.GET,
            path = basePathSegments + listOf(
                VariablePathSegment("countryCode"),
                VariablePathSegment("partyId"),
                VariablePathSegment("locationId"),
                VariablePathSegment("evseUid"),
            ),
        ) { req ->
            req.httpResponse {
                service
                    .getEvse(
                        countryCode = req.pathParams["countryCode"]!!,
                        partyId = req.pathParams["partyId"]!!,
                        locationId = req.pathParams["locationId"]!!,
                        evseUid = req.pathParams["evseUid"]!!,
                    )
            }
        }

        transportServer.handle(
            method = HttpMethod.GET,
            path = basePathSegments + listOf(
                VariablePathSegment("countryCode"),
                VariablePathSegment("partyId"),
                VariablePathSegment("locationId"),
                VariablePathSegment("evseUid"),
                VariablePathSegment("connectorId"),
            ),
        ) { req ->
            req.httpResponse {
                service
                    .getConnector(
                        countryCode = req.pathParams["countryCode"]!!,
                        partyId = req.pathParams["partyId"]!!,
                        locationId = req.pathParams["locationId"]!!,
                        evseUid = req.pathParams["evseUid"]!!,
                        connectorId = req.pathParams["connectorId"]!!,
                    )
            }
        }

        transportServer.handle(
            method = HttpMethod.PUT,
            path = basePathSegments + listOf(
                VariablePathSegment("countryCode"),
                VariablePathSegment("partyId"),
                VariablePathSegment("locationId"),
            ),
        ) { req ->
            req.httpResponse {
                service
                    .putLocation(
                        countryCode = req.pathParams["countryCode"]!!,
                        partyId = req.pathParams["partyId"]!!,
                        locationId = req.pathParams["locationId"]!!,
                        location = mapper.readValue(req.body, Location::class.java),
                    )
            }
        }

        transportServer.handle(
            method = HttpMethod.PUT,
            path = basePathSegments + listOf(
                VariablePathSegment("countryCode"),
                VariablePathSegment("partyId"),
                VariablePathSegment("locationId"),
                VariablePathSegment("evseUid"),
            ),
        ) { req ->
            req.httpResponse {
                service
                    .putEvse(
                        countryCode = req.pathParams["countryCode"]!!,
                        partyId = req.pathParams["partyId"]!!,
                        locationId = req.pathParams["locationId"]!!,
                        evseUid = req.pathParams["evseUid"]!!,
                        evse = mapper.readValue(req.body!!, Evse::class.java),
                    )
            }
        }

        transportServer.handle(
            method = HttpMethod.PUT,
            path = basePathSegments + listOf(
                VariablePathSegment("countryCode"),
                VariablePathSegment("partyId"),
                VariablePathSegment("locationId"),
                VariablePathSegment("evseUid"),
                VariablePathSegment("connectorId"),
            ),
        ) { req ->
            req.httpResponse {
                service
                    .putConnector(
                        countryCode = req.pathParams["countryCode"]!!,
                        partyId = req.pathParams["partyId"]!!,
                        locationId = req.pathParams["locationId"]!!,
                        evseUid = req.pathParams["evseUid"]!!,
                        connectorId = req.pathParams["connectorId"]!!,
                        connector = mapper.readValue(req.body!!, Connector::class.java),
                    )
            }
        }

        transportServer.handle(
            method = HttpMethod.PATCH,
            path = basePathSegments + listOf(
                VariablePathSegment("countryCode"),
                VariablePathSegment("partyId"),
                VariablePathSegment("locationId"),
            ),
        ) { req ->
            req.httpResponse {
                service
                    .patchLocation(
                        countryCode = req.pathParams["countryCode"]!!,
                        partyId = req.pathParams["partyId"]!!,
                        locationId = req.pathParams["locationId"]!!,
                        location = mapper.readValue(req.body!!, LocationPartial::class.java),
                    )
            }
        }

        transportServer.handle(
            method = HttpMethod.PATCH,
            path = basePathSegments + listOf(
                VariablePathSegment("countryCode"),
                VariablePathSegment("partyId"),
                VariablePathSegment("locationId"),
                VariablePathSegment("evseUid"),
            ),
        ) { req ->
            req.httpResponse {
                service
                    .patchEvse(
                        countryCode = req.pathParams["countryCode"]!!,
                        partyId = req.pathParams["partyId"]!!,
                        locationId = req.pathParams["locationId"]!!,
                        evseUid = req.pathParams["evseUid"]!!,
                        evse = mapper.readValue(req.body!!, EvsePartial::class.java),
                    )
            }
        }

        transportServer.handle(
            method = HttpMethod.PATCH,
            path = basePathSegments + listOf(
                VariablePathSegment("countryCode"),
                VariablePathSegment("partyId"),
                VariablePathSegment("locationId"),
                VariablePathSegment("evseUid"),
                VariablePathSegment("connectorId"),
            ),
        ) { req ->
            req.httpResponse {
                service
                    .patchConnector(
                        countryCode = req.pathParams["countryCode"]!!,
                        partyId = req.pathParams["partyId"]!!,
                        locationId = req.pathParams["locationId"]!!,
                        evseUid = req.pathParams["evseUid"]!!,
                        connectorId = req.pathParams["connectorId"]!!,
                        connector = mapper.readValue(req.body!!, ConnectorPartial::class.java),
                    )
            }
        }
    }
}
