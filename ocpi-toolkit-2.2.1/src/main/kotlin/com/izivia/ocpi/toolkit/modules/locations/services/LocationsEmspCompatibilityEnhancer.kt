package com.izivia.ocpi.toolkit.modules.locations.services

import com.izivia.ocpi.toolkit.common.CiString
import com.izivia.ocpi.toolkit.modules.locations.LocationsEmspInterface
import com.izivia.ocpi.toolkit.modules.locations.domain.*
import java.math.BigDecimal
import java.math.RoundingMode

data class LocationCompatibilityConfiguration(
    val filterInvalidConnectors: Boolean = true,
)

class LocationsCompatibilityEnhancer(
    private val service: LocationsEmspInterface,
    private val cfg: LocationCompatibilityConfiguration = LocationCompatibilityConfiguration(),
) : LocationsEmspInterface {

    // --- pass through functions to satisfy interface --- //
    override suspend fun getLocation(countryCode: CiString, partyId: CiString, locationId: CiString): Location? {
        return service.getLocation(countryCode, partyId, locationId)
    }

    override suspend fun getEvse(
        countryCode: CiString,
        partyId: CiString,
        locationId: CiString,
        evseUid: CiString,
    ): Evse? {
        return service.getEvse(countryCode, partyId, locationId, evseUid)
    }

    override suspend fun getConnector(
        countryCode: CiString,
        partyId: CiString,
        locationId: CiString,
        evseUid: CiString,
        connectorId: CiString,
    ): Connector? {
        return service.getConnector(countryCode, partyId, locationId, evseUid, connectorId)
    }
    // ^^^ pass through functions to satisfy interface ^^^ //

    override suspend fun putLocation(
        countryCode: CiString,
        partyId: CiString,
        locationId: CiString,
        location: Location,
    ): LocationPartial {
        return service.putLocation(countryCode, partyId, locationId, location.ensureCompatible(cfg))
    }

    override suspend fun patchLocation(
        countryCode: CiString,
        partyId: CiString,
        locationId: CiString,
        location: LocationPartial,
    ): LocationPartial? {
        return service.patchLocation(countryCode, partyId, locationId, location.ensureCompatible(cfg))
    }

    override suspend fun putEvse(
        countryCode: CiString,
        partyId: CiString,
        locationId: CiString,
        evseUid: CiString,
        evse: Evse,
    ): EvsePartial {
        return service.putEvse(countryCode, partyId, locationId, evseUid, evse.ensureCompatible(cfg))
    }

    override suspend fun patchEvse(
        countryCode: CiString,
        partyId: CiString,
        locationId: CiString,
        evseUid: CiString,
        evse: EvsePartial,
    ): EvsePartial? {
        return service.patchEvse(
            countryCode = countryCode,
            partyId = partyId,
            locationId = locationId,
            evseUid = evseUid,
            evse = evse.ensureCompatible(cfg),
        )
    }

    override suspend fun putConnector(
        countryCode: CiString,
        partyId: CiString,
        locationId: CiString,
        evseUid: CiString,
        connectorId: CiString,
        connector: Connector,
    ): ConnectorPartial {
        if (cfg.filterInvalidConnectors && !connector.isCompatible()) return ConnectorPartial()
        return service.putConnector(countryCode, partyId, locationId, evseUid, connectorId, connector)
    }

    override suspend fun patchConnector(
        countryCode: CiString,
        partyId: CiString,
        locationId: CiString,
        evseUid: CiString,
        connectorId: CiString,
        connector: ConnectorPartial,
    ): ConnectorPartial? {
        // this will not avoid NOT_FOUND errors for previously filtered connectors but still avoids
        // invalid connectors to reach our database
        if (cfg.filterInvalidConnectors && !connector.isCompatible()) return ConnectorPartial()
        return service.patchConnector(countryCode, partyId, locationId, evseUid, connectorId, connector)
    }
}

private fun Location.ensureCompatible(cfg: LocationCompatibilityConfiguration) = copy(
    coordinates = coordinates.ensureCompatible(),
    relatedLocations = relatedLocations?.map(AdditionalGeoLocation::ensureCompatible),
    parkingType = parkingType?.takeIf { it != ParkingType.OTHER },
    evses = evses?.map { it.ensureCompatible(cfg) },
    facilities = facilities?.ensureCompatibleFacility(),
)

private fun LocationPartial.ensureCompatible(cfg: LocationCompatibilityConfiguration) = copy(
    coordinates = coordinates?.ensureCompatible(),
    relatedLocations = relatedLocations?.map(AdditionalGeoLocationPartial::ensureCompatible),
    parkingType = parkingType?.takeIf { it != ParkingType.OTHER },
    evses = evses?.map { it.ensureCompatible(cfg) },
    facilities = facilities?.ensureCompatibleFacility(),
)

private fun Evse.ensureCompatible(cfg: LocationCompatibilityConfiguration) = copy(
    capabilities = capabilities?.ensureCompatibleCapability(),
    connectors = if (cfg.filterInvalidConnectors) connectors.filterIncompatibleConnector() else connectors,
    coordinates = coordinates?.ensureCompatible(),
    parkingRestrictions = parkingRestrictions?.ensureCompatibleParkingRestriction(),
)

private fun EvsePartial.ensureCompatible(cfg: LocationCompatibilityConfiguration) = copy(
    capabilities = capabilities?.ensureCompatibleCapability(),
    connectors = if (cfg.filterInvalidConnectors) connectors?.filterIncompatibleConnectorPartial() else connectors,
    coordinates = coordinates?.ensureCompatible(),
    parkingRestrictions = parkingRestrictions?.ensureCompatibleParkingRestriction(),
)

private fun List<Connector>.filterIncompatibleConnector() = filter(Connector::isCompatible)
private fun List<ConnectorPartial>.filterIncompatibleConnectorPartial() = filter(ConnectorPartial::isCompatible)

private fun Connector.isCompatible() = this.standard != ConnectorType.OTHER
private fun ConnectorPartial.isCompatible() = this.standard != ConnectorType.OTHER

private fun List<Capability>.ensureCompatibleCapability() = filter { it != Capability.OTHER }
private fun List<Facility>.ensureCompatibleFacility() = filter { it != Facility.OTHER }
private fun List<ParkingRestriction>.ensureCompatibleParkingRestriction() = filter { it != ParkingRestriction.OTHER }

private fun GeoLocation.ensureCompatible() = copy(
    latitude = latitude.ensureScale(7),
    longitude = longitude.ensureScale(7),
)

private fun GeoLocationPartial.ensureCompatible() = copy(
    latitude = latitude?.ensureScale(7),
    longitude = longitude?.ensureScale(7),
)

private fun AdditionalGeoLocation.ensureCompatible() = copy(
    latitude = latitude.ensureScale(7),
    longitude = longitude.ensureScale(7),
)

private fun AdditionalGeoLocationPartial.ensureCompatible() = copy(
    latitude = latitude?.ensureScale(7),
    longitude = longitude?.ensureScale(7),
)

private fun String.ensureScale(scale: Int) = try {
    BigDecimal(this).ensureScale(scale).toString()
} catch (e: NumberFormatException) {
    // fall through to validator
    this
}

private fun BigDecimal.ensureScale(maxScale: Int) =
    takeIf { scale() > maxScale }
        ?.setScale(maxScale, RoundingMode.HALF_UP)
        ?: this
