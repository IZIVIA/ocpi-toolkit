package com.izivia.ocpi.toolkit.modules.locations.services

import com.izivia.ocpi.toolkit.common.OcpiStatus
import com.izivia.ocpi.toolkit.modules.locations.domain.toPartial
import com.izivia.ocpi.toolkit.modules.locations.mock.locationsEmspRepository
import com.izivia.ocpi.toolkit.samples.common.validConnector
import com.izivia.ocpi.toolkit.samples.common.validEvse
import com.izivia.ocpi.toolkit.samples.common.validLocation
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Test
import strikt.api.expectThat
import strikt.assertions.isEqualTo

class LocationsEmspServiceTest {
    private lateinit var service: LocationsEmspService

    private val str1char = "a"
    private val str2chars = "ab"
    private val str3chars = "abc"
    private val str4chars = "abcd"
    private val str36chars = "abababababababababababababababababab"
    private val str37chars = "ababababababababababababababababababa"
    private val str40chars = "abababababababababababababababababababab"

    @Test
    fun getLocationParamsValidationTest() {
        service = LocationsEmspService(service = locationsEmspRepository(emptyList()))

        expectThat(
            runBlocking { service.getLocation(countryCode = str1char, partyId = str2chars, locationId = str4chars) },
        ) {
            get { statusCode }
                .isEqualTo(OcpiStatus.SUCCESS.code)
        }

        expectThat(
            runBlocking {
                service.getLocation(
                    countryCode = str2chars,
                    partyId = str3chars,
                    locationId = str36chars,
                )
            },
        ) {
            get { statusCode }
                .isEqualTo(OcpiStatus.SUCCESS.code)
        }

        expectThat(
            runBlocking {
                service.getLocation(
                    countryCode = str3chars,
                    partyId = str3chars,
                    locationId = str36chars,
                )
            },
        ) {
            get { statusCode }
                .isEqualTo(OcpiStatus.CLIENT_INVALID_PARAMETERS.code)
        }

        expectThat(
            runBlocking {
                service.getLocation(
                    countryCode = str2chars,
                    partyId = str4chars,
                    locationId = str36chars,
                )
            },
        ) {
            get { statusCode }
                .isEqualTo(OcpiStatus.CLIENT_INVALID_PARAMETERS.code)
        }

        expectThat(
            runBlocking {
                service.getLocation(
                    countryCode = str2chars,
                    partyId = str3chars,
                    locationId = str40chars,
                )
            },
        ) {
            get { statusCode }
                .isEqualTo(OcpiStatus.CLIENT_INVALID_PARAMETERS.code)
        }
    }

    @Test
    fun getEvseParamsValidationTest() {
        service = LocationsEmspService(service = locationsEmspRepository(emptyList()))

        expectThat(
            runBlocking {
                service.getEvse(
                    countryCode = str1char,
                    partyId = str2chars,
                    locationId = str4chars,
                    evseUid = str4chars,
                )
            },
        ) {
            get { statusCode }
                .isEqualTo(OcpiStatus.SUCCESS.code)
        }

        expectThat(
            runBlocking {
                service.getEvse(
                    countryCode = str2chars,
                    partyId = str3chars,
                    locationId = str36chars,
                    evseUid = str36chars,
                )
            },
        ) {
            get { statusCode }
                .isEqualTo(OcpiStatus.SUCCESS.code)
        }

        expectThat(
            runBlocking {
                service.getEvse(
                    countryCode = str3chars,
                    partyId = str3chars,
                    locationId = str36chars,
                    evseUid = str36chars,
                )
            },
        ) {
            get { statusCode }
                .isEqualTo(OcpiStatus.CLIENT_INVALID_PARAMETERS.code)
        }

        expectThat(
            runBlocking {
                service.getEvse(
                    countryCode = str2chars,
                    partyId = str4chars,
                    locationId = str36chars,
                    evseUid = str36chars,
                )
            },
        ) {
            get { statusCode }
                .isEqualTo(OcpiStatus.CLIENT_INVALID_PARAMETERS.code)
        }

        expectThat(
            runBlocking {
                service.getEvse(
                    countryCode = str2chars,
                    partyId = str3chars,
                    locationId = str40chars,
                    evseUid = str36chars,
                )
            },
        ) {
            get { statusCode }
                .isEqualTo(OcpiStatus.CLIENT_INVALID_PARAMETERS.code)
        }

        expectThat(
            runBlocking {
                service.getEvse(
                    countryCode = str2chars,
                    partyId = str3chars,
                    locationId = str36chars,
                    evseUid = str40chars,
                )
            },
        ) {
            get { statusCode }
                .isEqualTo(OcpiStatus.CLIENT_INVALID_PARAMETERS.code)
        }
    }

