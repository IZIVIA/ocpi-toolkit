package com.izivia.ocpi.toolkit211.modules.cdr

import com.izivia.ocpi.toolkit.transport.TransportServer
import com.izivia.ocpi.toolkit.transport.domain.HttpMethod
import com.izivia.ocpi.toolkit.transport.domain.VariablePathSegment
import com.izivia.ocpi.toolkit211.common.*
import com.izivia.ocpi.toolkit211.modules.cdr.domain.Cdr
import com.izivia.ocpi.toolkit211.modules.versions.domain.ModuleID
import com.izivia.ocpi.toolkit211.modules.versions.domain.VersionNumber
import com.izivia.ocpi.toolkit211.modules.versions.repositories.MutableVersionsRepository
import com.izivia.ocpi.toolkit211.serialization.mapper
import java.time.Instant

class CdrsEmspServer(
    private val service: CdrsEmspInterface<String>,
    private val timeProvider: TimeProvider = TimeProvider { Instant.now() },
    versionsRepository: MutableVersionsRepository? = null,
    basePathOverride: String? = null,
) : OcpiSelfRegisteringModuleServer(
    ocpiVersion = VersionNumber.V2_1_1,
    moduleID = ModuleID.cdrs,
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
                service.postCdr(cdr = mapper.deserializeObject(req.body, Cdr::class.java))
            }
            req.respondNothing(timeProvider.now()) { url.getOrThrow() }
                .withHeaderMixin(Header.LOCATION, url.getOrNull() ?: "")
        }
    }
}
