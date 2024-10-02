package com.izivia.ocpi.toolkit.modules.locations.mock

import com.izivia.ocpi.toolkit.modules.locations.domain.*
import com.izivia.ocpi.toolkit.modules.locations.repositories.LocationsEmspRepository
import io.mockk.coEvery
import io.mockk.mockk
import io.mockk.slot
import java.util.*

fun locationsEmspRepository(locations: List<Location>): LocationsEmspRepository = mockk {
    val countryCode = slot<String>()
    val partyId = slot<String>()
    val locationId = slot<String>()
    val evseUid = slot<String>()
    val connectorId = slot<String>()
    val location = slot<Location>()
    val evse = slot<Evse>()
    val connector = slot<Connector>()
    val locationPartial = slot<LocationPartial>()
    val evsePartial = slot<EvsePartial>()
    val connectorPartial = slot<ConnectorPartial>()

    coEvery { getLocation(capture(countryCode), capture(partyId), capture(locationId)) } coAnswers {
        locations.find { it.id.lowercase(Locale.ENGLISH) == locationId.captured.lowercase(Locale.ENGLISH) }
    }

    coEvery { getEvse(capture(countryCode), capture(partyId), capture(locationId), capture(evseUid)) } coAnswers {
        locations
            .find { it.id.lowercase(Locale.ENGLISH) == locationId.captured.lowercase(Locale.ENGLISH) }
            ?.evses
            ?.find { it.uid.lowercase(Locale.ENGLISH) == evseUid.captured.lowercase(Locale.ENGLISH) }
    }

    coEvery {
        getConnector(
            capture(countryCode),
            capture(partyId),
            capture(locationId),
            capture(evseUid),
            capture(connectorId),
        )
    } coAnswers {
        locations
            .find { it.id.lowercase(Locale.ENGLISH) == locationId.captured.lowercase(Locale.ENGLISH) }
            ?.evses
            ?.find { it.uid.lowercase(Locale.ENGLISH) == evseUid.captured.lowercase(Locale.ENGLISH) }
            ?.connectors
            ?.find { it.id.lowercase(Locale.ENGLISH) == connectorId.captured.lowercase(Locale.ENGLISH) }
    }

    coEvery {
        patchLocation(capture(countryCode), capture(partyId), capture(locationId), capture(locationPartial))
    } coAnswers {
        // Patch logic is not implemented
        locations
            .find { it.id.lowercase(Locale.ENGLISH) == locationId.captured.lowercase(Locale.ENGLISH) }
    }

    coEvery {
        patchEvse(capture(countryCode), capture(partyId), capture(locationId), capture(evseUid), capture(evsePartial))
    } coAnswers {
        // Patch logic is not implemented
        locations
            .find { it.id.lowercase(Locale.ENGLISH) == locationId.captured.lowercase(Locale.ENGLISH) }
            ?.evses
            ?.find { it.uid.lowercase(Locale.ENGLISH) == evseUid.captured.lowercase(Locale.ENGLISH) }
    }

    coEvery {
        patchConnector(
            capture(countryCode),
            capture(partyId),
            capture(locationId),
            capture(evseUid),
            capture(connectorId),
            capture(connectorPartial),
        )
    } coAnswers {
        // Patch logic is not implemented
        locations
            .find { it.id.lowercase(Locale.ENGLISH) == locationId.captured.lowercase(Locale.ENGLISH) }
            ?.evses
            ?.find { it.uid.lowercase(Locale.ENGLISH) == evseUid.captured.lowercase(Locale.ENGLISH) }
            ?.connectors
            ?.find { it.id.lowercase(Locale.ENGLISH) == connectorId.captured.lowercase(Locale.ENGLISH) }
    }

    coEvery { putLocation(capture(countryCode), capture(partyId), capture(locationId), capture(location)) } coAnswers {
        // Put logic is not implemented
        locations
            .find { it.id.lowercase(Locale.ENGLISH) == locationId.captured.lowercase(Locale.ENGLISH) }
            ?: location.captured
    }

    coEvery {
        putEvse(capture(countryCode), capture(partyId), capture(locationId), capture(evseUid), capture(evse))
    } coAnswers {
        // Put logic is not implemented
        locations
            .find { it.id.lowercase(Locale.ENGLISH) == locationId.captured.lowercase(Locale.ENGLISH) }
            ?.evses
            ?.find { it.uid.lowercase(Locale.ENGLISH) == evseUid.captured.lowercase(Locale.ENGLISH) }
            ?: evse.captured
    }

    coEvery {
        putConnector(
            capture(countryCode),
            capture(partyId),
            capture(locationId),
            capture(evseUid),
            capture(connectorId),
            capture(connector),
        )
    } coAnswers {
        // Put logic is not implemented
        locations
            .find { it.id.lowercase(Locale.ENGLISH) == locationId.captured.lowercase(Locale.ENGLISH) }
            ?.evses
            ?.find { it.uid.lowercase(Locale.ENGLISH) == evseUid.captured.lowercase(Locale.ENGLISH) }
            ?.connectors
            ?.find { it.id.lowercase(Locale.ENGLISH) == connectorId.captured.lowercase(Locale.ENGLISH) }
            ?: connector.captured
    }
}
