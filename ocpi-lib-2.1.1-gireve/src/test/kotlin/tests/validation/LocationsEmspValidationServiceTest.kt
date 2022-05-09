package tests.validation

import common.OcpiStatus
import ocpi.locations.domain.toPartial
import ocpi.locations.validation.LocationsEmspValidationService
import org.junit.jupiter.api.Test
import samples.common.DummyPlatformCacheRepository
import strikt.api.expectThat
import strikt.assertions.isEqualTo
import tests.mock.locationsEmspService
import tests.utils.validConnector
import tests.utils.validEvse
import tests.utils.validLocation
import java.util.*

class LocationsEmspValidationServiceTest {
    private lateinit var service: LocationsEmspValidationService

    val token = UUID.randomUUID().toString()
    val str1char = "a"
    val str2chars = "ab"
    val str3chars = "abc"
    val str4chars = "abcd"
    val str36chars = "abababababababababababababababababab"
    val str37chars = "ababababababababababababababababababa"
    val str39chars = "abababababababababababababababababababa"
    val str40chars = "abababababababababababababababababababab"

    @Test
    fun getLocationParamsValidationTest() {
        service = LocationsEmspValidationService(locationsEmspService(emptyList()), DummyPlatformCacheRepository(tokenC = token))

        expectThat(service.getLocation(token = token, countryCode = str1char, partyId = str2chars, locationId = str4chars)) {
            get { status_code }
                .isEqualTo(OcpiStatus.SUCCESS.code)
        }

        expectThat(service.getLocation(token = token, countryCode = str2chars, partyId = str3chars, locationId = str39chars)) {
            get { status_code }
                .isEqualTo(OcpiStatus.SUCCESS.code)
        }

        expectThat(service.getLocation(token = token, countryCode = str3chars, partyId = str3chars, locationId = str39chars)) {
            get { status_code }
                .isEqualTo(OcpiStatus.CLIENT_INVALID_PARAMETERS.code)
        }

        expectThat(service.getLocation(token = token, countryCode = str2chars, partyId = str4chars, locationId = str39chars)) {
            get { status_code }
                .isEqualTo(OcpiStatus.CLIENT_INVALID_PARAMETERS.code)
        }

        expectThat(service.getLocation(token = token, countryCode = str2chars, partyId = str3chars, locationId = str40chars)) {
            get { status_code }
                .isEqualTo(OcpiStatus.CLIENT_INVALID_PARAMETERS.code)
        }
    }

    @Test
    fun getEvseParamsValidationTest() {
        service = LocationsEmspValidationService(locationsEmspService(emptyList()), DummyPlatformCacheRepository(tokenC = token))

        expectThat(
            service.getEvse(
                token = token,
                countryCode = str1char,
                partyId = str2chars,
                locationId = str4chars,
                evseUid = str4chars
            )
        ) {
            get { status_code }
                .isEqualTo(OcpiStatus.SUCCESS.code)
        }

        expectThat(
            service.getEvse(
                token = token,
                countryCode = str2chars,
                partyId = str3chars,
                locationId = str39chars,
                evseUid = str39chars
            )
        ) {
            get { status_code }
                .isEqualTo(OcpiStatus.SUCCESS.code)
        }

        expectThat(
            service.getEvse(
                token = token,
                countryCode = str3chars,
                partyId = str3chars,
                locationId = str39chars,
                evseUid = str39chars
            )
        ) {
            get { status_code }
                .isEqualTo(OcpiStatus.CLIENT_INVALID_PARAMETERS.code)
        }

        expectThat(
            service.getEvse(
                token = token,
                countryCode = str2chars,
                partyId = str4chars,
                locationId = str39chars,
                evseUid = str39chars
            )
        ) {
            get { status_code }
                .isEqualTo(OcpiStatus.CLIENT_INVALID_PARAMETERS.code)
        }

        expectThat(
            service.getEvse(
                token = token,
                countryCode = str2chars,
                partyId = str3chars,
                locationId = str40chars,
                evseUid = str39chars
            )
        ) {
            get { status_code }
                .isEqualTo(OcpiStatus.CLIENT_INVALID_PARAMETERS.code)
        }

        expectThat(
            service.getEvse(
                token = token,
                countryCode = str2chars,
                partyId = str3chars,
                locationId = str39chars,
                evseUid = str40chars
            )
        ) {
            get { status_code }
                .isEqualTo(OcpiStatus.CLIENT_INVALID_PARAMETERS.code)
        }
    }

