package tests.mock

import common.toSearchResult
import io.mockk.every
import io.mockk.mockk
import io.mockk.slot
import ocpi.locations.domain.Location
import ocpi.locations.services.LocationsCpoService
import java.time.Instant

fun locationsCpoService(locations: List<Location>): LocationsCpoService = mockk {
    val locationId = slot<String>()
    val evseUid = slot<String>()
    val connectorId = slot<String>()
    val dateFrom = mutableListOf<Instant?>()
    val dateTo = mutableListOf<Instant?>()
    val offset = slot<Int>()
    val limit = mutableListOf<Int?>()

    every { getLocations(captureNullable(dateFrom), captureNullable(dateTo), capture(offset), captureNullable(limit)) } answers {
        val capturedOffset = offset.captured
        val capturedLimit = limit.captured() ?: 10

        locations
            .filter { loc ->
                val dateFromFilterOk = dateFrom.captured()?.let { loc.last_updated.isAfter(it) } ?: true
                val dateToFilterOk = dateTo.captured()?.let { loc.last_updated.isBefore(it) } ?: true

                dateFromFilterOk && dateToFilterOk
            }
            .filterIndexed { index: Int, _: Location ->
                index >= capturedOffset
            }
            .take(capturedLimit)
            .toSearchResult(totalCount = locations.size, limit = capturedLimit, offset = capturedOffset)
    }

    every { getLocation(capture(locationId)) } answers {
        locations.find { it.id == locationId.captured }
    }

    every { getEvse(capture(locationId), capture(evseUid)) } answers {
        locations
            .find { it.id == locationId.captured  }
            ?.evses
            ?.find { it.uid == evseUid.captured }
    }

    every { getConnector(capture(locationId), capture(evseUid), capture(connectorId)) } answers {
        locations
            .find { it.id == locationId.captured  }
            ?.evses
            ?.find { it.uid == evseUid.captured }
            ?.connectors
            ?.find { it.id == connectorId.captured }
    }
}