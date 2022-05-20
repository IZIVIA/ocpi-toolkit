package ocpi.locations

import common.*
import ocpi.credentials.repositories.PlatformRepository
import ocpi.locations.domain.Connector
import ocpi.locations.domain.Evse
import ocpi.locations.domain.Location
import ocpi.versions.domain.ModuleID
import transport.TransportClient
import transport.TransportClientBuilder
import transport.domain.HttpMethod
import transport.domain.HttpRequest
import java.time.Instant

/**
 * Sends calls to the CPO
 * @property transportClientBuilder used to build transport client
 * @property serverVersionsEndpointUrl used to know which platform to communicate with
 * @property platformRepository used to get information about the platform (endpoint, token)
 */
class LocationsEmspClient(
    private val transportClientBuilder: TransportClientBuilder,
    private val serverVersionsEndpointUrl: String,
    private val platformRepository: PlatformRepository
) : LocationsCpoInterface {

    private fun buildTransport(): TransportClient = transportClientBuilder
        .buildFor(
            module = ModuleID.locations,
            platform = serverVersionsEndpointUrl,
            platformRepository = platformRepository
        )

    override fun getLocations(
        dateFrom: Instant?,
        dateTo: Instant?,
        offset: Int,
        limit: Int?
    ): OcpiResponseBody<SearchResult<Location>> =
        buildTransport()
            .send(
                HttpRequest(
                    method = HttpMethod.GET,
                    queryParams = listOfNotNull(
                        dateFrom?.let { "date_from" to dateFrom.toString() },
                        dateTo?.let { "date_to" to dateTo.toString() },
                        "offset" to offset.toString(),
                        limit?.let { "limit" to limit.toString() }
                    ).toMap()
                )
                    .withDebugHeaders()
                    .authenticate(platformRepository = platformRepository, baseUrl = serverVersionsEndpointUrl)
            )
            .parsePaginatedBody(offset)

    override fun getLocation(locationId: CiString): OcpiResponseBody<Location?> =
        buildTransport()
            .send(
                HttpRequest(
                    method = HttpMethod.GET,
                    path = "/$locationId",
                )
                    .withDebugHeaders()
                    .authenticate(platformRepository = platformRepository, baseUrl = serverVersionsEndpointUrl)
            )
            .parseBody()

    override fun getEvse(locationId: CiString, evseUid: CiString): OcpiResponseBody<Evse?> =
        buildTransport()
            .send(
                HttpRequest(
                    method = HttpMethod.GET,
                    path = "/$locationId/$evseUid",
                )
                    .withDebugHeaders()
                    .authenticate(platformRepository = platformRepository, baseUrl = serverVersionsEndpointUrl)
            )
            .parseBody()

    override fun getConnector(
        locationId: CiString,
        evseUid: CiString,
        connectorId: CiString
    ): OcpiResponseBody<Connector?> =
        buildTransport()
            .send(
                HttpRequest(
                    method = HttpMethod.GET,
                    path = "/$locationId/$evseUid/$connectorId",
                )
                    .withDebugHeaders()
                    .authenticate(platformRepository = platformRepository, baseUrl = serverVersionsEndpointUrl)
            )
            .parseBody()
}