    @Test
    fun getConnectorParamsValidationTest() {
        service = LocationsEmspValidationService(locationsEmspService(emptyList()), DummyPlatformCacheRepository(tokenC = token))

        expectThat(
            service.getConnector(
                token = token,
                countryCode = str1char,
                partyId = str2chars,
                locationId = str4chars,
                evseUid = str4chars,
                connectorId = str4chars
            )
        ) {
            get { status_code }
                .isEqualTo(OcpiStatus.SUCCESS.code)
        }

        expectThat(
            service.getConnector(
                token = token,
                countryCode = str2chars,
                partyId = str3chars,
                locationId = str39chars,
                evseUid = str39chars,
                connectorId = str36chars
            )
        ) {
            get { status_code }
                .isEqualTo(OcpiStatus.SUCCESS.code)
        }

        expectThat(
            service.getConnector(
                token = token,
                countryCode = str3chars,
                partyId = str3chars,
                locationId = str39chars,
                evseUid = str39chars,
                connectorId = str36chars
            )
        ) {
            get { status_code }
                .isEqualTo(OcpiStatus.CLIENT_INVALID_PARAMETERS.code)
        }

        expectThat(
            service.getConnector(
                token = token,
                countryCode = str2chars,
                partyId = str4chars,
                locationId = str39chars,
                evseUid = str39chars,
                connectorId = str36chars
            )
        ) {
            get { status_code }
                .isEqualTo(OcpiStatus.CLIENT_INVALID_PARAMETERS.code)
        }

        expectThat(
            service.getConnector(
                token = token,
                countryCode = str2chars,
                partyId = str3chars,
                locationId = str40chars,
                evseUid = str39chars,
                connectorId = str36chars
            )
        ) {
            get { status_code }
                .isEqualTo(OcpiStatus.CLIENT_INVALID_PARAMETERS.code)
        }

        expectThat(
            service.getConnector(
                token = token,
                countryCode = str2chars,
                partyId = str3chars,
                locationId = str39chars,
                evseUid = str40chars,
                connectorId = str36chars
            )
        ) {
            get { status_code }
                .isEqualTo(OcpiStatus.CLIENT_INVALID_PARAMETERS.code)
        }

        expectThat(
            service.getConnector(
                token = token,
                countryCode = str2chars,
                partyId = str3chars,
                locationId = str39chars,
                evseUid = str39chars,
                connectorId = str37chars
            )
        ) {
            get { status_code }
                .isEqualTo(OcpiStatus.CLIENT_INVALID_PARAMETERS.code)
        }
    }

    @Test
    fun patchLocationParamsValidationTest() {
        service = LocationsEmspValidationService(locationsEmspService(emptyList()), DummyPlatformCacheRepository(tokenC = token))

        expectThat(
            service.patchLocation(
                token = token,
                countryCode = str1char,
                partyId = str2chars,
                locationId = str4chars,
                location = validLocation.toPartial()
            )
        ) {
            get { status_code }
                .isEqualTo(OcpiStatus.SUCCESS.code)
        }

        expectThat(
            service.patchLocation(
                token = token,
                countryCode = str2chars,
                partyId = str3chars,
                locationId = str39chars,
                location = validLocation.toPartial()
            )
        ) {
            get { status_code }
                .isEqualTo(OcpiStatus.SUCCESS.code)
        }

        expectThat(
            service.patchLocation(
                token = token,
                countryCode = str3chars,
                partyId = str3chars,
                locationId = str39chars,
                location = validLocation.toPartial()
            )
        ) {
            get { status_code }
                .isEqualTo(OcpiStatus.CLIENT_INVALID_PARAMETERS.code)
        }

        expectThat(
            service.patchLocation(
                token = token,
                countryCode = str2chars,
                partyId = str4chars,
                locationId = str39chars,
                location = validLocation.toPartial()
            )
        ) {
            get { status_code }
                .isEqualTo(OcpiStatus.CLIENT_INVALID_PARAMETERS.code)
        }

        expectThat(
            service.patchLocation(
                token = token,
                countryCode = str2chars,
                partyId = str3chars,
                locationId = str40chars,
                location = validLocation.toPartial()
            )
        ) {
            get { status_code }
                .isEqualTo(OcpiStatus.CLIENT_INVALID_PARAMETERS.code)
        }
    }