    @Test
    fun getConnectorParamsValidationTest() {
        service = LocationsEmspService(service = locationsEmspRepository(emptyList()))

        expectThat(
            runBlocking {
                service.getConnector(
                    countryCode = str1char,
                    partyId = str2chars,
                    locationId = str4chars,
                    evseUid = str4chars,
                    connectorId = str4chars,
                )
            },
        ) {
            get { statusCode }
                .isEqualTo(OcpiStatus.SUCCESS.code)
        }

        expectThat(
            runBlocking
            {
                service.getConnector(
                    countryCode = str2chars,
                    partyId = str3chars,
                    locationId = str36chars,
                    evseUid = str36chars,
                    connectorId = str36chars,
                )
            },
        ) {
            get { statusCode }
                .isEqualTo(OcpiStatus.SUCCESS.code)
        }

        expectThat(
            runBlocking
            {
                service.getConnector(
                    countryCode = str3chars,
                    partyId = str3chars,
                    locationId = str36chars,
                    evseUid = str36chars,
                    connectorId = str36chars,
                )
            },
        ) {
            get { statusCode }
                .isEqualTo(OcpiStatus.CLIENT_INVALID_PARAMETERS.code)
        }

        expectThat(
            runBlocking
            {
                service.getConnector(
                    countryCode = str2chars,
                    partyId = str4chars,
                    locationId = str36chars,
                    evseUid = str36chars,
                    connectorId = str36chars,
                )
            },
        ) {
            get { statusCode }
                .isEqualTo(OcpiStatus.CLIENT_INVALID_PARAMETERS.code)
        }

        expectThat(
            runBlocking
            {
                service.getConnector(
                    countryCode = str2chars,
                    partyId = str3chars,
                    locationId = str40chars,
                    evseUid = str36chars,
                    connectorId = str36chars,
                )
            },
        ) {
            get { statusCode }
                .isEqualTo(OcpiStatus.CLIENT_INVALID_PARAMETERS.code)
        }

        expectThat(
            runBlocking {
                service.getConnector(
                    countryCode = str2chars,
                    partyId = str3chars,
                    locationId = str36chars,
                    evseUid = str40chars,
                    connectorId = str36chars,
                )
            },
        ) {
            get { statusCode }
                .isEqualTo(OcpiStatus.CLIENT_INVALID_PARAMETERS.code)
        }

        expectThat(
            runBlocking {
                service.getConnector(
                    countryCode = str2chars,
                    partyId = str3chars,
                    locationId = str36chars,
                    evseUid = str36chars,
                    connectorId = str37chars,
                )
            },
        ) {
            get { statusCode }
                .isEqualTo(OcpiStatus.CLIENT_INVALID_PARAMETERS.code)
        }
    }

    @Test
    fun patchLocationParamsValidationTest() {
        service = LocationsEmspService(service = locationsEmspRepository(emptyList()))

        expectThat(
            runBlocking {
                service.patchLocation(
                    countryCode = str1char,
                    partyId = str2chars,
                    locationId = str4chars,
                    location = validLocation.toPartial(),
                )
            },
        ) {
            get { statusCode }
                .isEqualTo(OcpiStatus.SUCCESS.code)
        }

        expectThat(
            runBlocking {
                service.patchLocation(
                    countryCode = str2chars,
                    partyId = str3chars,
                    locationId = str36chars,
                    location = validLocation.toPartial(),
                )
            },
        ) {
            get { statusCode }
                .isEqualTo(OcpiStatus.SUCCESS.code)
        }

        expectThat(
            runBlocking {
                service.patchLocation(
                    countryCode = str3chars,
                    partyId = str3chars,
                    locationId = str36chars,
                    location = validLocation.toPartial(),
                )
            },
        ) {
            get { statusCode }
                .isEqualTo(OcpiStatus.CLIENT_INVALID_PARAMETERS.code)
        }

        expectThat(
            runBlocking {
                service.patchLocation(
                    countryCode = str2chars,
                    partyId = str4chars,
                    locationId = str36chars,
                    location = validLocation.toPartial(),
                )
            },
        ) {
            get { statusCode }
                .isEqualTo(OcpiStatus.CLIENT_INVALID_PARAMETERS.code)
        }

        expectThat(
            runBlocking {
                service.patchLocation(
                    countryCode = str2chars,
                    partyId = str3chars,
                    locationId = str40chars,
                    location = validLocation.toPartial(),
                )
            },
        ) {
            get { statusCode }
                .isEqualTo(OcpiStatus.CLIENT_INVALID_PARAMETERS.code)
        }
    }

