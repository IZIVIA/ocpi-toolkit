package com.izivia.ocpi.toolkit.tests.integration.mock

import com.izivia.ocpi.toolkit.common.SearchResult
import com.izivia.ocpi.toolkit.common.toSearchResult
import com.izivia.ocpi.toolkit.modules.tokens.domain.AuthorizationInfo
import com.izivia.ocpi.toolkit.modules.tokens.domain.LocationReferences
import com.izivia.ocpi.toolkit.modules.tokens.domain.Token
import com.izivia.ocpi.toolkit.modules.tokens.domain.TokenType
import com.izivia.ocpi.toolkit.modules.tokens.services.TokensEmspService
import com.mongodb.client.MongoCollection
import org.litote.kmongo.and
import org.litote.kmongo.gte
import org.litote.kmongo.lte
import java.time.Instant

class TokensEmspMongoRepository(
    private val collection: MongoCollection<Token>
) : TokensEmspService {

    override fun getTokens(dateFrom: Instant?, dateTo: Instant?, offset: Int, limit: Int?): SearchResult<Token> =
        collection
            .run {
                find(
                    and(
                        dateFrom?.let { Token::last_updated gte dateFrom },
                        dateTo?.let { Token::last_updated lte dateTo }
                    )
                )
            }
            .toList()
            .let {
                val actualLimit = limit ?: 10
                val size = it.size

                it
                    .filterIndexed { index: Int, _: Token -> index + 1 > offset }
                    .take(actualLimit)
                    .toSearchResult(totalCount = size, limit = actualLimit, offset = offset)
            }

    override fun postToken(
        tokenUid: String,
        tokenType: TokenType,
        locationReferences: LocationReferences?
    ): AuthorizationInfo {
        TODO("Not yet implemented")
    }
}
