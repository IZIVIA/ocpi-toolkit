package com.izivia.ocpi.toolkit.tests.validation

import com.izivia.ocpi.toolkit.modules.tokens.validation.validate
import com.izivia.ocpi.toolkit.samples.common.validToken
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
            validToken.validate()
        }.isSuccess()

        expectCatching {
            validToken.copy(uid = generateRandomString(37)).validate()
        }.isFailure()

        expectCatching {
            validToken.copy(auth_id = generateRandomString(37)).validate()
        }.isFailure()

        expectCatching {
            validToken.copy(visual_number = generateRandomString(65)).validate()
        }.isFailure()

        expectCatching {
            validToken.copy(issuer = generateRandomString(65)).validate()
        }.isFailure()
    }
}