    @Test
    fun patchEvseParamsValidationTest() {
        service = LocationsEmspService(service = locationsEmspRepository(emptyList()))

        expectThat(
            runBlocking {
                service.patchEvse(
                    countryCode = str1char,
                    partyId = str2chars,
                    locationId = str4chars,
                    evseUid = str4chars,
                    evse = validEvse.toPartial(),
                )
            },
        ) {
            get { statusCode }
                .isEqualTo(OcpiStatus.SUCCESS.code)
        }

        expectThat(
            runBlocking {
                service.patchEvse(
                    countryCode = str2chars,
                    partyId = str3chars,
                    locationId = str36chars,
                    evseUid = str36chars,
                    evse = validEvse.toPartial(),
                )
            },
        ) {
            get { statusCode }
                .isEqualTo(OcpiStatus.SUCCESS.code)
        }

        expectThat(
            runBlocking {
                service.patchEvse(
                    countryCode = str3chars,
                    partyId = str3chars,
                    locationId = str36chars,
                    evseUid = str36chars,
                    evse = validEvse.toPartial(),
                )
            },
        ) {
            get { statusCode }
                .isEqualTo(OcpiStatus.CLIENT_INVALID_PARAMETERS.code)
        }

        expectThat(
            runBlocking {
                service.patchEvse(
                    countryCode = str2chars,
                    partyId = str4chars,
                    locationId = str36chars,
                    evseUid = str36chars,
                    evse = validEvse.toPartial(),
                )
            },
        ) {
            get { statusCode }
                .isEqualTo(OcpiStatus.CLIENT_INVALID_PARAMETERS.code)
        }

        expectThat(
            runBlocking {
                service.patchEvse(
                    countryCode = str2chars,
                    partyId = str3chars,
                    locationId = str40chars,
                    evseUid = str36chars,
                    evse = validEvse.toPartial(),
                )
            },
        ) {
            get { statusCode }
                .isEqualTo(OcpiStatus.CLIENT_INVALID_PARAMETERS.code)
        }

        expectThat(
            runBlocking {
                service.patchEvse(
                    countryCode = str2chars,
                    partyId = str3chars,
                    locationId = str36chars,
                    evseUid = str40chars,
                    evse = validEvse.toPartial(),
                )
            },
        ) {
            get { statusCode }
                .isEqualTo(OcpiStatus.CLIENT_INVALID_PARAMETERS.code)
        }
    }

