package com.suryadigital.leo.kedwig

import okhttp3.mockwebserver.MockResponse
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotEquals
import kotlin.test.assertNull

class HeadersTest : MockWebServerTest() {
    private val okHttpAPIClient =
        OkHttpAPIClient(
            configuration =
                APIClientConfiguration(
                    defaultHeaders =
                        headers {
                            "X-Header" to "Non-Required-Header"
                        },
                ),
        )
    private val headerList =
        Headers(
            listOf(
                Header("foo", "bar"),
                Header("blah", "baz"),
                Header("foo", "bar2"),
            ),
        )

    @Test
    fun testDSL() {
        val actualOne =
            headers {
                header("foo", "bar")
                "blah" to "baz"
            }
        val actualTwo =
            headers {
                header("foo", "bar")
                "blah" to "baz"
            }
        val expected =
            Headers(
                listOf(
                    Header("foo", "bar"),
                    Header("blah", "baz"),
                ),
            )
        val headerBuilder = HeadersBuilder()
        assertEquals(expected, actualOne)
        assertEquals(expected, expected)
        assertEquals(headerBuilder, headerBuilder)
        assertEquals(actualOne, actualTwo)
        assertEquals(actualOne.hashCode(), actualTwo.hashCode())
    }

    @Test
    fun testHeaderEqualityFail() {
        val headerOne = Header("foo", "bar")
        val headerTwo = Header("Fooo", "bar")
        assertEquals(headerOne, headerOne)
        assertFalse(headerOne.equals(null))
        assertEquals(3246093, headerOne.hashCode())
        assertNotEquals(headerOne, headerTwo)
    }

    @Test
    fun defaultHeaderTest() {
        server.enqueue(MockResponse())
        val url = server.url("/get").toString()
        okHttpAPIClient.sendRequest(
            request {
                method = Method.GET
                path = url
            },
        )
        val recordedRequest = server.takeRequest()
        assertEquals("Non-Required-Header", recordedRequest.getHeader("X-Header"))
    }

    @Test
    fun requestHeaderTest() {
        val okHttpAPIClient = OkHttpAPIClient(APIClientConfiguration())
        server.enqueue(MockResponse())
        val url = server.url("/get").toString()
        okHttpAPIClient.sendRequest(
            request {
                method = Method.GET
                path = url
                headers { "X-Random-Header" to "string" }
            },
        )
        assertEquals("string", server.takeRequest().getHeader("X-Random-Header"))
    }

    @Test
    fun requestHeaderWithDefaultHeaderTest() {
        server.enqueue(MockResponse())
        val url = server.url("/get-default-request").toString()
        okHttpAPIClient.sendRequest(
            request {
                method = Method.GET
                path = url
                headers { "X-Random-Header" to "string" }
            },
        )
        val recordedRequest = server.takeRequest()
        assertEquals("Non-Required-Header", recordedRequest.getHeader("X-Header"))
        assertEquals("string", recordedRequest.getHeader("X-Random-Header"))
    }

    @Test
    fun requestHeaderWithOmitDefaultHeaderTest() {
        server.enqueue(MockResponse())
        val url = server.url("/header-omit-default-true").toString()
        okHttpAPIClient.sendRequest(
            request {
                method = Method.GET
                path = url
                headers { "X-Random-Header" to "string" }
                omitDefaultHeaders = true
            },
        )
        assertEquals(null, server.takeRequest().getHeader("X-Header"))
    }

    @Test
    fun testGetFirst() {
        val result = headerList.getFirst("foo")
        assertEquals(Header("foo", "bar"), result)
    }

    @Test
    fun testGetFirstReturningNull() {
        val result = headerList.getFirst("bar")
        assertNull(result)
    }

    @Test
    fun testGetFirstValue() {
        val result = headerList.getFirstValue("foo")
        assertEquals("bar", result)
    }

    @Test
    fun testGetFirstValueReturningNull() {
        val result = headerList.getFirstValue("bar")
        assertNull(result)
    }

    @Test
    fun testGet() {
        val result = headerList["foo"]
        assertEquals(listOf(Header("foo", "bar"), Header("foo", "bar2")), result)
    }

    @Test
    fun testGetValues() {
        val result = headerList.getValues("foo")
        assertEquals(listOf("bar", "bar2"), result)
    }

    @Test
    fun testGetAll() {
        val result = headerList.getAll()
        assertEquals(listOf(Header("foo", "bar"), Header("blah", "baz"), Header("foo", "bar2")), result)
    }

    @Test
    fun testGetAllValues() {
        val result = headerList.getAllValues()
        assertEquals(listOf("bar", "baz", "bar2"), result)
    }

    @Test
    fun testGetAllNames() {
        val result = headerList.getAllNames()
        assertEquals(listOf("foo", "blah", "foo"), result)
    }

    @Test
    fun testHeadersEquality() {
        val headersOne = Headers(listOf(Header("foo", "bar")))
        val headersTwo = Headers(listOf(Header("foo", "bar")))
        val headersThree = Headers(listOf(Header("foo", "bar"), Header("foo", "bar2")))
        assertEquals(headersOne, headersTwo)
        assertEquals(headersOne.hashCode(), headersTwo.hashCode())
        assertNotEquals(headersOne, headersThree)
        assertFalse(headersOne.equals(null))
        assertNotEquals(headersOne.hashCode(), headersThree.hashCode())
    }
}
