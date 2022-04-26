package ocpi.locations

import common.mapper
import transport.TransportServer
import transport.domain.HttpMethod
import transport.domain.HttpResponse
import transport.domain.PathSegment

/**
 * Receives calls from a CPO
 * @property transportClient
 */
class LocationsEmspServer(
    private val transportClient: TransportServer,
    private val callbacks: LocationsEmspInterface
) {

    init {
        transportClient.handle(
            HttpMethod.GET,
            listOf(
                PathSegment("/ocpi/emsp/2.0"),
                PathSegment("countryCode", true),
                PathSegment("partyId", true),
                PathSegment("locationId", true)
            )
        ) { pathParams, _ ->
            val location = callbacks.getLocation(
                countryCode = pathParams["countryCode"]!!,
                partyId = pathParams["partyId"]!!,
                locationId = pathParams["locationId"]!!
            )

            HttpResponse(
                status = 200,
                body = mapper.writeValueAsString(location)
            )
        }

        // TODO
    }
}