    @Test
    fun patchConnectorParamsValidationTest() {
        service = LocationsEmspService(service = locationsEmspRepository(emptyList()))

        expectThat(
            runBlocking {
                service.patchConnector(
                    countryCode = str1char,
                    partyId = str2chars,
                    locationId = str4chars,
                    evseUid = str4chars,
                    connectorId = str4chars,
                    connector = validConnector.toPartial(),
                )
            },
        ) {
            get { statusCode }
                .isEqualTo(OcpiStatus.SUCCESS.code)
        }

        expectThat(
            runBlocking {
                service.patchConnector(
                    countryCode = str2chars,
                    partyId = str3chars,
                    locationId = str36chars,
                    evseUid = str36chars,
                    connectorId = str36chars,
                    connector = validConnector.toPartial(),
                )
            },
        ) {
            get { statusCode }
                .isEqualTo(OcpiStatus.SUCCESS.code)
        }

        expectThat(
            runBlocking {
                service.patchConnector(
                    countryCode = str3chars,
                    partyId = str3chars,
                    locationId = str36chars,
                    evseUid = str36chars,
                    connectorId = str36chars,
                    connector = validConnector.toPartial(),
                )
            },
        ) {
            get { statusCode }
                .isEqualTo(OcpiStatus.CLIENT_INVALID_PARAMETERS.code)
        }

        expectThat(
            runBlocking {
                service.patchConnector(
                    countryCode = str2chars,
                    partyId = str4chars,
                    locationId = str36chars,
                    evseUid = str36chars,
                    connectorId = str36chars,
                    connector = validConnector.toPartial(),
                )
            },
        ) {
            get { statusCode }
                .isEqualTo(OcpiStatus.CLIENT_INVALID_PARAMETERS.code)
        }

        expectThat(
            runBlocking {
                service.patchConnector(
                    countryCode = str2chars,
                    partyId = str3chars,
                    locationId = str40chars,
                    evseUid = str36chars,
                    connectorId = str36chars,
                    connector = validConnector.toPartial(),
                )
            },
        ) {
            get { statusCode }
                .isEqualTo(OcpiStatus.CLIENT_INVALID_PARAMETERS.code)
        }

        expectThat(
            runBlocking {
                service.patchConnector(
                    countryCode = str2chars,
                    partyId = str3chars,
                    locationId = str36chars,
                    evseUid = str40chars,
                    connectorId = str36chars,
                    connector = validConnector.toPartial(),
                )
            },
        ) {
            get { statusCode }
                .isEqualTo(OcpiStatus.CLIENT_INVALID_PARAMETERS.code)
        }

        expectThat(
            runBlocking {
                service.patchConnector(
                    countryCode = str2chars,
                    partyId = str3chars,
                    locationId = str36chars,
                    evseUid = str36chars,
                    connectorId = str37chars,
                    connector = validConnector.toPartial(),
                )
            },
        ) {
            get { statusCode }
                .isEqualTo(OcpiStatus.CLIENT_INVALID_PARAMETERS.code)
        }
    }

    @Test
    fun putLocationParamsValidationTest() {
        service = LocationsEmspService(service = locationsEmspRepository(emptyList()))

        expectThat(
            runBlocking {
                service.putLocation(
                    countryCode = validLocation.countryCode,
                    partyId = validLocation.partyId,
                    locationId = validLocation.id,
                    location = validLocation,
                )
            },
        ) {
            get { statusCode }
                .isEqualTo(OcpiStatus.SUCCESS.code)
        }

        expectThat(
            runBlocking {
                service.putLocation(
                    countryCode = str3chars,
                    partyId = str3chars,
                    locationId = validLocation.id,
                    location = validLocation,
                )
            },
        ) {
            get { statusCode }
                .isEqualTo(OcpiStatus.CLIENT_INVALID_PARAMETERS.code)
        }

        expectThat(
            runBlocking {
                service.putLocation(
                    countryCode = str2chars,
                    partyId = str4chars,
                    locationId = validLocation.id,
                    location = validLocation,
                )
            },
        ) {
            get { statusCode }
                .isEqualTo(OcpiStatus.CLIENT_INVALID_PARAMETERS.code)
        }

        expectThat(
            runBlocking {
                service.putLocation(
                    countryCode = str2chars,
                    partyId = str3chars,
                    locationId = str40chars,
                    location = validLocation,
                )
            },
        ) {
            get { statusCode }
                .isEqualTo(OcpiStatus.CLIENT_INVALID_PARAMETERS.code)
        }
    }

