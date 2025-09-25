package com.izivia.ocpi.toolkit.common

import com.izivia.ocpi.toolkit.serialization.OcpiSerializationRegistry
import com.izivia.ocpi.toolkit.serialization.OcpiSerializer

interface TestWithSerializerProviders {
    companion object {
        @JvmStatic
        fun getAvailableOcpiSerializers(): List<OcpiSerializer> {
            return OcpiSerializationRegistry.serializers
        }
    }
}
