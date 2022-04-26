package ocpi.locations

import common.mapper
import transport.TransportServer
import transport.domain.*

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
                FixedPathSegment("/ocpi/emsp/2.0"),
                VariablePathSegment("countryCode"),
                VariablePathSegment("partyId"),
                VariablePathSegment("locationId")
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