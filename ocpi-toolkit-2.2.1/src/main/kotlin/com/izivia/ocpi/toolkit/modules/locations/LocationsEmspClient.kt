package com.izivia.ocpi.toolkit.modules.locations

import com.izivia.ocpi.toolkit.common.*
import com.izivia.ocpi.toolkit.modules.locations.domain.Connector
import com.izivia.ocpi.toolkit.modules.locations.domain.Evse
import com.izivia.ocpi.toolkit.modules.locations.domain.Location
import com.izivia.ocpi.toolkit.modules.locations.domain.LocationPartial
import com.izivia.ocpi.toolkit.modules.versions.domain.InterfaceRole
import com.izivia.ocpi.toolkit.modules.versions.domain.ModuleID
import com.izivia.ocpi.toolkit.transport.TransportClient
import com.izivia.ocpi.toolkit.transport.domain.HttpMethod
import com.izivia.ocpi.toolkit.transport.domain.HttpRequest
import java.time.Instant

/**
 * Sends calls to the CPO
 * @property transportClientBuilder used to build transport client
 * @property partnerId used to know which partner to communicate with
 */
class LocationsEmspClient(
    private val transportClientBuilder: TransportClientBuilder,
    private val partnerId: String,
    private val ignoreInvalidListEntry: Boolean = false,
) : LocationsCpoInterface {

    private suspend fun buildTransport(): TransportClient = transportClientBuilder
        .buildFor(
            partnerId = partnerId,
            module = ModuleID.locations,
            role = InterfaceRole.SENDER,
        )

    override suspend fun getLocations(
        dateFrom: Instant?,
        dateTo: Instant?,
        offset: Int,
        limit: Int?,
    ): SearchResult<Location> = with(buildTransport()) {
        send(
            HttpRequest(
                method = HttpMethod.GET,
                queryParams = listOfNotNull(
                    dateFrom?.let { "date_from" to dateFrom.toString() },
                    dateTo?.let { "date_to" to dateTo.toString() },
                    "offset" to offset.toString(),
                    limit?.let { "limit" to limit.toString() },
                ).toMap(),
            ),
        ).let { res ->
            if (ignoreInvalidListEntry) {
                res.parseSearchResultIgnoringInvalid<Location, LocationPartial>(offset)
            } else {
                res.parseSearchResult<Location>(offset)
            }
        }
    }

    suspend fun getLocationsNextPage(
        previousResponse: SearchResult<Location>,
    ): SearchResult<Location>? = getNextPage<Location, LocationPartial>(
        transportClientBuilder = transportClientBuilder,
        partnerId = partnerId,
        previousResponse = previousResponse,
        ignoreInvalidListEntry = ignoreInvalidListEntry,
    )

    override suspend fun getLocation(locationId: CiString): Location? = with(buildTransport()) {
        send(
            HttpRequest(
                method = HttpMethod.GET,
                path = "/$locationId",
            ),
        )
            .parseOptionalResult()
    }

    override suspend fun getEvse(locationId: CiString, evseUid: CiString): Evse? =
        with(buildTransport()) {
            send(
                HttpRequest(
                    method = HttpMethod.GET,
                    path = "/$locationId/$evseUid",
                ),
            )
                .parseOptionalResult()
        }

    override suspend fun getConnector(
        locationId: CiString,
        evseUid: CiString,
        connectorId: CiString,
    ): Connector? = with(buildTransport()) {
        send(
            HttpRequest(
                method = HttpMethod.GET,
                path = "/$locationId/$evseUid/$connectorId",
            ),
        )
            .parseOptionalResult()
    }
}
