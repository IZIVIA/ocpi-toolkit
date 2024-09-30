package com.izivia.ocpi.toolkit.samples.common

import com.izivia.ocpi.toolkit.modules.credentials.domain.CredentialRole
import com.izivia.ocpi.toolkit.modules.versions.domain.Endpoint
import com.izivia.ocpi.toolkit.modules.versions.domain.Version

data class Partner(
    val url: String? = null,
    val credentialsRoles: List<CredentialRole> = emptyList(),
    val version: Version? = null,
    val endpoints: List<Endpoint>? = null,
    val tokenA: String? = null,
    val clientToken: String? = null,
    val serverToken: String? = null,
)
