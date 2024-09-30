package com.izivia.ocpi.toolkit.common

import com.izivia.ocpi.toolkit.modules.versions.domain.Endpoint
import com.izivia.ocpi.toolkit.modules.versions.domain.InterfaceRole
import com.izivia.ocpi.toolkit.modules.versions.domain.ModuleID
import com.izivia.ocpi.toolkit.modules.versions.domain.VersionNumber
import com.izivia.ocpi.toolkit.modules.versions.repositories.MutableVersionsRepository
import com.izivia.ocpi.toolkit.transport.TransportServer

abstract class OcpiSelfRegisteringModuleServer(
    private val ocpiVersion: VersionNumber,
    private val moduleID: ModuleID,
    private val interfaceRole: InterfaceRole,
    private val versionsRepository: MutableVersionsRepository? = null,
    basePathOverride: String? = null,
) : OcpiModuleServer(basePathOverride ?: "/${ocpiVersion.value}/${moduleID.name}") {

    protected abstract suspend fun doRegisterOn(transportServer: TransportServer)

    override suspend fun registerOn(transportServer: TransportServer) {
        versionsRepository?.addEndpoint(
            ocpiVersion,
            Endpoint(
                identifier = moduleID,
                role = interfaceRole,
                url = "${transportServer.baseUrl()}$basePath",
            ),
        )
        doRegisterOn(transportServer)
    }
}
