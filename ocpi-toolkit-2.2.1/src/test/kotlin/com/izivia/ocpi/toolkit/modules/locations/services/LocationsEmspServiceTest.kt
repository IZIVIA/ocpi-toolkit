package com.izivia.ocpi.toolkit.modules.locations.services

import com.izivia.ocpi.toolkit.common.OcpiClientInvalidParametersException
import com.izivia.ocpi.toolkit.modules.locations.domain.toPartial
import com.izivia.ocpi.toolkit.modules.locations.mock.locationsEmspRepository
import com.izivia.ocpi.toolkit.samples.common.validConnector
import com.izivia.ocpi.toolkit.samples.common.validEvse
import com.izivia.ocpi.toolkit.samples.common.validLocation
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Test
import strikt.api.expectDoesNotThrow
import strikt.api.expectThrows

class LocationsEmspServiceTest {
    private val str1char = "a"
    private val str2chars = "ab"
    private val str3chars = "abc"
    private val str4chars = "abcd"
    private val str36chars = "abababababababababababababababababab"
    private val str37chars = "ababababababababababababababababababa"
    private val str40chars = "abababababababababababababababababababab"

    @Test
    fun getLocationParamsValidationTest() {
        val service = LocationsEmspValidator(LocationsEmspService(locationsEmspRepository(emptyList())))

        expectDoesNotThrow {
            runBlocking { service.getLocation(countryCode = str1char, partyId = str2chars, locationId = str4chars) }
        }

        expectDoesNotThrow {
            runBlocking {
                service.getLocation(
                    countryCode = str2chars,
                    partyId = str3chars,
                    locationId = str36chars,
                )
            }
        }

        expectThrows<OcpiClientInvalidParametersException> {
            runBlocking {
                service.getLocation(
                    countryCode = str3chars,
                    partyId = str3chars,
                    locationId = str36chars,
                )
            }
        }

        expectThrows<OcpiClientInvalidParametersException> {
            runBlocking {
                service.getLocation(
                    countryCode = str2chars,
                    partyId = str4chars,
                    locationId = str36chars,
                )
            }
        }

        expectThrows<OcpiClientInvalidParametersException> {
            runBlocking {
                service.getLocation(
                    countryCode = str2chars,
                    partyId = str3chars,
                    locationId = str40chars,
                )
            }
        }
    }

    @Test
    fun getEvseParamsValidationTest() {
        val service = LocationsEmspValidator(LocationsEmspService(locationsEmspRepository(emptyList())))

        expectDoesNotThrow {
            runBlocking {
                service.getEvse(
                    countryCode = str1char,
                    partyId = str2chars,
                    locationId = str4chars,
                    evseUid = str4chars,
                )
            }
        }

        expectDoesNotThrow {
            runBlocking {
                service.getEvse(
                    countryCode = str2chars,
                    partyId = str3chars,
                    locationId = str36chars,
                    evseUid = str36chars,
                )
            }
        }

        expectThrows<OcpiClientInvalidParametersException> {
            runBlocking {
                service.getEvse(
                    countryCode = str3chars,
                    partyId = str3chars,
                    locationId = str36chars,
                    evseUid = str36chars,
                )
            }
        }

        expectThrows<OcpiClientInvalidParametersException> {
            runBlocking {
                service.getEvse(
                    countryCode = str2chars,
                    partyId = str4chars,
                    locationId = str36chars,
                    evseUid = str36chars,
                )
            }
        }

        expectThrows<OcpiClientInvalidParametersException> {
            runBlocking {
                service.getEvse(
                    countryCode = str2chars,
                    partyId = str3chars,
                    locationId = str40chars,
                    evseUid = str36chars,
                )
            }
        }

        expectThrows<OcpiClientInvalidParametersException> {
            runBlocking {
                service.getEvse(
                    countryCode = str2chars,
                    partyId = str3chars,
                    locationId = str36chars,
                    evseUid = str40chars,
                )
            }
        }
    }

