package com.izivia.ocpi.toolkit.modules.tokens.services

import com.izivia.ocpi.toolkit.common.OcpiStatus
import com.izivia.ocpi.toolkit.modules.tokens.domain.TokenType
import com.izivia.ocpi.toolkit.modules.tokens.domain.toPartial
import com.izivia.ocpi.toolkit.modules.tokens.mock.tokensCpoRepositoryTest
import com.izivia.ocpi.toolkit.modules.tokens.mock.validTokenFullRfid
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Test
import strikt.api.expectThat
import strikt.assertions.isEqualTo

class TokensCpoServiceTest {
    private lateinit var service: TokensCpoService

    private val str1char = "a"
    private val str2chars = "ab"
    private val str3chars = "abc"
    private val str4chars = "abcd"
    private val str36chars = "abababababababababababababababababab"
    private val str37chars = "ababababababababababababababababababa"
    private val str40chars = "abababababababababababababababababababab"

    @Test
    fun getTokenParamsValidationTest() {
        service = TokensCpoService(service = tokensCpoRepositoryTest(validTokenFullRfid))

        expectThat(
            runBlocking { service.getToken(countryCode = str2chars, partyId = str3chars, tokenUid = str36chars) },
        ) {
            get { statusCode }.isEqualTo(OcpiStatus.SUCCESS.code)
        }

        expectThat(
            runBlocking {
                service.getToken(
                    countryCode = str2chars,
                    partyId = str3chars,
                    tokenUid = str36chars,
                    type = TokenType.RFID,
                )
            },
        ) {
            get { statusCode }.isEqualTo(OcpiStatus.SUCCESS.code)
        }

        expectThat(
            runBlocking {
                service.getToken(
                    countryCode = str2chars,
                    partyId = str3chars,
                    tokenUid = str37chars,
                    type = TokenType.RFID,
                )
            },
        ) {
            get { statusCode }.isEqualTo(OcpiStatus.CLIENT_INVALID_PARAMETERS.code)
        }
        expectThat(
            runBlocking {
                service.getToken(
                    countryCode = str3chars,
                    partyId = str3chars,
                    tokenUid = str36chars,
                    type = TokenType.RFID,
                )
            },
        ) {
            get { statusCode }.isEqualTo(OcpiStatus.CLIENT_INVALID_PARAMETERS.code)
        }
        expectThat(
            runBlocking {
                service.getToken(
                    countryCode = str2chars,
                    partyId = str4chars,
                    tokenUid = str36chars,
                    type = TokenType.RFID,
                )
            },
        ) {
            get { statusCode }.isEqualTo(OcpiStatus.CLIENT_INVALID_PARAMETERS.code)
        }
    }

    @Test
    fun putTokenParamsValidationTest() {
        service = TokensCpoService(service = tokensCpoRepositoryTest(validTokenFullRfid))

        expectThat(
            runBlocking {
                service.putToken(
                    token = validTokenFullRfid,
                    countryCode = validTokenFullRfid.countryCode,
                    partyId = validTokenFullRfid.partyId,
                    tokenUid = validTokenFullRfid.uid,
                )
            },
        ) {
            get { statusCode }.isEqualTo(OcpiStatus.SUCCESS.code)
        }

        expectThat(
            runBlocking {
                service.putToken(
                    token = validTokenFullRfid,
                    countryCode = validTokenFullRfid.countryCode,
                    partyId = validTokenFullRfid.partyId,
                    tokenUid = validTokenFullRfid.uid,
                    type = TokenType.RFID,
                )
            },
        ) {
            get { statusCode }.isEqualTo(OcpiStatus.SUCCESS.code)
        }

        expectThat(
            runBlocking {
                service.putToken(
                    token = validTokenFullRfid,
                    countryCode = str3chars,
                    partyId = str3chars,
                    tokenUid = str36chars,
                    type = TokenType.RFID,
                )
            },
        ) {
            get { statusCode }.isEqualTo(OcpiStatus.CLIENT_INVALID_PARAMETERS.code)
        }

        expectThat(
            runBlocking {
                service.putToken(
                    token = validTokenFullRfid,
                    countryCode = str2chars,
                    partyId = str4chars,
                    tokenUid = str36chars,
                    type = TokenType.RFID,
                )
            },
        ) {
            get { statusCode }.isEqualTo(OcpiStatus.CLIENT_INVALID_PARAMETERS.code)
        }

        expectThat(
            runBlocking {
                service.putToken(
                    token = validTokenFullRfid,
                    countryCode = str2chars,
                    partyId = str3chars,
                    tokenUid = str40chars,
                    type = TokenType.RFID,
                )
            },
        ) {
            get { statusCode }.isEqualTo(OcpiStatus.CLIENT_INVALID_PARAMETERS.code)
        }
    }

    @Test
    fun patchTokenParamsValidationTest() {
        service = TokensCpoService(service = tokensCpoRepositoryTest(validTokenFullRfid))

        expectThat(
            runBlocking {
                service.patchToken(
                    token = validTokenFullRfid.toPartial(),
                    countryCode = str2chars,
                    partyId = str3chars,
                    tokenUid = str4chars,
                )
            },
        ) {
            get { statusCode }.isEqualTo(OcpiStatus.SUCCESS.code)
        }

        expectThat(
            runBlocking {
                service.patchToken(
                    token = validTokenFullRfid.toPartial(),
                    countryCode = str2chars,
                    partyId = str3chars,
                    tokenUid = str36chars,
                    type = TokenType.RFID,
                )
            },
        ) {
            get { statusCode }.isEqualTo(OcpiStatus.SUCCESS.code)
        }

        expectThat(
            runBlocking {
                service.patchToken(
                    token = validTokenFullRfid.toPartial(),
                    countryCode = str3chars,
                    partyId = str3chars,
                    tokenUid = str36chars,
                    type = TokenType.RFID,
                )
            },
        ) {
            get { statusCode }.isEqualTo(OcpiStatus.CLIENT_INVALID_PARAMETERS.code)
        }

        expectThat(
            runBlocking {
                service.patchToken(
                    token = validTokenFullRfid.toPartial(),
                    countryCode = str2chars,
                    partyId = str4chars,
                    tokenUid = str36chars,
                    type = TokenType.RFID,
                )
            },
        ) {
            get { statusCode }.isEqualTo(OcpiStatus.CLIENT_INVALID_PARAMETERS.code)
        }

        expectThat(
            runBlocking {
                service.patchToken(
                    token = validTokenFullRfid.toPartial(),
                    countryCode = str2chars,
                    partyId = str3chars,
                    tokenUid = str40chars,
                    type = TokenType.RFID,
                )
            },
        ) {
            get { statusCode }.isEqualTo(OcpiStatus.CLIENT_INVALID_PARAMETERS.code)
        }
    }
}
