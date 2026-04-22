package com.izivia.ocpi.toolkit211.common

import com.izivia.ocpi.toolkit.transport.TransportServer
import com.izivia.ocpi.toolkit211.modules.versions.domain.Endpoint
import com.izivia.ocpi.toolkit211.modules.versions.domain.ModuleID
import com.izivia.ocpi.toolkit211.modules.versions.domain.VersionNumber
import com.izivia.ocpi.toolkit211.modules.versions.repositories.MutableVersionsRepository

abstract class OcpiSelfRegisteringModuleServer(
    private val ocpiVersion: VersionNumber,
    private val moduleID: ModuleID,
    private val versionsRepository: MutableVersionsRepository? = null,
    basePathOverride: String? = null,
) : OcpiModuleServer(basePathOverride ?: "/${ocpiVersion.value}/${moduleID.name}") {

    protected abstract suspend fun doRegisterOn(transportServer: TransportServer)

    override suspend fun registerOn(transportServer: TransportServer) {
        versionsRepository?.addEndpoint(
            ocpiVersion,
            Endpoint(
                identifier = moduleID,
                url = "${transportServer.baseUrl()}$basePath",
            ),
        )
        doRegisterOn(transportServer)
    }
}