    @Test
    fun patchEvseParamsValidationTest() {
        service = LocationsEmspValidationService(locationsEmspService(emptyList()), DummyPlatformCacheRepository(tokenC = token))

        expectThat(
            service.patchEvse(
                token = token,
                countryCode = str1char,
                partyId = str2chars,
                locationId = str4chars,
                evseUid = str4chars,
                evse = validEvse.toPartial()
            )
        ) {
            get { status_code }
                .isEqualTo(OcpiStatus.SUCCESS.code)
        }

        expectThat(
            service.patchEvse(
                token = token,
                countryCode = str2chars,
                partyId = str3chars,
                locationId = str39chars,
                evseUid = str39chars,
                evse = validEvse.toPartial()
            )
        ) {
            get { status_code }
                .isEqualTo(OcpiStatus.SUCCESS.code)
        }

        expectThat(
            service.patchEvse(
                token = token,
                countryCode = str3chars,
                partyId = str3chars,
                locationId = str39chars,
                evseUid = str39chars,
                evse = validEvse.toPartial()
            )
        ) {
            get { status_code }
                .isEqualTo(OcpiStatus.CLIENT_INVALID_PARAMETERS.code)
        }

        expectThat(
            service.patchEvse(
                token = token,
                countryCode = str2chars,
                partyId = str4chars,
                locationId = str39chars,
                evseUid = str39chars,
                evse = validEvse.toPartial()
            )
        ) {
            get { status_code }
                .isEqualTo(OcpiStatus.CLIENT_INVALID_PARAMETERS.code)
        }

        expectThat(
            service.patchEvse(
                token = token,
                countryCode = str2chars,
                partyId = str3chars,
                locationId = str40chars,
                evseUid = str39chars,
                evse = validEvse.toPartial()
            )
        ) {
            get { status_code }
                .isEqualTo(OcpiStatus.CLIENT_INVALID_PARAMETERS.code)
        }

        expectThat(
            service.patchEvse(
                token = token,
                countryCode = str2chars,
                partyId = str3chars,
                locationId = str39chars,
                evseUid = str40chars,
                evse = validEvse.toPartial()
            )
        ) {
            get { status_code }
                .isEqualTo(OcpiStatus.CLIENT_INVALID_PARAMETERS.code)
        }
    }

