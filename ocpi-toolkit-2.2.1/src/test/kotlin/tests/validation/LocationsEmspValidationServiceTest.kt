package tests.validation

import common.OcpiStatus
import ocpi.locations.domain.toPartial
import ocpi.locations.validation.LocationsEmspValidationService
import org.junit.jupiter.api.Test
import samples.common.validConnector
import samples.common.validEvse
import samples.common.validLocation
import strikt.api.expectThat
import strikt.assertions.isEqualTo
import tests.mock.locationsEmspService

class LocationsEmspValidationServiceTest {
    private lateinit var service: LocationsEmspValidationService

    private val str1char = "a"
    private val str2chars = "ab"
    private val str3chars = "abc"
    private val str4chars = "abcd"
    private val str36chars = "abababababababababababababababababab"
    private val str37chars = "ababababababababababababababababababa"
    private val str40chars = "abababababababababababababababababababab"

    @Test
    fun getLocationParamsValidationTest() {
        service = LocationsEmspValidationService(service = locationsEmspService(emptyList()))

        expectThat(service.getLocation(countryCode = str1char, partyId = str2chars, locationId = str4chars)) {
            get { status_code }
                .isEqualTo(OcpiStatus.SUCCESS.code)
        }

        expectThat(service.getLocation(countryCode = str2chars, partyId = str3chars, locationId = str36chars)) {
            get { status_code }
                .isEqualTo(OcpiStatus.SUCCESS.code)
        }

        expectThat(service.getLocation(countryCode = str3chars, partyId = str3chars, locationId = str36chars)) {
            get { status_code }
                .isEqualTo(OcpiStatus.CLIENT_INVALID_PARAMETERS.code)
        }

        expectThat(service.getLocation(countryCode = str2chars, partyId = str4chars, locationId = str36chars)) {
            get { status_code }
                .isEqualTo(OcpiStatus.CLIENT_INVALID_PARAMETERS.code)
        }

        expectThat(service.getLocation(countryCode = str2chars, partyId = str3chars, locationId = str40chars)) {
            get { status_code }
                .isEqualTo(OcpiStatus.CLIENT_INVALID_PARAMETERS.code)
        }
    }

    @Test
    fun getEvseParamsValidationTest() {
        service = LocationsEmspValidationService(service = locationsEmspService(emptyList()))

        expectThat(
            service.getEvse(
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
                countryCode = str2chars,
                partyId = str3chars,
                locationId = str36chars,
                evseUid = str36chars
            )
        ) {
            get { status_code }
                .isEqualTo(OcpiStatus.SUCCESS.code)
        }

        expectThat(
            service.getEvse(
                countryCode = str3chars,
                partyId = str3chars,
                locationId = str36chars,
                evseUid = str36chars
            )
        ) {
            get { status_code }
                .isEqualTo(OcpiStatus.CLIENT_INVALID_PARAMETERS.code)
        }

        expectThat(
            service.getEvse(
                countryCode = str2chars,
                partyId = str4chars,
                locationId = str36chars,
                evseUid = str36chars
            )
        ) {
            get { status_code }
                .isEqualTo(OcpiStatus.CLIENT_INVALID_PARAMETERS.code)
        }

        expectThat(
            service.getEvse(
                countryCode = str2chars,
                partyId = str3chars,
                locationId = str40chars,
                evseUid = str36chars
            )
        ) {
            get { status_code }
                .isEqualTo(OcpiStatus.CLIENT_INVALID_PARAMETERS.code)
        }

        expectThat(
            service.getEvse(
                countryCode = str2chars,
                partyId = str3chars,
                locationId = str36chars,
                evseUid = str40chars
            )
        ) {
            get { status_code }
                .isEqualTo(OcpiStatus.CLIENT_INVALID_PARAMETERS.code)
        }
    }

    @Test
    fun getConnectorParamsValidationTest() {
        service = LocationsEmspValidationService(service = locationsEmspService(emptyList()))

        expectThat(
            service.getConnector(
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
                countryCode = str2chars,
                partyId = str3chars,
                locationId = str36chars,
                evseUid = str36chars,
                connectorId = str36chars
            )
        ) {
            get { status_code }
                .isEqualTo(OcpiStatus.SUCCESS.code)
        }

        expectThat(
            service.getConnector(
                countryCode = str3chars,
                partyId = str3chars,
                locationId = str36chars,
                evseUid = str36chars,
                connectorId = str36chars
            )
        ) {
            get { status_code }
                .isEqualTo(OcpiStatus.CLIENT_INVALID_PARAMETERS.code)
        }

        expectThat(
            service.getConnector(
                countryCode = str2chars,
                partyId = str4chars,
                locationId = str36chars,
                evseUid = str36chars,
                connectorId = str36chars
            )
        ) {
            get { status_code }
                .isEqualTo(OcpiStatus.CLIENT_INVALID_PARAMETERS.code)
        }

        expectThat(
            service.getConnector(
                countryCode = str2chars,
                partyId = str3chars,
                locationId = str40chars,
                evseUid = str36chars,
                connectorId = str36chars
            )
        ) {
            get { status_code }
                .isEqualTo(OcpiStatus.CLIENT_INVALID_PARAMETERS.code)
        }

        expectThat(
            service.getConnector(
                countryCode = str2chars,
                partyId = str3chars,
                locationId = str36chars,
                evseUid = str40chars,
                connectorId = str36chars
            )
        ) {
            get { status_code }
                .isEqualTo(OcpiStatus.CLIENT_INVALID_PARAMETERS.code)
        }

        expectThat(
            service.getConnector(
                countryCode = str2chars,
                partyId = str3chars,
                locationId = str36chars,
                evseUid = str36chars,
                connectorId = str37chars
            )
        ) {
            get { status_code }
                .isEqualTo(OcpiStatus.CLIENT_INVALID_PARAMETERS.code)
        }
    }

