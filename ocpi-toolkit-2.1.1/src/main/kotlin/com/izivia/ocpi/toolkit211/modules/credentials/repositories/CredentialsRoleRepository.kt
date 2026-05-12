package com.izivia.ocpi.toolkit211.modules.credentials.repositories

import com.izivia.ocpi.toolkit211.modules.credentials.domain.CredentialRole

interface CredentialsRoleRepository {
    suspend fun getCredentialsRoles(partnerId: String): List<CredentialRole>
}
