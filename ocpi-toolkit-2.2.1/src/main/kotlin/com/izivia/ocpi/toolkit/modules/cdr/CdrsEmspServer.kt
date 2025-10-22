package com.izivia.ocpi.toolkit.modules.cdr

import com.izivia.ocpi.toolkit.common.*
import com.izivia.ocpi.toolkit.modules.cdr.domain.Cdr
import com.izivia.ocpi.toolkit.modules.versions.domain.InterfaceRole
import com.izivia.ocpi.toolkit.modules.versions.domain.ModuleID
import com.izivia.ocpi.toolkit.modules.versions.domain.VersionNumber
import com.izivia.ocpi.toolkit.modules.versions.repositories.MutableVersionsRepository
import com.izivia.ocpi.toolkit.serialization.mapper
import com.izivia.ocpi.toolkit.transport.TransportServer
import com.izivia.ocpi.toolkit.transport.domain.HttpMethod
import com.izivia.ocpi.toolkit.transport.domain.VariablePathSegment
import java.time.Instant

class CdrsEmspServer(
    private val service: CdrsEmspInterface<String>,
    private val timeProvider: TimeProvider = TimeProvider { Instant.now() },
    versionsRepository: MutableVersionsRepository? = null,
    basePathOverride: String? = null,
) : OcpiSelfRegisteringModuleServer(
    ocpiVersion = VersionNumber.V2_2_1,
    moduleID = ModuleID.cdrs,
    interfaceRole = InterfaceRole.RECEIVER,
    versionsRepository = versionsRepository,
    basePathOverride = basePathOverride,
) {
    override suspend fun doRegisterOn(transportServer: TransportServer) {
        transportServer.handle(
            method = HttpMethod.GET,
            path = basePathSegments + listOf(VariablePathSegment("cdrId")),
        ) { req ->
            req.respondObject(timeProvider.now()) {
                service.getCdr(param = req.pathParam("cdrId"))
            }
        }

        transportServer.handle(
            method = HttpMethod.POST,
            path = basePathSegments,
        ) { req ->
            val url = runCatching {
                service
                    .postCdr(
                        cdr = mapper.deserializeObject(req.body, Cdr::class.java),
                    )
            }
            req.respondNothing(timeProvider.now()) { url.getOrThrow() }
                .withHeaderMixin(Header.LOCATION, url.getOrNull() ?: "")
        }
    }
}
