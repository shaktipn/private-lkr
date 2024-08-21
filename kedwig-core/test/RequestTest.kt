package com.suryadigital.leo.kedwig

import com.jayway.jsonpath.JsonPath
import okhttp3.mockwebserver.MockResponse
import java.io.File
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertTrue

class RequestTest : MockWebServerTest() {
    @Test
    fun testDSL() {
        val actual =
            request {
                method = Method.POST
                path = "/v1/foo"
                headers {
                    "foo" to "bar"
                }
                queryParameters {
                    "baz" to "qux"
                }
                body("foo")
            }
        val expected =
            Request(
                method = Method.POST,
                path = "/v1/foo",
                headers = Headers(listOf(Header("foo", "bar"))),
                queryParameters = QueryParameters(listOf(QueryParameter("baz", "qux"))),
                body = "foo".toByteArray(),
            )
        assertTrue(expected.isEqualTo(actual))
        assertTrue(expected.isEqualTo(actual, true))
    }

    @Test
    fun testBodyEquation() {
        val url = server.url("/test-equation").toString()
        val client = OkHttpAPIClient(APIClientConfiguration())
        val reqBody =
            """
            {
                "foo": "bar"
            }
            """.trimIndent()
        server.enqueue(MockResponse().setBody(reqBody))
        val tmpFile = File.createTempFile("reqBody", null)
        tmpFile.writeText(reqBody)
        val req1 =
            request {
                method = Method.POST
                path = url
                bodyStream(reqBody.byteInputStream())
                headers {
                    "Content-Type" to "application/json"
                }
            }
        val req2 =
            request {
                method = Method.POST
                path = url
                bodyStream(tmpFile.inputStream())
                headers {
                    "Content-Type" to "application/json"
                }
            }
        assertTrue(req1.isEqualTo(req1, compareBody = true))
        assertTrue(req1.isEqualTo(req2, compareBody = true))
        val res1 = client.sendRequest(req1)
        // This should work, and return a proper response, because ByteArrayInputStream does support mark/reset
        assertEquals("bar", JsonPath.read(res1.stringBody, "$.foo"))
        assertFailsWith<NetworkException> {
            // This should fail, because FileInputStream does not support mark/reset
            client.sendRequest(req2)
        }
    }

    @Test
    fun testRequestBuilderExceptionWithBodyStreamAlreadyExisting() {
        val exception =
            assertFailsWith<RequestBuilderException> {
                request {
                    method = Method.POST
                    path = "url"
                    bodyStream("data".byteInputStream())
                    body("data2")
                    headers {
                        "Content-Type" to "application/json"
                    }
                }
            }
        assertEquals("Cannot set body as both a ByteArray and an InputStream", exception.message)
    }

    @Test
    fun testRequestBuilderExceptionWithBodyAlreadyExisting() {
        val exception =
            assertFailsWith<RequestBuilderException> {
                request {
                    method = Method.POST
                    path = "url"
                    body("data2")
                    bodyStream("data".byteInputStream())
                    headers {
                        "Content-Type" to "application/json"
                    }
                }
            }
        assertEquals("Cannot set body as both a ByteArray and an InputStream", exception.message)
    }
}
