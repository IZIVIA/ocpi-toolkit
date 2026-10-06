package com.izivia.ocpi.toolkit211.common

import com.izivia.ocpi.toolkit.transport.domain.HttpResponse
import com.izivia.ocpi.toolkit.transport.domain.HttpStatus
import com.izivia.ocpi.toolkit211.serialization.OcpiSerializationRegistry
import com.izivia.ocpi.toolkit211.serialization.mapper
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.DynamicTest
import org.junit.jupiter.api.TestFactory

class HttpResponseErrorTest {
    @TestFactory
    fun `success responses still validate typed data`(): List<DynamicTest> =
        OcpiSerializationRegistry.serializers.map { serializer ->
            DynamicTest.dynamicTest("${serializer.name}: success data validation") {
                val previous = mapper
                try {
                    mapper = serializer
                    val response = HttpResponse(
                        HttpStatus.OK,
                        """
                        {"status_code":1000,"timestamp":"2026-10-05T10:00:00Z","data":"value"}
                        """.trimIndent(),
                    )
                    assertEquals("value", response.parseResult<String>())
                    val incompatible = response.copy(body = response.body!!.replace("\"value\"", "{}"))
                    assertThrows(OcpiToolkitResponseParsingException::class.java) {
                        incompatible.parseResult<String>()
                    }
                    incompatible.parseResultIgnoringData()
                    assertThrows(OcpiToolkitResponseParsingException::class.java) {
                        HttpResponse(HttpStatus.OK, "{}").parseResultIgnoringData()
                    }
                } finally {
                    mapper = previous
                }
            }
        }

    @TestFactory
    fun `typed responses preserve OCPI errors with diagnostic data`(): List<DynamicTest> =
        OcpiSerializationRegistry.serializers.flatMap { serializer ->
            listOf(HttpStatus.BAD_REQUEST, HttpStatus.OK).flatMap { status ->
                listOf("{\"field\":\"invalid\"}", "[1,2]").flatMap { data ->
                    listOf(false, true).map { list ->
                        DynamicTest.dynamicTest("${serializer.name}: HTTP ${status.code} list=$list data=$data") {
                            val previous = mapper
                            try {
                                mapper = serializer
                                val response = HttpResponse(
                                    status,
                                    """
                                    {
                                      "status_code":2001,
                                      "status_message":"Invalid parameters",
                                      "timestamp":"2026-10-05T10:00:00Z",
                                      "data":$data
                                    }
                                    """.trimIndent(),
                                )
                                val error = assertThrows(OcpiException::class.java) {
                                    if (list) response.parseResultList<String>() else response.parseResult<String>()
                                }
                                assertEquals(2001, error.ocpiStatusCode)
                                assertEquals(status, error.httpStatus)
                                assertEquals("Invalid parameters", error.message)
                            } finally {
                                mapper = previous
                            }
                        }
                    }
                }
            }
        }
}
