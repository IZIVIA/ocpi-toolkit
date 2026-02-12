package com.izivia.ocpi.toolkit.common

import com.izivia.ocpi.toolkit.modules.versions.domain.InterfaceRole
import com.izivia.ocpi.toolkit.modules.versions.domain.ModuleID
import com.izivia.ocpi.toolkit.transport.BaseTransportClientBuilder

/** alias for OCPI 2.2.1 specific TransportClientBuilder */
interface TransportClientBuilder : BaseTransportClientBuilder<ModuleID, InterfaceRole>
