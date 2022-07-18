package com.izivia.ocpi.toolkit.tests.mock

import com.izivia.ocpi.toolkit.modules.tokens.domain.Token
import com.izivia.ocpi.toolkit.modules.tokens.domain.TokenPartial
import com.izivia.ocpi.toolkit.modules.tokens.services.TokensCpoService
import io.mockk.every
import io.mockk.mockk
import io.mockk.slot

fun tokensCpoService(tokens: MutableList<Token>): TokensCpoService = mockk {

    val countryCode = slot<String>()
    val partyId = slot<String>()
    val tokenUid = slot<String>()
    val token = slot<Token>()
    val tokenPartial = slot<TokenPartial>()

    every {
        getToken(
            capture(countryCode),
            capture(partyId),
            capture(tokenUid)
        )
    } answers {
        tokens.find {
            it.uid == tokenUid.captured
        }
    }

    every {
        putToken(
            capture(countryCode),
            capture(partyId),
            capture(tokenUid),
            capture(token)
        )
    } answers {
        tokens.add(token.captured)
        tokens.find { it.uid == token.captured.uid }!!
    }

    every {
        patchToken(
            capture(countryCode),
            capture(partyId),
            capture(tokenUid),
            capture(tokenPartial)
        )
    } answers {
        tokens
            .indexOfFirst { it.uid == tokenUid.captured }
            .let { index ->
                val existingToken = tokens[index]
                existingToken.copy(
                    uid = tokenPartial.captured.uid ?: existingToken.uid,
                    type = tokenPartial.captured.type ?: existingToken.type,
                    auth_id = tokenPartial.captured.auth_id ?: existingToken.auth_id,
                    visual_number = tokenPartial.captured.visual_number ?: existingToken.visual_number,
                    issuer = tokenPartial.captured.issuer ?: existingToken.issuer,
                    valid = tokenPartial.captured.valid ?: existingToken.valid,
                    whitelist = tokenPartial.captured.whitelist ?: existingToken.whitelist,
                    language = tokenPartial.captured.language ?: existingToken.language,
                    last_updated = tokenPartial.captured.last_updated ?: existingToken.last_updated,
                ).also {
                    tokens[index] = it
                }
            }
    }
}
