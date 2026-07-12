package com.izivia.ocpi.toolkit211.modules.credentials.repositories

import com.izivia.ocpi.toolkit211.modules.credentials.domain.CredentialsDetails

interface CredentialsDetailsRepository {
    suspend fun getCredentialsDetails(partnerId: String): CredentialsDetails
}