    @Test
    fun patchConnectorParamsValidationTest() {
        service = LocationsEmspValidationService(locationsEmspService(emptyList()), DummyPlatformCacheRepository(tokenC = token))

        expectThat(
            service.patchConnector(
                token = token,
                countryCode = str1char,
                partyId = str2chars,
                locationId = str4chars,
                evseUid = str4chars,
                connectorId = str4chars,
                connector = validConnector.toPartial()
            )
        ) {
            get { status_code }
                .isEqualTo(OcpiStatus.SUCCESS.code)
        }

        expectThat(
            service.patchConnector(
                token = token,
                countryCode = str2chars,
                partyId = str3chars,
                locationId = str39chars,
                evseUid = str39chars,
                connectorId = str36chars,
                connector = validConnector.toPartial()
            )
        ) {
            get { status_code }
                .isEqualTo(OcpiStatus.SUCCESS.code)
        }

        expectThat(
            service.patchConnector(
                token = token,
                countryCode = str3chars,
                partyId = str3chars,
                locationId = str39chars,
                evseUid = str39chars,
                connectorId = str36chars,
                connector = validConnector.toPartial()
            )
        ) {
            get { status_code }
                .isEqualTo(OcpiStatus.CLIENT_INVALID_PARAMETERS.code)
        }

        expectThat(
            service.patchConnector(
                token = token,
                countryCode = str2chars,
                partyId = str4chars,
                locationId = str39chars,
                evseUid = str39chars,
                connectorId = str36chars,
                connector = validConnector.toPartial()
            )
        ) {
            get { status_code }
                .isEqualTo(OcpiStatus.CLIENT_INVALID_PARAMETERS.code)
        }

        expectThat(
            service.patchConnector(
                token = token,
                countryCode = str2chars,
                partyId = str3chars,
                locationId = str40chars,
                evseUid = str39chars,
                connectorId = str36chars,
                connector = validConnector.toPartial()
            )
        ) {
            get { status_code }
                .isEqualTo(OcpiStatus.CLIENT_INVALID_PARAMETERS.code)
        }

        expectThat(
            service.patchConnector(
                token = token,
                countryCode = str2chars,
                partyId = str3chars,
                locationId = str39chars,
                evseUid = str40chars,
                connectorId = str36chars,
                connector = validConnector.toPartial()
            )
        ) {
            get { status_code }
                .isEqualTo(OcpiStatus.CLIENT_INVALID_PARAMETERS.code)
        }

        expectThat(
            service.patchConnector(
                token = token,
                countryCode = str2chars,
                partyId = str3chars,
                locationId = str39chars,
                evseUid = str39chars,
                connectorId = str37chars,
                connector = validConnector.toPartial()
            )
        ) {
            get { status_code }
                .isEqualTo(OcpiStatus.CLIENT_INVALID_PARAMETERS.code)
        }
    }

    @Test
    fun putLocationParamsValidationTest() {
        service = LocationsEmspValidationService(locationsEmspService(emptyList()), DummyPlatformCacheRepository(tokenC = token))

        expectThat(
            service.putLocation(
                token = token,
                countryCode = str1char,
                partyId = str2chars,
                locationId = str4chars,
                location = validLocation
            )
        ) {
            get { status_code }
                .isEqualTo(OcpiStatus.SUCCESS.code)
        }

        expectThat(
            service.putLocation(
                token = token,
                countryCode = str2chars,
                partyId = str3chars,
                locationId = str39chars,
                location = validLocation
            )
        ) {
            get { status_code }
                .isEqualTo(OcpiStatus.SUCCESS.code)
        }

        expectThat(
            service.putLocation(
                token = token,
                countryCode = str3chars,
                partyId = str3chars,
                locationId = str39chars,
                location = validLocation
            )
        ) {
            get { status_code }
                .isEqualTo(OcpiStatus.CLIENT_INVALID_PARAMETERS.code)
        }

        expectThat(
            service.putLocation(
                token = token,
                countryCode = str2chars,
                partyId = str4chars,
                locationId = str39chars,
                location = validLocation
            )
        ) {
            get { status_code }
                .isEqualTo(OcpiStatus.CLIENT_INVALID_PARAMETERS.code)
        }

        expectThat(
            service.putLocation(
                token = token,
                countryCode = str2chars,
                partyId = str3chars,
                locationId = str40chars,
                location = validLocation
            )
        ) {
            get { status_code }
                .isEqualTo(OcpiStatus.CLIENT_INVALID_PARAMETERS.code)
        }
    }

