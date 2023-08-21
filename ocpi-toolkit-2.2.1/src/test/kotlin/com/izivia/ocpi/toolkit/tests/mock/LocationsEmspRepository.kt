package com.izivia.ocpi.toolkit.tests.mock

import com.izivia.ocpi.toolkit.modules.locations.domain.*
import com.izivia.ocpi.toolkit.modules.locations.repositories.LocationsEmspRepository
import io.mockk.every
import io.mockk.mockk
import io.mockk.slot
import java.util.*

fun locationsEmspService(locations: List<Location>): LocationsEmspRepository = mockk {
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

    every { getLocation(capture(countryCode), capture(partyId), capture(locationId)) } answers {
        locations.find { it.id.lowercase(Locale.ENGLISH) == locationId.captured.lowercase(Locale.ENGLISH) }
    }

    every { getEvse(capture(countryCode), capture(partyId), capture(locationId), capture(evseUid)) } answers {
        locations
            .find { it.id.lowercase(Locale.ENGLISH) == locationId.captured.lowercase(Locale.ENGLISH) }
            ?.evses
            ?.find { it.uid.lowercase(Locale.ENGLISH) == evseUid.captured.lowercase(Locale.ENGLISH) }
    }

    every { getConnector(capture(countryCode), capture(partyId), capture(locationId), capture(evseUid), capture(connectorId)) } answers {
        locations
            .find { it.id.lowercase(Locale.ENGLISH) == locationId.captured.lowercase(Locale.ENGLISH)  }
            ?.evses
            ?.find { it.uid.lowercase(Locale.ENGLISH) == evseUid.captured.lowercase(Locale.ENGLISH) }
            ?.connectors
            ?.find { it.id.lowercase(Locale.ENGLISH) == connectorId.captured.lowercase(Locale.ENGLISH) }
    }

    every { patchLocation(capture(countryCode), capture(partyId), capture(locationId), capture(locationPartial)) } answers {
        // Patch logic is not implemented
        locations
            .find { it.id.lowercase(Locale.ENGLISH) == locationId.captured.lowercase(Locale.ENGLISH) }
    }

    every { patchEvse(capture(countryCode), capture(partyId), capture(locationId), capture(evseUid), capture(evsePartial)) } answers {
        // Patch logic is not implemented
        locations
            .find { it.id.lowercase(Locale.ENGLISH) == locationId.captured.lowercase(Locale.ENGLISH) }
            ?.evses
            ?.find { it.uid.lowercase(Locale.ENGLISH) == evseUid.captured.lowercase(Locale.ENGLISH) }
    }

    every { patchConnector(capture(countryCode), capture(partyId), capture(locationId), capture(evseUid), capture(connectorId), capture(connectorPartial)) } answers {
        // Patch logic is not implemented
        locations
            .find { it.id.lowercase(Locale.ENGLISH) == locationId.captured.lowercase(Locale.ENGLISH)  }
            ?.evses
            ?.find { it.uid.lowercase(Locale.ENGLISH) == evseUid.captured.lowercase(Locale.ENGLISH) }
            ?.connectors
            ?.find { it.id.lowercase(Locale.ENGLISH) == connectorId.captured.lowercase(Locale.ENGLISH) }
    }

    every { putLocation(capture(countryCode), capture(partyId), capture(locationId), capture(location)) } answers {
        // Put logic is not implemented
        locations
            .find { it.id.lowercase(Locale.ENGLISH) == locationId.captured.lowercase(Locale.ENGLISH) }
            ?: location.captured
    }

    every { putEvse(capture(countryCode), capture(partyId), capture(locationId), capture(evseUid), capture(evse)) } answers {
        // Put logic is not implemented
        locations
            .find { it.id.lowercase(Locale.ENGLISH) == locationId.captured.lowercase(Locale.ENGLISH) }
            ?.evses
            ?.find { it.uid.lowercase(Locale.ENGLISH) == evseUid.captured.lowercase(Locale.ENGLISH) }
            ?: evse.captured
    }

    every { putConnector(capture(countryCode), capture(partyId), capture(locationId), capture(evseUid), capture(connectorId), capture(connector)) } answers {
        // Put logic is not implemented
        locations
            .find { it.id.lowercase(Locale.ENGLISH) == locationId.captured.lowercase(Locale.ENGLISH)  }
            ?.evses
            ?.find { it.uid.lowercase(Locale.ENGLISH) == evseUid.captured.lowercase(Locale.ENGLISH) }
            ?.connectors
            ?.find { it.id.lowercase(Locale.ENGLISH) == connectorId.captured.lowercase(Locale.ENGLISH) }
            ?: connector.captured
    }
}
