package com.izivia.ocpi.toolkit.modules.versions

import com.izivia.ocpi.toolkit.common.OcpiResponseBody
import com.izivia.ocpi.toolkit.modules.versions.domain.VersionDetails

interface VersionDetailsInterface {
    fun getVersionDetails(): OcpiResponseBody<VersionDetails>
}