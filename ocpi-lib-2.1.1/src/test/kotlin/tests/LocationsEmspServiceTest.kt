package tests

import common.OcpiResponseBody
import common.OcpiStatusCode
import ocpi.locations.domain.Connector
import ocpi.locations.domain.Evse
import ocpi.locations.domain.Location
import ocpi.locations.services.LocationsEmspService
import org.junit.jupiter.api.Test
import strikt.api.expectThat
import strikt.assertions.isEqualTo

class LocationsEmspServiceTest {
    private lateinit var service: LocationsEmspService

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
        service = LocationsEmspService(locationsEmspRepository(emptyList()))

        expectThat(service.getLocation(countryCode = str1char, partyId = str2chars, locationId = str4chars)) {
            get { status_code }
                .isEqualTo(OcpiStatusCode.SUCCESS.code)
        }

        expectThat(service.getLocation(countryCode = str2chars, partyId = str3chars, locationId = str39chars)) {
            get { status_code }
                .isEqualTo(OcpiStatusCode.SUCCESS.code)
        }

        expectThat(service.getLocation(countryCode = str3chars, partyId = str3chars, locationId = str39chars)) {
            get { status_code }
                .isEqualTo(OcpiStatusCode.INVALID_OR_MISSING_PARAMETERS.code)
        }

        expectThat(service.getLocation(countryCode = str2chars, partyId = str4chars, locationId = str39chars)) {
            get { status_code }
                .isEqualTo(OcpiStatusCode.INVALID_OR_MISSING_PARAMETERS.code)
        }