    @Test
    fun putEvseParamsValidationTest() {
        service = LocationsEmspService(service = locationsEmspRepository(emptyList()))

        expectThat(
            runBlocking {
                service.putEvse(
                    countryCode = str1char,
                    partyId = str2chars,
                    locationId = str4chars,
                    evseUid = validEvse.uid,
                    evse = validEvse,
                )
            },
        ) {
            get { statusCode }
                .isEqualTo(OcpiStatus.SUCCESS.code)
        }

        expectThat(
            runBlocking {
                service.putEvse(
                    countryCode = str2chars,
                    partyId = str3chars,
                    locationId = str36chars,
                    evseUid = validEvse.uid,
                    evse = validEvse,
                )
            },
        ) {
            get { statusCode }
                .isEqualTo(OcpiStatus.SUCCESS.code)
        }

        expectThat(
            runBlocking {
                service.putEvse(
                    countryCode = str3chars,
                    partyId = str3chars,
                    locationId = str36chars,
                    evseUid = validEvse.uid,
                    evse = validEvse,
                )
            },
        ) {
            get { statusCode }
                .isEqualTo(OcpiStatus.CLIENT_INVALID_PARAMETERS.code)
        }

        expectThat(
            runBlocking {
                service.putEvse(
                    countryCode = str2chars,
                    partyId = str4chars,
                    locationId = str36chars,
                    evseUid = validEvse.uid,
                    evse = validEvse,
                )
            },
        ) {
            get { statusCode }
                .isEqualTo(OcpiStatus.CLIENT_INVALID_PARAMETERS.code)
        }

        expectThat(
            runBlocking {
                service.putEvse(
                    countryCode = str2chars,
                    partyId = str3chars,
                    locationId = str40chars,
                    evseUid = validEvse.uid,
                    evse = validEvse,
                )
            },
        ) {
            get { statusCode }
                .isEqualTo(OcpiStatus.CLIENT_INVALID_PARAMETERS.code)
        }

        expectThat(
            runBlocking {
                service.putEvse(
                    countryCode = str2chars,
                    partyId = str3chars,
                    locationId = str36chars,
                    evseUid = str40chars,
                    evse = validEvse,
                )
            },
        ) {
            get { statusCode }
                .isEqualTo(OcpiStatus.CLIENT_INVALID_PARAMETERS.code)
        }
    }

    @Test
    fun putConnectorParamsValidationTest() {
        service = LocationsEmspService(service = locationsEmspRepository(emptyList()))

        expectThat(
            runBlocking {
                service.putConnector(
                    countryCode = str1char,
                    partyId = str2chars,
                    locationId = str4chars,
                    evseUid = str4chars,
                    connectorId = validConnector.id,
                    connector = validConnector,
                )
            },
        ) {
            get { statusCode }
                .isEqualTo(OcpiStatus.SUCCESS.code)
        }

        expectThat(
            runBlocking {
                service.putConnector(
                    countryCode = str2chars,
                    partyId = str3chars,
                    locationId = str36chars,
                    evseUid = str36chars,
                    connectorId = validConnector.id,
                    connector = validConnector,
                )
            },
        ) {
            get { statusCode }
                .isEqualTo(OcpiStatus.SUCCESS.code)
        }

        expectThat(
            runBlocking {
                service.putConnector(
                    countryCode = str3chars,
                    partyId = str3chars,
                    locationId = str36chars,
                    evseUid = str36chars,
                    connectorId = validConnector.id,
                    connector = validConnector,
                )
            },
        ) {
            get { statusCode }
                .isEqualTo(OcpiStatus.CLIENT_INVALID_PARAMETERS.code)
        }

        expectThat(
            runBlocking {
                service.putConnector(
                    countryCode = str2chars,
                    partyId = str4chars,
                    locationId = str36chars,
                    evseUid = str36chars,
                    connectorId = validConnector.id,
                    connector = validConnector,
                )
            },
        ) {
            get { statusCode }
                .isEqualTo(OcpiStatus.CLIENT_INVALID_PARAMETERS.code)
        }

        expectThat(
            runBlocking {
                service.putConnector(
                    countryCode = str2chars,
                    partyId = str3chars,
                    locationId = str40chars,
                    evseUid = str36chars,
                    connectorId = validConnector.id,
                    connector = validConnector,
                )
            },
        ) {
            get { statusCode }
                .isEqualTo(OcpiStatus.CLIENT_INVALID_PARAMETERS.code)
        }

        expectThat(
            runBlocking {
                service.putConnector(
                    countryCode = str2chars,
                    partyId = str3chars,
                    locationId = str36chars,
                    evseUid = str40chars,
                    connectorId = validConnector.id,
                    connector = validConnector,
                )
            },
        ) {
            get { statusCode }
                .isEqualTo(OcpiStatus.CLIENT_INVALID_PARAMETERS.code)
        }

        expectThat(
            runBlocking {
                service.putConnector(
                    countryCode = str2chars,
                    partyId = str3chars,
                    locationId = str36chars,
                    evseUid = str36chars,
                    connectorId = str37chars,
                    connector = validConnector,
                )
            },
        ) {
            get { statusCode }
                .isEqualTo(OcpiStatus.CLIENT_INVALID_PARAMETERS.code)
        }
    }
}
