package com.izivia.ocpi.toolkit.modules.credentials.repositories

import com.izivia.ocpi.toolkit.modules.credentials.domain.CredentialRole

interface CredentialsRoleRepository {
    suspend fun getCredentialsRoles(partnerId: String): List<CredentialRole>
}
