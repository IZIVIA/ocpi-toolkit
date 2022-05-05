package ocpi.credentials.repositories

import ocpi.credentials.domain.CredentialRole

interface CredentialsRoleRepository {
    fun getCredentialsRoles(): List<CredentialRole>
}