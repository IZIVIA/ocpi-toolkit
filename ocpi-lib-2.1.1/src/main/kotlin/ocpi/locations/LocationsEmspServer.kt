package ocpi.locations

import common.mapper
import transport.TransportServer
import transport.domain.HttpMethod
import transport.domain.HttpResponse
import transport.domain.PathSegment

/**
 * Receives calls from a CPO
 * @property transportServer
 */
class LocationsEmspServer(
    private val transportServer: TransportServer,
    private val callbacks: LocationsEmspInterface
) {

    init {
        transportServer.handle(
            HttpMethod.GET,
            listOf(
                PathSegment("/ocpi/emsp/2.0"),
                PathSegment("countryCode", true), // TODO
                PathSegment("partyId", true),
                PathSegment("locationId", true)
            )
        ) { req ->
            val location = callbacks.getLocation(
                countryCode = req.pathParams["countryCode"]!!,
                partyId = req.pathParams["partyId"]!!,
                locationId = req.pathParams["locationId"]!!
            )

            HttpResponse(
                status = 200,
                body = mapper.writeValueAsString(location)
            )
        }

        // TODO
    }
}