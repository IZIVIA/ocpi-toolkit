package com.izivia.ocpi.toolkit211.common

import com.izivia.ocpi.toolkit211.serialization.OcpiSerializationRegistry
import com.izivia.ocpi.toolkit211.serialization.OcpiSerializer

interface TestWithSerializerProviders {
    companion object {
        @JvmStatic
        fun getAvailableOcpiSerializers(): List<OcpiSerializer> {
            return OcpiSerializationRegistry.serializers
        }
    }
}
