package com.izivia.ocpi.toolkit.modules.tokens.mock

import com.izivia.ocpi.toolkit.modules.tokens.domain.Token
import com.izivia.ocpi.toolkit.modules.tokens.domain.TokenPartial
import com.izivia.ocpi.toolkit.modules.tokens.domain.TokenType
import com.izivia.ocpi.toolkit.modules.tokens.repositories.TokensCpoRepository
import io.mockk.coEvery
import io.mockk.mockk
import io.mockk.slot

fun tokensCpoRepositoryTest(responseToken: Token): TokensCpoRepository = mockk {
    val country_code = slot<String>()
    val party_id = slot<String>()
    val token_uid = slot<String>()
    val type = mutableListOf<TokenType?>()
    val requestToken = slot<Token>()
    val tokenPartial = slot<TokenPartial>()

    coEvery {
        getToken(
            capture(country_code),
            capture(party_id),
            capture(token_uid),
            captureNullable(type),
        )
    } coAnswers { responseToken }

    coEvery {
        putToken(
            token = capture(requestToken),
            countryCode = capture(country_code),
            partyId = capture(party_id),
            tokenUid = capture(token_uid),
            type = captureNullable(type),
        )
    } coAnswers { responseToken }

    coEvery {
        patchToken(
            token = capture(tokenPartial),
            countryCode = capture(country_code),
            partyId = capture(party_id),
            tokenUid = capture(token_uid),
            type = captureNullable(type),
        )
    } coAnswers { responseToken }
}
