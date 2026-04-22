package com.izivia.ocpi.toolkit211.modules.credentials

import com.izivia.ocpi.toolkit211.modules.credentials.domain.Credentials

interface CredentialsInterface {
    suspend fun get(token: String): Credentials

    suspend fun post(
        token: String,
        credentials: Credentials,
        debugHeaders: Map<String, String>,
    ): Credentials

    suspend fun put(
        token: String,
        credentials: Credentials,
        debugHeaders: Map<String, String>,
    ): Credentials

    suspend fun delete(token: String)
}