    @Test
    fun getConnectorParamsValidationTest() {
        val service = LocationsEmspValidator(LocationsEmspService(locationsEmspRepository(emptyList())))

        expectDoesNotThrow {
            runBlocking {
                service.getConnector(
                    countryCode = str1char,
                    partyId = str2chars,
                    locationId = str4chars,
                    evseUid = str4chars,
                    connectorId = str4chars,
                )
            }
        }

        expectDoesNotThrow {
            runBlocking {
                service.getConnector(
                    countryCode = str2chars,
                    partyId = str3chars,
                    locationId = str36chars,
                    evseUid = str36chars,
                    connectorId = str36chars,
                )
            }
        }

        expectThrows<OcpiClientInvalidParametersException> {
            runBlocking {
                service.getConnector(
                    countryCode = str3chars,
                    partyId = str3chars,
                    locationId = str36chars,
                    evseUid = str36chars,
                    connectorId = str36chars,
                )
            }
        }

        expectThrows<OcpiClientInvalidParametersException> {
            runBlocking {
                service.getConnector(
                    countryCode = str2chars,
                    partyId = str4chars,
                    locationId = str36chars,
                    evseUid = str36chars,
                    connectorId = str36chars,
                )
            }
        }

        expectThrows<OcpiClientInvalidParametersException> {
            runBlocking {
                service.getConnector(
                    countryCode = str2chars,
                    partyId = str3chars,
                    locationId = str40chars,
                    evseUid = str36chars,
                    connectorId = str36chars,
                )
            }
        }

        expectThrows<OcpiClientInvalidParametersException> {
            runBlocking {
                service.getConnector(
                    countryCode = str2chars,
                    partyId = str3chars,
                    locationId = str36chars,
                    evseUid = str40chars,
                    connectorId = str36chars,
                )
            }
        }

        expectThrows<OcpiClientInvalidParametersException> {
            runBlocking {
                service.getConnector(
                    countryCode = str2chars,
                    partyId = str3chars,
                    locationId = str36chars,
                    evseUid = str36chars,
                    connectorId = str37chars,
                )
            }
        }
    }

    @Test
    fun patchLocationParamsValidationTest() {
        val service = LocationsEmspValidator(LocationsEmspService(locationsEmspRepository(emptyList())))

        expectDoesNotThrow {
            runBlocking {
                service.patchLocation(
                    countryCode = str1char,
                    partyId = str2chars,
                    locationId = str4chars,
                    location = validLocation.toPartial(),
                )
            }
        }

        expectDoesNotThrow {
            runBlocking {
                service.patchLocation(
                    countryCode = str2chars,
                    partyId = str3chars,
                    locationId = str36chars,
                    location = validLocation.toPartial(),
                )
            }
        }

        expectThrows<OcpiClientInvalidParametersException> {
            runBlocking {
                service.patchLocation(
                    countryCode = str3chars,
                    partyId = str3chars,
                    locationId = str36chars,
                    location = validLocation.toPartial(),
                )
            }
        }

        expectThrows<OcpiClientInvalidParametersException> {
            runBlocking {
                service.patchLocation(
                    countryCode = str2chars,
                    partyId = str4chars,
                    locationId = str36chars,
                    location = validLocation.toPartial(),
                )
            }
        }

        expectThrows<OcpiClientInvalidParametersException> {
            runBlocking {
                service.patchLocation(
                    countryCode = str2chars,
                    partyId = str3chars,
                    locationId = str40chars,
                    location = validLocation.toPartial(),
                )
            }
        }
    }

    @Test
    fun patchEvseParamsValidationTest() {
        val service = LocationsEmspValidator(LocationsEmspService(locationsEmspRepository(emptyList())))

        expectDoesNotThrow {
            runBlocking {
                service.patchEvse(
                    countryCode = str1char,
                    partyId = str2chars,
                    locationId = str4chars,
                    evseUid = str4chars,
                    evse = validEvse.toPartial(),
                )
            }
        }

        expectDoesNotThrow {
            runBlocking {
                service.patchEvse(
                    countryCode = str2chars,
                    partyId = str3chars,
                    locationId = str36chars,
                    evseUid = str36chars,
                    evse = validEvse.toPartial(),
                )
            }
        }

        expectThrows<OcpiClientInvalidParametersException> {
            runBlocking {
                service.patchEvse(
                    countryCode = str3chars,
                    partyId = str3chars,
                    locationId = str36chars,
                    evseUid = str36chars,
                    evse = validEvse.toPartial(),
                )
            }
        }

        expectThrows<OcpiClientInvalidParametersException> {
            runBlocking {
                service.patchEvse(
                    countryCode = str2chars,
                    partyId = str4chars,
                    locationId = str36chars,
                    evseUid = str36chars,
                    evse = validEvse.toPartial(),
                )
            }
        }

        expectThrows<OcpiClientInvalidParametersException> {
            runBlocking {
                service.patchEvse(
                    countryCode = str2chars,
                    partyId = str3chars,
                    locationId = str40chars,
                    evseUid = str36chars,
                    evse = validEvse.toPartial(),
                )
            }
        }

        expectThrows<OcpiClientInvalidParametersException> {
            runBlocking {
                service.patchEvse(
                    countryCode = str2chars,
                    partyId = str3chars,
                    locationId = str36chars,
                    evseUid = str40chars,
                    evse = validEvse.toPartial(),
                )
            }
        }
    }

