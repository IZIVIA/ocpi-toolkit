package com.izivia.ocpi.toolkit.common

import com.izivia.ocpi.toolkit.modules.credentials.domain.Credentials
import com.izivia.ocpi.toolkit.serialization.OcpiSerializationRegistry
import com.izivia.ocpi.toolkit.serialization.OcpiSerializer
import com.izivia.ocpi.toolkit.serialization.mapper
import com.izivia.ocpi.toolkit.transport.domain.HttpMethod
import com.izivia.ocpi.toolkit.transport.domain.HttpRequest
import com.izivia.ocpi.toolkit.transport.domain.HttpResponse
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.DynamicTest
import org.junit.jupiter.api.TestFactory
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.MethodSource
import strikt.api.expectThat
import strikt.assertions.isEqualTo
import strikt.assertions.isNull
import java.time.Instant

class HttpRequestTest : TestWithSerializerProviders {
    private val fixedTimestamp = Instant.parse("2024-01-15T10:30:00Z")

    @ParameterizedTest
    @MethodSource("getAvailableOcpiSerializers")
    fun `consistent response for OcpiException`(serializer: OcpiSerializer) {
        mapper = serializer

        val wantJson = """
            {"status_code":2001,"status_message":"Invalid or missing parameters","timestamp":"2024-01-15T10:30:00Z"}
        """.trimIndent()
        val assert = { res: HttpResponse ->
            expectThat(res) {
                get { status.code }.isEqualTo(400)
                get { body }.isEqualTo(wantJson)
            }
        }

        assert(
            runBlocking {
                HttpRequest(HttpMethod.GET).respondSearchResult<String>(fixedTimestamp) {
                    throw OcpiClientInvalidParametersException()
                }
            },
        )

        assert(
            runBlocking {
                HttpRequest(HttpMethod.GET).respondObject<String>(fixedTimestamp) {
                    throw OcpiClientInvalidParametersException()
                }
            },
        )

        assert(
            runBlocking {
                HttpRequest(HttpMethod.GET).respondNothing(fixedTimestamp) {
                    throw OcpiClientInvalidParametersException()
                }
            },
        )
    }

    @ParameterizedTest
    @MethodSource("getAvailableOcpiSerializers")
    fun `consistently respond to Exception`(serializer: OcpiSerializer) {
        mapper = serializer

        val wantJson =
            """{"status_code":3000,"status_message":"Generic server error","timestamp":"2024-01-15T10:30:00Z"}"""
        val assert = { res: HttpResponse ->
            expectThat(res) {
                get { status.code }.isEqualTo(200)
                get { body }.isEqualTo(wantJson)
            }
        }

        assert(
            runBlocking {
                HttpRequest(HttpMethod.GET).respondSearchResult<String>(fixedTimestamp) {
                    throw RuntimeException()
                }
            },
        )

        assert(
            runBlocking {
                HttpRequest(HttpMethod.GET).respondObject<String>(fixedTimestamp) {
                    throw RuntimeException()
                }
            },
        )

        assert(
            runBlocking {
                HttpRequest(HttpMethod.GET).respondNothing(fixedTimestamp) {
                    throw RuntimeException()
                }
            },
        )
    }

