package com.izivia.ocpi.toolkit.modules.tokens.services

import com.izivia.ocpi.toolkit.modules.tokens.mock.*
import org.junit.jupiter.api.Test
import strikt.api.expectCatching
import strikt.assertions.isFailure
import strikt.assertions.isSuccess
import kotlin.random.Random

class TokensValidatorTest {
    private fun generateRandomString(length: Int): String = (('a'..'z') + ('A'..'Z'))
        .let { chars ->
            (0 until length)
                .map { chars[Random.nextInt(chars.size)] }
                .joinToString("")
        }

    @Test
    fun `Token validator`() {
        expectCatching {
            validTokenAppUser.validate()
        }.isSuccess()

        expectCatching {
            validTokenFullRfid.validate()
        }.isSuccess()

        expectCatching {
            validTokenFullRfid.copy(countryCode = "test").validate()
        }.isFailure()

        expectCatching {
            validTokenFullRfid.copy(partyId = generateRandomString(4)).validate()
        }.isFailure()

        expectCatching {
            validTokenFullRfid.copy(uid = generateRandomString(37)).validate()
        }.isFailure()

        expectCatching {
            validTokenFullRfid.copy(contractId = generateRandomString(37)).validate()
        }.isFailure()

        expectCatching {
            validTokenFullRfid.copy(visualNumber = generateRandomString(66)).validate()
        }.isFailure()

        expectCatching {
            validTokenFullRfid.copy(issuer = generateRandomString(65)).validate()
        }.isFailure()

        expectCatching {
            validTokenFullRfid.copy(groupId = generateRandomString(37)).validate()
        }.isFailure()

        expectCatching {
            validTokenFullRfid.copy(language = generateRandomString(3)).validate()
        }.isFailure()
    }

    @Test
    fun `Authorization Info validator`() {
        expectCatching {
            validAuthorizationInfo.validate()
        }.isSuccess()

        expectCatching {
            validAuthorizationInfo.copy(authorizationReference = generateRandomString(37)).validate()
        }.isFailure()
    }

    @Test
    fun `Energy contract validator`() {
        expectCatching {
            validEnergyContract.validate()
        }.isSuccess()

        expectCatching {
            validEnergyContract.copy(supplierName = generateRandomString(65)).validate()
        }.isFailure()

        expectCatching {
            validEnergyContract.copy(contractId = generateRandomString(65)).validate()
        }.isFailure()
    }

    @Test
    fun `Location References validator`() {
        expectCatching {
            validLocationReference.validate()
        }.isSuccess()

        expectCatching {
            validLocationReference.copy(locationId = generateRandomString(37)).validate()
        }.isFailure()

        expectCatching {
            validLocationReference.copy(evseUids = listOf(generateRandomString(37))).validate()
        }.isFailure()
    }

    @Test
    fun `DisplayText validator`() {
        expectCatching {
            validDisplayText.validate()
        }.isSuccess()

        expectCatching {
            validDisplayText.copy(language = "fra").validate()
        }.isFailure()

        expectCatching {
            validDisplayText.copy(text = generateRandomString(513)).validate()
        }.isFailure()

        expectCatching {
            validDisplayText.copy(text = "<h>no html!").validate()
        }.isFailure()
    }
}