        expectThat(service.getLocation(countryCode = str2chars, partyId = str3chars, locationId = str40chars)) {
            get { status_code }
                .isEqualTo(OcpiStatusCode.INVALID_OR_MISSING_PARAMETERS.code)
        }
    }

    @Test
    fun getEvseParamsValidationTest() {
        service = LocationsEmspService(locationsEmspRepository(emptyList()))

        expectThat(
            service.getEvse(
                countryCode = str1char,
                partyId = str2chars,
                locationId = str4chars,
                evseUid = str4chars
            )
        ) {
            get { status_code }
                .isEqualTo(OcpiStatusCode.SUCCESS.code)
        }

        expectThat(
            service.getEvse(
                countryCode = str2chars,
                partyId = str3chars,
                locationId = str39chars,
                evseUid = str39chars
            )
        ) {
            get { status_code }
                .isEqualTo(OcpiStatusCode.SUCCESS.code)
        }

        expectThat(
            service.getEvse(
                countryCode = str3chars,
                partyId = str3chars,
                locationId = str39chars,
                evseUid = str39chars
            )
        ) {
            get { status_code }
                .isEqualTo(OcpiStatusCode.INVALID_OR_MISSING_PARAMETERS.code)
        }

        expectThat(
            service.getEvse(
                countryCode = str2chars,
                partyId = str4chars,
                locationId = str39chars,
                evseUid = str39chars
            )
        ) {
            get { status_code }
                .isEqualTo(OcpiStatusCode.INVALID_OR_MISSING_PARAMETERS.code)
        }

        expectThat(
            service.getEvse(
                countryCode = str2chars,
                partyId = str3chars,
                locationId = str40chars,
                evseUid = str39chars
            )
        ) {
            get { status_code }
                .isEqualTo(OcpiStatusCode.INVALID_OR_MISSING_PARAMETERS.code)
        }

        expectThat(
            service.getEvse(
                countryCode = str2chars,
                partyId = str3chars,
                locationId = str39chars,
                evseUid = str40chars
            )
        ) {
            get { status_code }
                .isEqualTo(OcpiStatusCode.INVALID_OR_MISSING_PARAMETERS.code)
        }
    }

    @Test
    fun getConnectorParamsValidationTest() {
        service = LocationsEmspService(locationsEmspRepository(emptyList()))

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
                .isEqualTo(OcpiStatusCode.SUCCESS.code)
        }

        expectThat(
            service.getConnector(
                countryCode = str2chars,
                partyId = str3chars,
                locationId = str39chars,
                evseUid = str39chars,
                connectorId = str36chars
            )
        ) {
            get { status_code }
                .isEqualTo(OcpiStatusCode.SUCCESS.code)
        }

        expectThat(
            service.getConnector(
                countryCode = str3chars,
                partyId = str3chars,
                locationId = str39chars,
                evseUid = str39chars,
                connectorId = str36chars
            )
        ) {
            get { status_code }
                .isEqualTo(OcpiStatusCode.INVALID_OR_MISSING_PARAMETERS.code)
        }

        expectThat(
            service.getConnector(
                countryCode = str2chars,
                partyId = str4chars,
                locationId = str39chars,
                evseUid = str39chars,
                connectorId = str36chars
            )
        ) {
            get { status_code }
                .isEqualTo(OcpiStatusCode.INVALID_OR_MISSING_PARAMETERS.code)
        }

        expectThat(
            service.getConnector(
                countryCode = str2chars,
                partyId = str3chars,
                locationId = str40chars,
                evseUid = str39chars,
                connectorId = str36chars
            )
        ) {
            get { status_code }
                .isEqualTo(OcpiStatusCode.INVALID_OR_MISSING_PARAMETERS.code)
        }

        expectThat(
            service.getConnector(
                countryCode = str2chars,
                partyId = str3chars,
                locationId = str39chars,
                evseUid = str40chars,
                connectorId = str36chars
            )
        ) {
            get { status_code }
                .isEqualTo(OcpiStatusCode.INVALID_OR_MISSING_PARAMETERS.code)
        }

        expectThat(
            service.getConnector(
                countryCode = str2chars,
                partyId = str3chars,
                locationId = str39chars,
                evseUid = str39chars,
                connectorId = str37chars
            )
        ) {
            get { status_code }
                .isEqualTo(OcpiStatusCode.INVALID_OR_MISSING_PARAMETERS.code)
        }
    }

    @Test
    fun patchLocationParamsValidationTest() {
        service = LocationsEmspService(locationsEmspRepository(emptyList()))

        expectThat(
            service.patchLocation(
                countryCode = str1char,
                partyId = str2chars,
                locationId = str4chars,
                location = dummyLocationPartial
            )
        ) {
            get { status_code }
                .isEqualTo(OcpiStatusCode.SUCCESS.code)
        }

        expectThat(
            service.patchLocation(
                countryCode = str2chars,
                partyId = str3chars,
                locationId = str39chars,
                location = dummyLocationPartial
            )
        ) {
            get { status_code }
                .isEqualTo(OcpiStatusCode.SUCCESS.code)
        }

        expectThat(
            service.patchLocation(
                countryCode = str3chars,
                partyId = str3chars,
                locationId = str39chars,
                location = dummyLocationPartial
            )
        ) {
            get { status_code }
                .isEqualTo(OcpiStatusCode.INVALID_OR_MISSING_PARAMETERS.code)
        }

        expectThat(
            service.patchLocation(
                countryCode = str2chars,
                partyId = str4chars,
                locationId = str39chars,
                location = dummyLocationPartial
            )
        ) {
            get { status_code }
                .isEqualTo(OcpiStatusCode.INVALID_OR_MISSING_PARAMETERS.code)
        }

        expectThat(
            service.patchLocation(
                countryCode = str2chars,
                partyId = str3chars,
                locationId = str40chars,
                location = dummyLocationPartial
            )
        ) {
            get { status_code }
                .isEqualTo(OcpiStatusCode.INVALID_OR_MISSING_PARAMETERS.code)
        }
    }

    @Test
    fun patchEvseParamsValidationTest() {
        service = LocationsEmspService(locationsEmspRepository(emptyList()))

        expectThat(
            service.patchEvse(
                countryCode = str1char,
                partyId = str2chars,
                locationId = str4chars,
                evseUid = str4chars,
                evse = dummyEvsePartial
            )
        ) {
            get { status_code }
                .isEqualTo(OcpiStatusCode.SUCCESS.code)
        }

        expectThat(
            service.patchEvse(
                countryCode = str2chars,
                partyId = str3chars,
                locationId = str39chars,
                evseUid = str39chars,
                evse = dummyEvsePartial
            )
        ) {
            get { status_code }
                .isEqualTo(OcpiStatusCode.SUCCESS.code)
        }

        expectThat(
            service.patchEvse(
                countryCode = str3chars,
                partyId = str3chars,
                locationId = str39chars,
                evseUid = str39chars,
                evse = dummyEvsePartial
            )
        ) {
            get { status_code }
                .isEqualTo(OcpiStatusCode.INVALID_OR_MISSING_PARAMETERS.code)
        }

        expectThat(
            service.patchEvse(
                countryCode = str2chars,
                partyId = str4chars,
                locationId = str39chars,
                evseUid = str39chars,
                evse = dummyEvsePartial
            )
        ) {
            get { status_code }
                .isEqualTo(OcpiStatusCode.INVALID_OR_MISSING_PARAMETERS.code)
        }

        expectThat(
            service.patchEvse(
                countryCode = str2chars,
                partyId = str3chars,
                locationId = str40chars,
                evseUid = str39chars,
                evse = dummyEvsePartial
            )
        ) {
            get { status_code }
                .isEqualTo(OcpiStatusCode.INVALID_OR_MISSING_PARAMETERS.code)
        }

        expectThat(
            service.patchEvse(
                countryCode = str2chars,
                partyId = str3chars,
                locationId = str39chars,
                evseUid = str40chars,
                evse = dummyEvsePartial
            )
        ) {
            get { status_code }
                .isEqualTo(OcpiStatusCode.INVALID_OR_MISSING_PARAMETERS.code)
        }
    }

    @Test
    fun patchConnectorParamsValidationTest() {
        service = LocationsEmspService(locationsEmspRepository(emptyList()))

        expectThat(
            service.patchConnector(
                countryCode = str1char,
                partyId = str2chars,
                locationId = str4chars,
                evseUid = str4chars,
                connectorId = str4chars,
                connector = dummyConnectorPartial
            )
        ) {
            get { status_code }
                .isEqualTo(OcpiStatusCode.SUCCESS.code)
        }

        expectThat(
            service.patchConnector(
                countryCode = str2chars,
                partyId = str3chars,
                locationId = str39chars,
                evseUid = str39chars,
                connectorId = str36chars,
                connector = dummyConnectorPartial
            )
        ) {
            get { status_code }
                .isEqualTo(OcpiStatusCode.SUCCESS.code)
        }

        expectThat(
            service.patchConnector(
                countryCode = str3chars,
                partyId = str3chars,
                locationId = str39chars,
                evseUid = str39chars,
                connectorId = str36chars,
                connector = dummyConnectorPartial
            )
        ) {
            get { status_code }
                .isEqualTo(OcpiStatusCode.INVALID_OR_MISSING_PARAMETERS.code)
        }

        expectThat(
            service.patchConnector(
                countryCode = str2chars,
                partyId = str4chars,
                locationId = str39chars,
                evseUid = str39chars,
                connectorId = str36chars,
                connector = dummyConnectorPartial
            )
        ) {
            get { status_code }
                .isEqualTo(OcpiStatusCode.INVALID_OR_MISSING_PARAMETERS.code)
        }

        expectThat(
            service.patchConnector(
                countryCode = str2chars,
                partyId = str3chars,
                locationId = str40chars,
                evseUid = str39chars,
                connectorId = str36chars,
                connector = dummyConnectorPartial
            )
        ) {
            get { status_code }
                .isEqualTo(OcpiStatusCode.INVALID_OR_MISSING_PARAMETERS.code)
        }

        expectThat(
            service.patchConnector(
                countryCode = str2chars,
                partyId = str3chars,
                locationId = str39chars,
                evseUid = str40chars,
                connectorId = str36chars,
                connector = dummyConnectorPartial
            )
        ) {
            get { status_code }
                .isEqualTo(OcpiStatusCode.INVALID_OR_MISSING_PARAMETERS.code)
        }

        expectThat(
            service.patchConnector(
                countryCode = str2chars,
                partyId = str3chars,
                locationId = str39chars,
                evseUid = str39chars,
                connectorId = str37chars,
                connector = dummyConnectorPartial
            )
        ) {
            get { status_code }
                .isEqualTo(OcpiStatusCode.INVALID_OR_MISSING_PARAMETERS.code)
        }
    }

    @Test
    fun putLocationParamsValidationTest() {
        service = LocationsEmspService(locationsEmspRepository(emptyList()))

        expectThat(
            service.putLocation(
                countryCode = str1char,
                partyId = str2chars,
                locationId = str4chars,
                location = dummyLocation
            )
        ) {
            get { status_code }
                .isEqualTo(OcpiStatusCode.SUCCESS.code)
        }

        expectThat(
            service.putLocation(
                countryCode = str2chars,
                partyId = str3chars,
                locationId = str39chars,
                location = dummyLocation
            )
        ) {
            get { status_code }
                .isEqualTo(OcpiStatusCode.SUCCESS.code)
        }

        expectThat(
            service.putLocation(
                countryCode = str3chars,
                partyId = str3chars,
                locationId = str39chars,
                location = dummyLocation
            )
        ) {
            get { status_code }
                .isEqualTo(OcpiStatusCode.INVALID_OR_MISSING_PARAMETERS.code)
        }

        expectThat(
            service.putLocation(
                countryCode = str2chars,
                partyId = str4chars,
                locationId = str39chars,
                location = dummyLocation
            )
        ) {
            get { status_code }
                .isEqualTo(OcpiStatusCode.INVALID_OR_MISSING_PARAMETERS.code)
        }

        expectThat(
            service.putLocation(
                countryCode = str2chars,
                partyId = str3chars,
                locationId = str40chars,
                location = dummyLocation
            )
        ) {
            get { status_code }
                .isEqualTo(OcpiStatusCode.INVALID_OR_MISSING_PARAMETERS.code)
        }
    }

    @Test
    fun putEvseParamsValidationTest() {
        service = LocationsEmspService(locationsEmspRepository(emptyList()))

        expectThat(
            service.putEvse(
                countryCode = str1char,
                partyId = str2chars,
                locationId = str4chars,
                evseUid = str4chars,
                evse = dummyEvse
            )
        ) {
            get { status_code }
                .isEqualTo(OcpiStatusCode.SUCCESS.code)
        }

        expectThat(
            service.putEvse(
                countryCode = str2chars,
                partyId = str3chars,
                locationId = str39chars,
                evseUid = str39chars,
                evse = dummyEvse
            )
        ) {
            get { status_code }
                .isEqualTo(OcpiStatusCode.SUCCESS.code)
        }

        expectThat(
            service.putEvse(
                countryCode = str3chars,
                partyId = str3chars,
                locationId = str39chars,
                evseUid = str39chars,
                evse = dummyEvse
            )
        ) {
            get { status_code }
                .isEqualTo(OcpiStatusCode.INVALID_OR_MISSING_PARAMETERS.code)
        }

        expectThat(
            service.putEvse(
                countryCode = str2chars,
                partyId = str4chars,
                locationId = str39chars,
                evseUid = str39chars,
                evse = dummyEvse
            )
        ) {
            get { status_code }
                .isEqualTo(OcpiStatusCode.INVALID_OR_MISSING_PARAMETERS.code)
        }

        expectThat(
            service.putEvse(
                countryCode = str2chars,
                partyId = str3chars,
                locationId = str40chars,
                evseUid = str39chars,
                evse = dummyEvse
            )
        ) {
            get { status_code }
                .isEqualTo(OcpiStatusCode.INVALID_OR_MISSING_PARAMETERS.code)
        }

        expectThat(
            service.putEvse(
                countryCode = str2chars,
                partyId = str3chars,
                locationId = str39chars,
                evseUid = str40chars,
                evse = dummyEvse
            )
        ) {
            get { status_code }
                .isEqualTo(OcpiStatusCode.INVALID_OR_MISSING_PARAMETERS.code)
        }
    }

    @Test
    fun putConnectorParamsValidationTest() {
        service = LocationsEmspService(locationsEmspRepository(emptyList()))

        expectThat(
            service.putConnector(
                countryCode = str1char,
                partyId = str2chars,
                locationId = str4chars,
                evseUid = str4chars,
                connectorId = str4chars,
                connector = dummyConnector
            )
        ) {
            get { status_code }
                .isEqualTo(OcpiStatusCode.SUCCESS.code)
        }

        expectThat(
            service.putConnector(
                countryCode = str2chars,
                partyId = str3chars,
                locationId = str39chars,
                evseUid = str39chars,
                connectorId = str36chars,
                connector = dummyConnector
            )
        ) {
            get { status_code }
                .isEqualTo(OcpiStatusCode.SUCCESS.code)
        }

        expectThat(
            service.putConnector(
                countryCode = str3chars,
                partyId = str3chars,
                locationId = str39chars,
                evseUid = str39chars,
                connectorId = str36chars,
                connector = dummyConnector
            )
        ) {
            get { status_code }
                .isEqualTo(OcpiStatusCode.INVALID_OR_MISSING_PARAMETERS.code)
        }

        expectThat(
            service.putConnector(
                countryCode = str2chars,
                partyId = str4chars,
                locationId = str39chars,
                evseUid = str39chars,
                connectorId = str36chars,
                connector = dummyConnector
            )
        ) {
            get { status_code }
                .isEqualTo(OcpiStatusCode.INVALID_OR_MISSING_PARAMETERS.code)
        }

        expectThat(
            service.putConnector(
                countryCode = str2chars,
                partyId = str3chars,
                locationId = str40chars,
                evseUid = str39chars,
                connectorId = str36chars,
                connector = dummyConnector
            )
        ) {
            get { status_code }
                .isEqualTo(OcpiStatusCode.INVALID_OR_MISSING_PARAMETERS.code)
        }

        expectThat(
            service.putConnector(
                countryCode = str2chars,
                partyId = str3chars,
                locationId = str39chars,
                evseUid = str40chars,
                connectorId = str36chars,
                connector = dummyConnector
            )
        ) {
            get { status_code }
                .isEqualTo(OcpiStatusCode.INVALID_OR_MISSING_PARAMETERS.code)
        }

        expectThat(
            service.putConnector(
                countryCode = str2chars,
                partyId = str3chars,
                locationId = str39chars,
                evseUid = str39chars,
                connectorId = str37chars,
                connector = dummyConnector
            )
        ) {
            get { status_code }
                .isEqualTo(OcpiStatusCode.INVALID_OR_MISSING_PARAMETERS.code)
        }
    }
}