    @TestFactory
    fun `respond search result`() = listOf(
        testCase(
            name = "null",
            data = null, // causing NPE
            wantJson = """
                |{"status_code":3000,"status_message":"Generic server error","timestamp":"2024-01-15T10:30:00Z"}
            """,
        ),
        testCase(
            name = "empty List<String>",
            data = emptyList<String>(),
            wantJson = """{
                |"data":[],
                |"status_code":1000,"status_message":"Success","timestamp":"2024-01-15T10:30:00Z"}
            """,
        ),
        testCase(
            name = "list with single string",
            data = listOf("single-item"),
            wantJson = """{
                |"data":["single-item"],
                |"status_code":1000,"status_message":"Success","timestamp":"2024-01-15T10:30:00Z"}
            """,
        ),
        testCase(
            name = "list with multiple strings",
            data = listOf("first", "second", "third"),
            wantJson = """{
                |"data":["first","second","third"],
                |"status_code":1000,"status_message":"Success","timestamp":"2024-01-15T10:30:00Z"}
            """,
        ),
    ).flatten().map { testCase ->
        DynamicTest.dynamicTest(testCase.name) {
            mapper = testCase.serializer
            val res = runBlocking {
                HttpRequest(HttpMethod.GET).respondSearchResult<String>(fixedTimestamp) {
                    SearchResult(testCase.data!!, testCase.data.size, 10, 0, null)
                }
            }
            expectThat(res) {
                get { status.code }.isEqualTo(testCase.wantHttpStatus)
                get { body }.and {
                    if (testCase.wantJson == null) {
                        isNull()
                    } else {
                        isEqualTo(testCase.wantJson)
                    }
                }
            }
        }
    }

    @TestFactory
    fun `encodes object results`() = listOf(
        testCase(
            name = "null data",
            data = null, // converted into NotFound
            wantJson = """{"status_code":2000,"status_message":"Not Found","timestamp":"2024-01-15T10:30:00Z"}""",
            wantHttpStatus = 404,
        ),
        testCase(
            name = "string",
            data = "test-string",
            wantJson = """{
                |"data":"test-string",
                |"status_code":1000,"status_message":"Success","timestamp":"2024-01-15T10:30:00Z"}
            """,
        ),
    ).flatten().map { testCase ->
        DynamicTest.dynamicTest(testCase.name) {
            mapper = testCase.serializer
            val res = runBlocking {
                HttpRequest(HttpMethod.GET).respondObject(fixedTimestamp) {
                    testCase.data
                }
            }
            expectThat(res) {
                get { status.code }.isEqualTo(testCase.wantHttpStatus)
                get { body }.and {
                    if (testCase.wantJson == null) {
                        isNull()
                    } else {
                        isEqualTo(testCase.wantJson)
                    }
                }
            }
        }
    }

    @TestFactory
    fun `encodes nothing results`() = listOf(
        testCase(
            name = "null data",
            data = null, // converted into NotFound
            wantJson = """{"status_code":1000,"status_message":"Success","timestamp":"2024-01-15T10:30:00Z"}""",
        ),
        testCase(
            name = "string",
            data = "test-string",
            wantJson = """{"status_code":1000,"status_message":"Success","timestamp":"2024-01-15T10:30:00Z"}""",
        ),
        testCase(
            name = "object",
            data = Credentials("token", "url", emptyList()),
            wantJson = """{"status_code":1000,"status_message":"Success","timestamp":"2024-01-15T10:30:00Z"}""",
        ),
    ).flatten().map { testCase ->
        DynamicTest.dynamicTest(testCase.name) {
            mapper = testCase.serializer
            val res = runBlocking {
                HttpRequest(HttpMethod.GET).respondNothing(fixedTimestamp) {
                    testCase.data
                }
            }
            expectThat(res) {
                get { status.code }.isEqualTo(testCase.wantHttpStatus)
                get { body }.and {
                    if (testCase.wantJson == null) {
                        isNull()
                    } else {
                        isEqualTo(testCase.wantJson)
                    }
                }
            }
        }
    }

    private data class TestCase<T>(
        val name: String,
        val data: T?,
        val wantJson: String?,
        val wantHttpStatus: Int,
        val serializer: OcpiSerializer,
    )

    private fun <T> testCase(
        name: String,
        data: T?,
        wantJson: String,
        wantHttpStatus: Int = 200,
    ) = OcpiSerializationRegistry.serializers.map {
        TestCase(
            name = "$name (${it.name})",
            data = data,
            wantJson = wantJson.trimMargin().lines().joinToString(""),
            wantHttpStatus = wantHttpStatus,
            serializer = it,
        )
    }
}