    @Test
    fun putEvseParamsValidationTest() {
        service = LocationsEmspValidationService(locationsEmspService(emptyList()), DummyPlatformCacheRepository(tokenC = token))

        expectThat(
            service.putEvse(
                token = token,
                countryCode = str1char,
                partyId = str2chars,
                locationId = str4chars,
                evseUid = str4chars,
                evse = validEvse
            )
        ) {
            get { status_code }
                .isEqualTo(OcpiStatus.SUCCESS.code)
        }

        expectThat(
            service.putEvse(
                token = token,
                countryCode = str2chars,
                partyId = str3chars,
                locationId = str39chars,
                evseUid = str39chars,
                evse = validEvse
            )
        ) {
            get { status_code }
                .isEqualTo(OcpiStatus.SUCCESS.code)
        }

        expectThat(
            service.putEvse(
                token = token,
                countryCode = str3chars,
                partyId = str3chars,
                locationId = str39chars,
                evseUid = str39chars,
                evse = validEvse
            )
        ) {
            get { status_code }
                .isEqualTo(OcpiStatus.CLIENT_INVALID_PARAMETERS.code)
        }

        expectThat(
            service.putEvse(
                token = token,
                countryCode = str2chars,
                partyId = str4chars,
                locationId = str39chars,
                evseUid = str39chars,
                evse = validEvse
            )
        ) {
            get { status_code }
                .isEqualTo(OcpiStatus.CLIENT_INVALID_PARAMETERS.code)
        }

        expectThat(
            service.putEvse(
                token = token,
                countryCode = str2chars,
                partyId = str3chars,
                locationId = str40chars,
                evseUid = str39chars,
                evse = validEvse
            )
        ) {
            get { status_code }
                .isEqualTo(OcpiStatus.CLIENT_INVALID_PARAMETERS.code)
        }

        expectThat(
            service.putEvse(
                token = token,
                countryCode = str2chars,
                partyId = str3chars,
                locationId = str39chars,
                evseUid = str40chars,
                evse = validEvse
            )
        ) {
            get { status_code }
                .isEqualTo(OcpiStatus.CLIENT_INVALID_PARAMETERS.code)
        }
    }

    @Test
    fun putConnectorParamsValidationTest() {
        service = LocationsEmspValidationService(locationsEmspService(emptyList()), DummyPlatformCacheRepository(tokenC = token))

        expectThat(
            service.putConnector(
                token = token,
                countryCode = str1char,
                partyId = str2chars,
                locationId = str4chars,
                evseUid = str4chars,
                connectorId = str4chars,
                connector = validConnector
            )
        ) {
            get { status_code }
                .isEqualTo(OcpiStatus.SUCCESS.code)
        }

        expectThat(
            service.putConnector(
                token = token,
                countryCode = str2chars,
                partyId = str3chars,
                locationId = str39chars,
                evseUid = str39chars,
                connectorId = str36chars,
                connector = validConnector
            )
        ) {
            get { status_code }
                .isEqualTo(OcpiStatus.SUCCESS.code)
        }

        expectThat(
            service.putConnector(
                token = token,
                countryCode = str3chars,
                partyId = str3chars,
                locationId = str39chars,
                evseUid = str39chars,
                connectorId = str36chars,
                connector = validConnector
            )
        ) {
            get { status_code }
                .isEqualTo(OcpiStatus.CLIENT_INVALID_PARAMETERS.code)
        }

        expectThat(
            service.putConnector(
                token = token,
                countryCode = str2chars,
                partyId = str4chars,
                locationId = str39chars,
                evseUid = str39chars,
                connectorId = str36chars,
                connector = validConnector
            )
        ) {
            get { status_code }
                .isEqualTo(OcpiStatus.CLIENT_INVALID_PARAMETERS.code)
        }

        expectThat(
            service.putConnector(
                token = token,
                countryCode = str2chars,
                partyId = str3chars,
                locationId = str40chars,
                evseUid = str39chars,
                connectorId = str36chars,
                connector = validConnector
            )
        ) {
            get { status_code }
                .isEqualTo(OcpiStatus.CLIENT_INVALID_PARAMETERS.code)
        }

        expectThat(
            service.putConnector(
                token = token,
                countryCode = str2chars,
                partyId = str3chars,
                locationId = str39chars,
                evseUid = str40chars,
                connectorId = str36chars,
                connector = validConnector
            )
        ) {
            get { status_code }
                .isEqualTo(OcpiStatus.CLIENT_INVALID_PARAMETERS.code)
        }

        expectThat(
            service.putConnector(
                token = token,
                countryCode = str2chars,
                partyId = str3chars,
                locationId = str39chars,
                evseUid = str39chars,
                connectorId = str37chars,
                connector = validConnector
            )
        ) {
            get { status_code }
                .isEqualTo(OcpiStatus.CLIENT_INVALID_PARAMETERS.code)
        }
    }
}
