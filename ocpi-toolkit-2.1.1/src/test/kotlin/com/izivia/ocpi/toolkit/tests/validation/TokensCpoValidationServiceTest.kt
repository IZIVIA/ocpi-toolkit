package com.izivia.ocpi.toolkit.tests.validation

import com.izivia.ocpi.toolkit.common.OcpiStatus
import com.izivia.ocpi.toolkit.modules.tokens.domain.toPartial
import com.izivia.ocpi.toolkit.modules.tokens.validation.TokensCpoValidationService
import com.izivia.ocpi.toolkit.samples.common.validToken
import com.izivia.ocpi.toolkit.tests.mock.tokensCpoService
import org.junit.jupiter.api.Test
import strikt.api.expectThat
import strikt.assertions.isEqualTo

class TokensCpoValidationServiceTest {
    private lateinit var service: TokensCpoValidationService
    private val str40chars = "abababababababababababababababababababab"

    @Test
    fun getTokenParamsValidationTest() {
        service = TokensCpoValidationService(service = tokensCpoService(mutableListOf()))

        expectThat(
            service.getToken(
                countryCode = "FR",
                partyId = "ABC",
                tokenUid = validToken.uid
            )
        ) {
            get { status_code }
                .isEqualTo(OcpiStatus.SUCCESS.code)
        }

        expectThat(
            service.getToken(
                countryCode = "FRx",
                partyId = "ABC",
                tokenUid = validToken.uid
            )
        ) {
            get { status_code }
                .isEqualTo(OcpiStatus.CLIENT_INVALID_PARAMETERS.code)
        }

        expectThat(
            service.getToken(
                countryCode = "FR",
                partyId = "ABCx",
                tokenUid = validToken.uid
            )
        ) {
            get { status_code }
                .isEqualTo(OcpiStatus.CLIENT_INVALID_PARAMETERS.code)
        }

        expectThat(
            service.getToken(
                countryCode = "FR",
                partyId = "ABC",
                tokenUid = str40chars
            )
        ) {
            get { status_code }
                .isEqualTo(OcpiStatus.CLIENT_INVALID_PARAMETERS.code)
        }
    }

    @Test
    fun putTokenParamsValidationTest() {
        service = TokensCpoValidationService(service = tokensCpoService(mutableListOf()))

        expectThat(
            service.putToken(
                countryCode = "FR",
                partyId = "ABC",
                tokenUid = validToken.uid,
                token = validToken
            )
        ) {
            get { status_code }
                .isEqualTo(OcpiStatus.SUCCESS.code)
        }

        expectThat(
            service.putToken(
                countryCode = "FRx",
                partyId = "ABC",
                tokenUid = validToken.uid,
                token = validToken
            )
        ) {
            get { status_code }
                .isEqualTo(OcpiStatus.CLIENT_INVALID_PARAMETERS.code)
        }

        expectThat(
            service.putToken(
                countryCode = "FR",
                partyId = "ABCx",
                tokenUid = validToken.uid,
                token = validToken
            )
        ) {
            get { status_code }
                .isEqualTo(OcpiStatus.CLIENT_INVALID_PARAMETERS.code)
        }

        expectThat(
            service.putToken(
                countryCode = "FR",
                partyId = "ABC",
                tokenUid = str40chars,
                token = validToken
            )
        ) {
            get { status_code }
                .isEqualTo(OcpiStatus.CLIENT_INVALID_PARAMETERS.code)
        }
    }

    @Test
    fun patchTokenParamsValidationTest() {
        service = TokensCpoValidationService(service = tokensCpoService(mutableListOf(validToken)))

        expectThat(
            service.patchToken(
                countryCode = "FR",
                partyId = "ABC",
                tokenUid = validToken.uid,
                token = validToken.toPartial()
            )
        ) {
            get { status_code }
                .isEqualTo(OcpiStatus.SUCCESS.code)
        }

        expectThat(
            service.patchToken(
                countryCode = "FRx",
                partyId = "ABC",
                tokenUid = validToken.uid,
                token = validToken.toPartial()
            )
        ) {
            get { status_code }
                .isEqualTo(OcpiStatus.CLIENT_INVALID_PARAMETERS.code)
        }

        expectThat(
            service.patchToken(
                countryCode = "FR",
                partyId = "ABCx",
                tokenUid = validToken.uid,
                token = validToken.toPartial()
            )
        ) {
            get { status_code }
                .isEqualTo(OcpiStatus.CLIENT_INVALID_PARAMETERS.code)
        }

        expectThat(
            service.patchToken(
                countryCode = "FR",
                partyId = "ABC",
                tokenUid = str40chars,
                token = validToken.toPartial()
            )
        ) {
            get { status_code }
                .isEqualTo(OcpiStatus.CLIENT_INVALID_PARAMETERS.code)
        }
    }
}
