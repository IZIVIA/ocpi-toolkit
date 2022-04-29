package tests

import io.mockk.every
import io.mockk.mockk
import io.mockk.slot
import ocpi.locations.domain.*
import ocpi.locations.repositories.LocationsEmspRepository

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

    every { getLocation(capture(countryCode), capture(partyId), capture(locationId)) } answers {
        locations.find { it.id == locationId.captured }
    }

    every { getEvse(capture(countryCode), capture(partyId), capture(locationId), capture(evseUid)) } answers {
        locations
            .find { it.id == locationId.captured }
            ?.evses
            ?.find { it.uid == evseUid.captured }
    }

    every { getConnector(capture(countryCode), capture(partyId), capture(locationId), capture(evseUid), capture(connectorId)) } answers {
        locations
            .find { it.id == locationId.captured  }
            ?.evses
            ?.find { it.uid == evseUid.captured }
            ?.connectors
            ?.find { it.id == connectorId.captured }
    }

    every { patchLocation(capture(countryCode), capture(partyId), capture(locationId), capture(locationPartial)) } answers {
        // Patch logic is not implemented
        locations
            .find { it.id == locationId.captured }
    }

    every { patchEvse(capture(countryCode), capture(partyId), capture(locationId), capture(evseUid), capture(evsePartial)) } answers {
        // Patch logic is not implemented
        locations
            .find { it.id == locationId.captured }
            ?.evses
            ?.find { it.uid == evseUid.captured }
    }

    every { patchConnector(capture(countryCode), capture(partyId), capture(locationId), capture(evseUid), capture(connectorId), capture(connectorPartial)) } answers {
        // Patch logic is not implemented
        locations
            .find { it.id == locationId.captured  }
            ?.evses
            ?.find { it.uid == evseUid.captured }
            ?.connectors
            ?.find { it.id == connectorId.captured }
    }

    every { putLocation(capture(countryCode), capture(partyId), capture(locationId), capture(location)) } answers {
        // Put logic is not implemented
        locations
            .find { it.id == locationId.captured }
            ?: location.captured
    }

    every { putEvse(capture(countryCode), capture(partyId), capture(locationId), capture(evseUid), capture(evse)) } answers {
        // Put logic is not implemented
        locations
            .find { it.id == locationId.captured }
            ?.evses
            ?.find { it.uid == evseUid.captured }
            ?: evse.captured
    }

    every { putConnector(capture(countryCode), capture(partyId), capture(locationId), capture(evseUid), capture(connectorId), capture(connector)) } answers {
        // Put logic is not implemented
        locations
            .find { it.id == locationId.captured  }
            ?.evses
            ?.find { it.uid == evseUid.captured }
            ?.connectors
            ?.find { it.id == connectorId.captured }
            ?: connector.captured
    }
}