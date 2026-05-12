package com.izivia.ocpi.toolkit211.modules.tokens.services

import com.izivia.ocpi.toolkit211.modules.tokens.TokensCpoInterface
import com.izivia.ocpi.toolkit211.modules.tokens.repositories.TokensCpoRepository

open class TokensCpoService(
    repository: TokensCpoRepository,
) : TokensCpoInterface by repository
