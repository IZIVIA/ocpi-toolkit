package com.izivia.ocpi.toolkit.modules.locations.mock

import com.izivia.ocpi.toolkit.common.toSearchResult
import com.izivia.ocpi.toolkit.modules.locations.domain.Location
import com.izivia.ocpi.toolkit.modules.locations.repositories.LocationsCpoRepository
import io.mockk.every
import io.mockk.mockk
import io.mockk.slot
import java.time.Instant
import java.util.*

fun locationsCpoRepository(locations: List<Location>): LocationsCpoRepository = mockk {
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
        locations.find { it.id.lowercase(Locale.ENGLISH) == locationId.captured.lowercase(Locale.ENGLISH) }
    }

    every { getEvse(capture(locationId), capture(evseUid)) } answers {
        locations
            .find { it.id.lowercase(Locale.ENGLISH) == locationId.captured.lowercase(Locale.ENGLISH)  }
            ?.evses
            ?.find { it.uid.lowercase(Locale.ENGLISH) == evseUid.captured.lowercase(Locale.ENGLISH) }
    }

    every { getConnector(capture(locationId), capture(evseUid), capture(connectorId)) } answers {
        locations
            .find { it.id.lowercase(Locale.ENGLISH) == locationId.captured.lowercase(Locale.ENGLISH)  }
            ?.evses
            ?.find { it.uid.lowercase(Locale.ENGLISH) == evseUid.captured.lowercase(Locale.ENGLISH) }
            ?.connectors
            ?.find { it.id.lowercase(Locale.ENGLISH) == connectorId.captured.lowercase(Locale.ENGLISH) }
    }
}