    @Test
    fun patchConnectorParamsValidationTest() {
        val service = LocationsEmspValidator(LocationsEmspService(locationsEmspRepository(emptyList())))

        expectDoesNotThrow {
            runBlocking {
                service.patchConnector(
                    countryCode = str1char,
                    partyId = str2chars,
                    locationId = str4chars,
                    evseUid = str4chars,
                    connectorId = str4chars,
                    connector = validConnector.toPartial(),
                )
            }
        }

        expectDoesNotThrow {
            runBlocking {
                service.patchConnector(
                    countryCode = str2chars,
                    partyId = str3chars,
                    locationId = str36chars,
                    evseUid = str36chars,
                    connectorId = str36chars,
                    connector = validConnector.toPartial(),
                )
            }
        }

        expectThrows<OcpiClientInvalidParametersException> {
            runBlocking {
                service.patchConnector(
                    countryCode = str3chars,
                    partyId = str3chars,
                    locationId = str36chars,
                    evseUid = str36chars,
                    connectorId = str36chars,
                    connector = validConnector.toPartial(),
                )
            }
        }

        expectThrows<OcpiClientInvalidParametersException> {
            runBlocking {
                service.patchConnector(
                    countryCode = str2chars,
                    partyId = str4chars,
                    locationId = str36chars,
                    evseUid = str36chars,
                    connectorId = str36chars,
                    connector = validConnector.toPartial(),
                )
            }
        }

        expectThrows<OcpiClientInvalidParametersException> {
            runBlocking {
                service.patchConnector(
                    countryCode = str2chars,
                    partyId = str3chars,
                    locationId = str40chars,
                    evseUid = str36chars,
                    connectorId = str36chars,
                    connector = validConnector.toPartial(),
                )
            }
        }

        expectThrows<OcpiClientInvalidParametersException> {
            runBlocking {
                service.patchConnector(
                    countryCode = str2chars,
                    partyId = str3chars,
                    locationId = str36chars,
                    evseUid = str40chars,
                    connectorId = str36chars,
                    connector = validConnector.toPartial(),
                )
            }
        }

        expectThrows<OcpiClientInvalidParametersException> {
            runBlocking {
                service.patchConnector(
                    countryCode = str2chars,
                    partyId = str3chars,
                    locationId = str36chars,
                    evseUid = str36chars,
                    connectorId = str37chars,
                    connector = validConnector.toPartial(),
                )
            }
        }
    }

    @Test
    fun putLocationParamsValidationTest() {
        val service = LocationsEmspValidator(LocationsEmspService(locationsEmspRepository(emptyList())))

        expectDoesNotThrow {
            runBlocking {
                service.putLocation(
                    countryCode = validLocation.countryCode,
                    partyId = validLocation.partyId,
                    locationId = validLocation.id,
                    location = validLocation,
                )
            }
        }

        expectThrows<OcpiClientInvalidParametersException> {
            runBlocking {
                service.putLocation(
                    countryCode = str3chars,
                    partyId = str3chars,
                    locationId = validLocation.id,
                    location = validLocation,
                )
            }
        }

        expectThrows<OcpiClientInvalidParametersException> {
            runBlocking {
                service.putLocation(
                    countryCode = str2chars,
                    partyId = str4chars,
                    locationId = validLocation.id,
                    location = validLocation,
                )
            }
        }

        expectThrows<OcpiClientInvalidParametersException> {
            runBlocking {
                service.putLocation(
                    countryCode = str2chars,
                    partyId = str3chars,
                    locationId = str40chars,
                    location = validLocation,
                )
            }
        }
    }

