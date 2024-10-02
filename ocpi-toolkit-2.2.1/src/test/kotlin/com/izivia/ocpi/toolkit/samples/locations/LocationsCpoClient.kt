package com.izivia.ocpi.toolkit.samples.locations

import com.izivia.ocpi.toolkit.modules.locations.LocationsCpoClient
import com.izivia.ocpi.toolkit.samples.common.Http4kTransportClientBuilder
import kotlinx.coroutines.runBlocking

/**
 * Example on how to use the CPO client
 */
fun main() {
    // We instantiate the clients that we want to use
    val locationsCpoClient = LocationsCpoClient(
        transportClientBuilder = Http4kTransportClientBuilder(),
        partnerId = emspServerVersionsUrl,
        partnerRepository = DUMMY_PLATFORM_REPOSITORY,
    )

    // We can use it
    println(
        runBlocking {
            locationsCpoClient.getLocation(countryCode = "fr", partyId = "abc", locationId = "location1")
        },
    )
}
