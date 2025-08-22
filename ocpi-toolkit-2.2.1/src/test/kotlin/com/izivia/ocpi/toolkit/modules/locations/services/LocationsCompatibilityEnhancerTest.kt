package com.izivia.ocpi.toolkit.modules.locations.services

import com.izivia.ocpi.toolkit.modules.locations.domain.*
import com.izivia.ocpi.toolkit.modules.locations.repositories.LocationsEmspRepository
import com.izivia.ocpi.toolkit.samples.common.validConnector
import com.izivia.ocpi.toolkit.samples.common.validEvse
import com.izivia.ocpi.toolkit.samples.common.validLocation
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import org.junit.jupiter.api.DynamicTest
import org.junit.jupiter.api.TestFactory
import strikt.api.expectDoesNotThrow

class LocationsCompatibilityEnhancerTest {

    data class TestCase<T>(
        val name: String,
        val objectProvider: () -> T,
    )

    private fun mocks() =
        mockk<LocationsEmspRepository>(relaxed = true) {
            coEvery { putLocation(any(), any(), any(), any()) } coAnswers { validLocation }
            coEvery { patchLocation(any(), any(), any(), any()) } coAnswers { validLocation }
            coEvery { putEvse(any(), any(), any(), any(), any()) } coAnswers { validEvse }
            coEvery { patchEvse(any(), any(), any(), any(), any()) } coAnswers { validEvse }
            coEvery { putConnector(any(), any(), any(), any(), any(), any()) } coAnswers { validConnector }
            coEvery { patchConnector(any(), any(), any(), any(), any(), any()) } coAnswers { validConnector }
        }.let {
            Pair(it, LocationsCompatibilityEnhancer(LocationsEmspValidator(LocationsEmspService(it))))
        }

    @TestFactory
    fun `invalid Location is compatible`() = listOf(
        TestCase("parking type invalid") { validLocation.copy(parkingType = ParkingType.OTHER) },
        TestCase("facilities contains invalid") { validLocation.copy(facilities = listOf(Facility.OTHER)) },
    ).map { testCase ->
        val (repo, service) = mocks()
        DynamicTest.dynamicTest(testCase.name) {
            expectDoesNotThrow {
                service.putLocation(
                    countryCode = validLocation.countryCode,
                    partyId = validLocation.partyId,
                    locationId = validLocation.id,
                    location = testCase.objectProvider(),
                )
                coVerify(exactly = 1) { repo.putLocation(any(), any(), any(), any()) }

                service.patchLocation(
                    countryCode = validLocation.countryCode,
                    partyId = validLocation.partyId,
                    locationId = validLocation.id,
                    location = testCase.objectProvider().toPartial(),
                )
                coVerify(exactly = 1) { repo.patchLocation(any(), any(), any(), any()) }
            }
        }
    }

    @TestFactory
    fun `EVSE validation tests`() = listOf(
        TestCase("capabilities contains invalid") {
            validEvse.copy(capabilities = listOf(Capability.OTHER))
        },
        TestCase("parkingRestrictions invalid") {
            validEvse.copy(parkingRestrictions = listOf(ParkingRestriction.OTHER))
        },
    ).map { testCase ->
        val (repo, service) = mocks()
        DynamicTest.dynamicTest(testCase.name) {
            expectDoesNotThrow {
                service.putEvse(
                    countryCode = validLocation.countryCode,
                    partyId = validLocation.partyId,
                    locationId = validLocation.id,
                    evseUid = validEvse.uid,
                    evse = testCase.objectProvider(),
                )
                coVerify(exactly = 1) { repo.putEvse(any(), any(), any(), any(), any()) }

                service.patchEvse(
                    countryCode = validLocation.countryCode,
                    partyId = validLocation.partyId,
                    locationId = validLocation.id,
                    evseUid = validEvse.uid,
                    evse = testCase.objectProvider().toPartial(),
                )
                coVerify(exactly = 1) { repo.patchEvse(any(), any(), any(), any(), any()) }
            }
        }
    }

    @TestFactory
    fun `Connector validation tests`() = listOf(
        TestCase("invalid connector type") { validConnector.copy(standard = ConnectorType.OTHER) },
    ).map { testCase ->
        val (repo, service) = mocks()
        DynamicTest.dynamicTest(testCase.name) {
            expectDoesNotThrow {
                service.putConnector(
                    countryCode = validLocation.countryCode,
                    partyId = validLocation.partyId,
                    locationId = validLocation.id,
                    evseUid = validEvse.uid,
                    connectorId = validConnector.id,
                    connector = testCase.objectProvider(),
                )
                coVerify(exactly = 0) {
                    repo.putConnector(any(), any(), any(), any(), any(), any())
                }

                service.patchConnector(
                    countryCode = validLocation.countryCode,
                    partyId = validLocation.partyId,
                    locationId = validLocation.id,
                    evseUid = validEvse.uid,
                    connectorId = validConnector.id,
                    connector = testCase.objectProvider().toPartial(),
                )
                coVerify(exactly = 0) {
                    repo.patchConnector(any(), any(), any(), any(), any(), any())
                }
            }
        }
    }
}