    @Test
    fun putEvseParamsValidationTest() {
        val service = LocationsEmspValidator(LocationsEmspService(locationsEmspRepository(emptyList())))

        expectDoesNotThrow {
            runBlocking {
                service.putEvse(
                    countryCode = str1char,
                    partyId = str2chars,
                    locationId = str4chars,
                    evseUid = validEvse.uid,
                    evse = validEvse,
                )
            }
        }

        expectDoesNotThrow {
            runBlocking {
                service.putEvse(
                    countryCode = str2chars,
                    partyId = str3chars,
                    locationId = str36chars,
                    evseUid = validEvse.uid,
                    evse = validEvse,
                )
            }
        }

        expectThrows<OcpiClientInvalidParametersException> {
            runBlocking {
                service.putEvse(
                    countryCode = str3chars,
                    partyId = str3chars,
                    locationId = str36chars,
                    evseUid = validEvse.uid,
                    evse = validEvse,
                )
            }
        }

        expectThrows<OcpiClientInvalidParametersException> {
            runBlocking {
                service.putEvse(
                    countryCode = str2chars,
                    partyId = str4chars,
                    locationId = str36chars,
                    evseUid = validEvse.uid,
                    evse = validEvse,
                )
            }
        }

        expectThrows<OcpiClientInvalidParametersException> {
            runBlocking {
                service.putEvse(
                    countryCode = str2chars,
                    partyId = str3chars,
                    locationId = str40chars,
                    evseUid = validEvse.uid,
                    evse = validEvse,
                )
            }
        }

        expectThrows<OcpiClientInvalidParametersException> {
            runBlocking {
                service.putEvse(
                    countryCode = str2chars,
                    partyId = str3chars,
                    locationId = str36chars,
                    evseUid = str40chars,
                    evse = validEvse,
                )
            }
        }
    }

    @Test
    fun putConnectorParamsValidationTest() {
        val service = LocationsEmspValidator(LocationsEmspService(locationsEmspRepository(emptyList())))

        expectDoesNotThrow {
            runBlocking {
                service.putConnector(
                    countryCode = str1char,
                    partyId = str2chars,
                    locationId = str4chars,
                    evseUid = str4chars,
                    connectorId = validConnector.id,
                    connector = validConnector,
                )
            }
        }

        expectDoesNotThrow {
            runBlocking {
                service.putConnector(
                    countryCode = str2chars,
                    partyId = str3chars,
                    locationId = str36chars,
                    evseUid = str36chars,
                    connectorId = validConnector.id,
                    connector = validConnector,
                )
            }
        }

        expectThrows<OcpiClientInvalidParametersException> {
            runBlocking {
                service.putConnector(
                    countryCode = str3chars,
                    partyId = str3chars,
                    locationId = str36chars,
                    evseUid = str36chars,
                    connectorId = validConnector.id,
                    connector = validConnector,
                )
            }
        }

        expectThrows<OcpiClientInvalidParametersException> {
            runBlocking {
                service.putConnector(
                    countryCode = str2chars,
                    partyId = str4chars,
                    locationId = str36chars,
                    evseUid = str36chars,
                    connectorId = validConnector.id,
                    connector = validConnector,
                )
            }
        }

        expectThrows<OcpiClientInvalidParametersException> {
            runBlocking {
                service.putConnector(
                    countryCode = str2chars,
                    partyId = str3chars,
                    locationId = str40chars,
                    evseUid = str36chars,
                    connectorId = validConnector.id,
                    connector = validConnector,
                )
            }
        }

        expectThrows<OcpiClientInvalidParametersException> {
            runBlocking {
                service.putConnector(
                    countryCode = str2chars,
                    partyId = str3chars,
                    locationId = str36chars,
                    evseUid = str40chars,
                    connectorId = validConnector.id,
                    connector = validConnector,
                )
            }
        }

        expectThrows<OcpiClientInvalidParametersException> {
            runBlocking {
                service.putConnector(
                    countryCode = str2chars,
                    partyId = str3chars,
                    locationId = str36chars,
                    evseUid = str36chars,
                    connectorId = str37chars,
                    connector = validConnector,
                )
            }
        }
    }
}
