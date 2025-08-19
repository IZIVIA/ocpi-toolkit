package com.izivia.ocpi.toolkit.modules.tokens.services

import com.izivia.ocpi.toolkit.common.OcpiClientInvalidParametersException
import com.izivia.ocpi.toolkit.modules.tokens.domain.TokenType
import com.izivia.ocpi.toolkit.modules.tokens.domain.toPartial
import com.izivia.ocpi.toolkit.modules.tokens.mock.tokensCpoRepositoryTest
import com.izivia.ocpi.toolkit.modules.tokens.mock.validTokenFullRfid
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Test
import strikt.api.expectDoesNotThrow
import strikt.api.expectThrows

class TokensCpoServiceTest {
    private val str1char = "a"
    private val str2chars = "ab"
    private val str3chars = "abc"
    private val str4chars = "abcd"
    private val str36chars = "abababababababababababababababababab"
    private val str37chars = "ababababababababababababababababababa"
    private val str40chars = "abababababababababababababababababababab"

    @Test
    fun getTokenParamsValidationTest() {
        val service = TokensCpoValidator(TokensCpoService(tokensCpoRepositoryTest(validTokenFullRfid)))

        expectDoesNotThrow {
            runBlocking { service.getToken(countryCode = str2chars, partyId = str3chars, tokenUid = str36chars) }
        }

        expectDoesNotThrow {
            runBlocking {
                service.getToken(
                    countryCode = str2chars,
                    partyId = str3chars,
                    tokenUid = str36chars,
                    type = TokenType.RFID,
                )
            }
        }

        expectThrows<OcpiClientInvalidParametersException> {
            runBlocking {
                service.getToken(
                    countryCode = str2chars,
                    partyId = str3chars,
                    tokenUid = str37chars,
                    type = TokenType.RFID,
                )
            }
        }

        expectThrows<OcpiClientInvalidParametersException> {
            runBlocking {
                service.getToken(
                    countryCode = str3chars,
                    partyId = str3chars,
                    tokenUid = str36chars,
                    type = TokenType.RFID,
                )
            }
        }

        expectThrows<OcpiClientInvalidParametersException> {
            runBlocking {
                service.getToken(
                    countryCode = str2chars,
                    partyId = str4chars,
                    tokenUid = str36chars,
                    type = TokenType.RFID,
                )
            }
        }
    }

    @Test
    fun putTokenParamsValidationTest() {
        val service = TokensCpoValidator(TokensCpoService(tokensCpoRepositoryTest(validTokenFullRfid)))

        expectDoesNotThrow {
            runBlocking {
                service.putToken(
                    countryCode = validTokenFullRfid.countryCode,
                    partyId = validTokenFullRfid.partyId,
                    tokenUid = validTokenFullRfid.uid,
                    token = validTokenFullRfid,
                )
            }
        }

        expectDoesNotThrow {
            runBlocking {
                service.putToken(
                    countryCode = validTokenFullRfid.countryCode,
                    partyId = validTokenFullRfid.partyId,
                    tokenUid = validTokenFullRfid.uid,
                    type = TokenType.RFID,
                    token = validTokenFullRfid,
                )
            }
        }

        expectThrows<OcpiClientInvalidParametersException> {
            runBlocking {
                service.putToken(
                    countryCode = str3chars,
                    partyId = str3chars,
                    tokenUid = str36chars,
                    type = TokenType.RFID,
                    token = validTokenFullRfid,
                )
            }
        }

        expectThrows<OcpiClientInvalidParametersException> {
            runBlocking {
                service.putToken(
                    countryCode = str2chars,
                    partyId = str4chars,
                    tokenUid = str36chars,
                    type = TokenType.RFID,
                    token = validTokenFullRfid,
                )
            }
        }

        expectThrows<OcpiClientInvalidParametersException> {
            runBlocking {
                service.putToken(
                    countryCode = str2chars,
                    partyId = str3chars,
                    tokenUid = str40chars,
                    type = TokenType.RFID,
                    token = validTokenFullRfid,
                )
            }
        }
    }

    @Test
    fun patchTokenParamsValidationTest() {
        val service = TokensCpoValidator(TokensCpoService(tokensCpoRepositoryTest(validTokenFullRfid)))

        expectDoesNotThrow {
            runBlocking {
                service.patchToken(
                    countryCode = str2chars,
                    partyId = str3chars,
                    tokenUid = str4chars,
                    token = validTokenFullRfid.toPartial(),
                )
            }
        }

        expectDoesNotThrow {
            runBlocking {
                service.patchToken(
                    countryCode = str2chars,
                    partyId = str3chars,
                    tokenUid = str36chars,
                    type = TokenType.RFID,
                    token = validTokenFullRfid.toPartial(),
                )
            }
        }

        expectThrows<OcpiClientInvalidParametersException> {
            runBlocking {
                service.patchToken(
                    countryCode = str3chars,
                    partyId = str3chars,
                    tokenUid = str36chars,
                    type = TokenType.RFID,
                    token = validTokenFullRfid.toPartial(),
                )
            }
        }

        expectThrows<OcpiClientInvalidParametersException> {
            runBlocking {
                service.patchToken(
                    countryCode = str2chars,
                    partyId = str4chars,
                    tokenUid = str36chars,
                    type = TokenType.RFID,
                    token = validTokenFullRfid.toPartial(),
                )
            }
        }

        expectThrows<OcpiClientInvalidParametersException> {
            runBlocking {
                service.patchToken(
                    countryCode = str2chars,
                    partyId = str3chars,
                    tokenUid = str40chars,
                    type = TokenType.RFID,
                    token = validTokenFullRfid.toPartial(),
                )
            }
        }
    }
}