    @Test
    fun patchLocationParamsValidationTest() {
        service = LocationsEmspValidationService(service = locationsEmspService(emptyList()))

        expectThat(
            service.patchLocation(
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
                countryCode = str2chars,
                partyId = str3chars,
                locationId = str36chars,
                location = validLocation.toPartial()
            )
        ) {
            get { status_code }
                .isEqualTo(OcpiStatus.SUCCESS.code)
        }

        expectThat(
            service.patchLocation(
                countryCode = str3chars,
                partyId = str3chars,
                locationId = str36chars,
                location = validLocation.toPartial()
            )
        ) {
            get { status_code }
                .isEqualTo(OcpiStatus.CLIENT_INVALID_PARAMETERS.code)
        }

        expectThat(
            service.patchLocation(
                countryCode = str2chars,
                partyId = str4chars,
                locationId = str36chars,
                location = validLocation.toPartial()
            )
        ) {
            get { status_code }
                .isEqualTo(OcpiStatus.CLIENT_INVALID_PARAMETERS.code)
        }

        expectThat(
            service.patchLocation(
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
        service = LocationsEmspValidationService(service = locationsEmspService(emptyList()))

        expectThat(
            service.patchEvse(
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
                countryCode = str2chars,
                partyId = str3chars,
                locationId = str36chars,
                evseUid = str36chars,
                evse = validEvse.toPartial()
            )
        ) {
            get { status_code }
                .isEqualTo(OcpiStatus.SUCCESS.code)
        }

        expectThat(
            service.patchEvse(
                countryCode = str3chars,
                partyId = str3chars,
                locationId = str36chars,
                evseUid = str36chars,
                evse = validEvse.toPartial()
            )
        ) {
            get { status_code }
                .isEqualTo(OcpiStatus.CLIENT_INVALID_PARAMETERS.code)
        }

        expectThat(
            service.patchEvse(
                countryCode = str2chars,
                partyId = str4chars,
                locationId = str36chars,
                evseUid = str36chars,
                evse = validEvse.toPartial()
            )
        ) {
            get { status_code }
                .isEqualTo(OcpiStatus.CLIENT_INVALID_PARAMETERS.code)
        }

        expectThat(
            service.patchEvse(
                countryCode = str2chars,
                partyId = str3chars,
                locationId = str40chars,
                evseUid = str36chars,
                evse = validEvse.toPartial()
            )
        ) {
            get { status_code }
                .isEqualTo(OcpiStatus.CLIENT_INVALID_PARAMETERS.code)
        }

        expectThat(
            service.patchEvse(
                countryCode = str2chars,
                partyId = str3chars,
                locationId = str36chars,
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
        service = LocationsEmspValidationService(service = locationsEmspService(emptyList()))

        expectThat(
            service.patchConnector(
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
                countryCode = str2chars,
                partyId = str3chars,
                locationId = str36chars,
                evseUid = str36chars,
                connectorId = str36chars,
                connector = validConnector.toPartial()
            )
        ) {
            get { status_code }
                .isEqualTo(OcpiStatus.SUCCESS.code)
        }

        expectThat(
            service.patchConnector(
                countryCode = str3chars,
                partyId = str3chars,
                locationId = str36chars,
                evseUid = str36chars,
                connectorId = str36chars,
                connector = validConnector.toPartial()
            )
        ) {
            get { status_code }
                .isEqualTo(OcpiStatus.CLIENT_INVALID_PARAMETERS.code)
        }

        expectThat(
            service.patchConnector(
                countryCode = str2chars,
                partyId = str4chars,
                locationId = str36chars,
                evseUid = str36chars,
                connectorId = str36chars,
                connector = validConnector.toPartial()
            )
        ) {
            get { status_code }
                .isEqualTo(OcpiStatus.CLIENT_INVALID_PARAMETERS.code)
        }

        expectThat(
            service.patchConnector(
                countryCode = str2chars,
                partyId = str3chars,
                locationId = str40chars,
                evseUid = str36chars,
                connectorId = str36chars,
                connector = validConnector.toPartial()
            )
        ) {
            get { status_code }
                .isEqualTo(OcpiStatus.CLIENT_INVALID_PARAMETERS.code)
        }

        expectThat(
            service.patchConnector(
                countryCode = str2chars,
                partyId = str3chars,
                locationId = str36chars,
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
                countryCode = str2chars,
                partyId = str3chars,
                locationId = str36chars,
                evseUid = str36chars,
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
        service = LocationsEmspValidationService(service = locationsEmspService(emptyList()))

        expectThat(
            service.putLocation(
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
                countryCode = str2chars,
                partyId = str3chars,
                locationId = str36chars,
                location = validLocation
            )
        ) {
            get { status_code }
                .isEqualTo(OcpiStatus.SUCCESS.code)
        }

        expectThat(
            service.putLocation(
                countryCode = str3chars,
                partyId = str3chars,
                locationId = str36chars,
                location = validLocation
            )
        ) {
            get { status_code }
                .isEqualTo(OcpiStatus.CLIENT_INVALID_PARAMETERS.code)
        }

        expectThat(
            service.putLocation(
                countryCode = str2chars,
                partyId = str4chars,
                locationId = str36chars,
                location = validLocation
            )
        ) {
            get { status_code }
                .isEqualTo(OcpiStatus.CLIENT_INVALID_PARAMETERS.code)
        }

        expectThat(
            service.putLocation(
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
        service = LocationsEmspValidationService(service = locationsEmspService(emptyList()))

        expectThat(
            service.putEvse(
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
                countryCode = str2chars,
                partyId = str3chars,
                locationId = str36chars,
                evseUid = str36chars,
                evse = validEvse
            )
        ) {
            get { status_code }
                .isEqualTo(OcpiStatus.SUCCESS.code)
        }

        expectThat(
            service.putEvse(
                countryCode = str3chars,
                partyId = str3chars,
                locationId = str36chars,
                evseUid = str36chars,
                evse = validEvse
            )
        ) {
            get { status_code }
                .isEqualTo(OcpiStatus.CLIENT_INVALID_PARAMETERS.code)
        }

        expectThat(
            service.putEvse(
                countryCode = str2chars,
                partyId = str4chars,
                locationId = str36chars,
                evseUid = str36chars,
                evse = validEvse
            )
        ) {
            get { status_code }
                .isEqualTo(OcpiStatus.CLIENT_INVALID_PARAMETERS.code)
        }

        expectThat(
            service.putEvse(
                countryCode = str2chars,
                partyId = str3chars,
                locationId = str40chars,
                evseUid = str36chars,
                evse = validEvse
            )
        ) {
            get { status_code }
                .isEqualTo(OcpiStatus.CLIENT_INVALID_PARAMETERS.code)
        }

        expectThat(
            service.putEvse(
                countryCode = str2chars,
                partyId = str3chars,
                locationId = str36chars,
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
        service = LocationsEmspValidationService(service = locationsEmspService(emptyList()))

        expectThat(
            service.putConnector(
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
                countryCode = str2chars,
                partyId = str3chars,
                locationId = str36chars,
                evseUid = str36chars,
                connectorId = str36chars,
                connector = validConnector
            )
        ) {
            get { status_code }
                .isEqualTo(OcpiStatus.SUCCESS.code)
        }

        expectThat(
            service.putConnector(
                countryCode = str3chars,
                partyId = str3chars,
                locationId = str36chars,
                evseUid = str36chars,
                connectorId = str36chars,
                connector = validConnector
            )
        ) {
            get { status_code }
                .isEqualTo(OcpiStatus.CLIENT_INVALID_PARAMETERS.code)
        }

        expectThat(
            service.putConnector(
                countryCode = str2chars,
                partyId = str4chars,
                locationId = str36chars,
                evseUid = str36chars,
                connectorId = str36chars,
                connector = validConnector
            )
        ) {
            get { status_code }
                .isEqualTo(OcpiStatus.CLIENT_INVALID_PARAMETERS.code)
        }

        expectThat(
            service.putConnector(
                countryCode = str2chars,
                partyId = str3chars,
                locationId = str40chars,
                evseUid = str36chars,
                connectorId = str36chars,
                connector = validConnector
            )
        ) {
            get { status_code }
                .isEqualTo(OcpiStatus.CLIENT_INVALID_PARAMETERS.code)
        }

        expectThat(
            service.putConnector(
                countryCode = str2chars,
                partyId = str3chars,
                locationId = str36chars,
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
                countryCode = str2chars,
                partyId = str3chars,
                locationId = str36chars,
                evseUid = str36chars,
                connectorId = str37chars,
                connector = validConnector
            )
        ) {
            get { status_code }
                .isEqualTo(OcpiStatus.CLIENT_INVALID_PARAMETERS.code)
        }
    }
}
