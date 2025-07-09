package com.izivia.ocpi.toolkit.modules.locations

import com.izivia.ocpi.toolkit.common.*
import com.izivia.ocpi.toolkit.modules.locations.domain.*
import com.izivia.ocpi.toolkit.modules.versions.domain.InterfaceRole
import com.izivia.ocpi.toolkit.modules.versions.domain.ModuleID
import com.izivia.ocpi.toolkit.modules.versions.domain.VersionNumber
import com.izivia.ocpi.toolkit.modules.versions.repositories.MutableVersionsRepository
import com.izivia.ocpi.toolkit.transport.TransportServer
import com.izivia.ocpi.toolkit.transport.domain.HttpMethod
import com.izivia.ocpi.toolkit.transport.domain.VariablePathSegment
import java.time.Instant

class LocationsEmspServer(
    private val service: LocationsEmspInterface,
    private val timeProvider: TimeProvider = TimeProvider { Instant.now() },
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
            req.respondObject(timeProvider.now()) {
                service
                    .getLocation(
                        countryCode = req.pathParam("countryCode"),
                        partyId = req.pathParam("partyId"),
                        locationId = req.pathParam("locationId"),
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
            req.respondObject(timeProvider.now()) {
                service
                    .getEvse(
                        countryCode = req.pathParam("countryCode"),
                        partyId = req.pathParam("partyId"),
                        locationId = req.pathParam("locationId"),
                        evseUid = req.pathParam("evseUid"),
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
            req.respondObject(timeProvider.now()) {
                service
                    .getConnector(
                        countryCode = req.pathParam("countryCode"),
                        partyId = req.pathParam("partyId"),
                        locationId = req.pathParam("locationId"),
                        evseUid = req.pathParam("evseUid"),
                        connectorId = req.pathParam("connectorId"),
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
            req.respondObject(timeProvider.now()) {
                service
                    .putLocation(
                        countryCode = req.pathParam("countryCode"),
                        partyId = req.pathParam("partyId"),
                        locationId = req.pathParam("locationId"),
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
            req.respondObject(timeProvider.now()) {
                service
                    .putEvse(
                        countryCode = req.pathParam("countryCode"),
                        partyId = req.pathParam("partyId"),
                        locationId = req.pathParam("locationId"),
                        evseUid = req.pathParam("evseUid"),
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
            req.respondObject(timeProvider.now()) {
                service
                    .putConnector(
                        countryCode = req.pathParam("countryCode"),
                        partyId = req.pathParam("partyId"),
                        locationId = req.pathParam("locationId"),
                        evseUid = req.pathParam("evseUid"),
                        connectorId = req.pathParam("connectorId"),
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
            req.respondObject(timeProvider.now()) {
                service
                    .patchLocation(
                        countryCode = req.pathParam("countryCode"),
                        partyId = req.pathParam("partyId"),
                        locationId = req.pathParam("locationId"),
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
            req.respondObject(timeProvider.now()) {
                service
                    .patchEvse(
                        countryCode = req.pathParam("countryCode"),
                        partyId = req.pathParam("partyId"),
                        locationId = req.pathParam("locationId"),
                        evseUid = req.pathParam("evseUid"),
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
            req.respondObject(timeProvider.now()) {
                service
                    .patchConnector(
                        countryCode = req.pathParam("countryCode"),
                        partyId = req.pathParam("partyId"),
                        locationId = req.pathParam("locationId"),
                        evseUid = req.pathParam("evseUid"),
                        connectorId = req.pathParam("connectorId"),
                        connector = mapper.readValue(req.body!!, ConnectorPartial::class.java),
                    )
            }
        }
    }
}
