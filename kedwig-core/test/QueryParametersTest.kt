package com.suryadigital.leo.kedwig

import okhttp3.mockwebserver.MockResponse
import kotlin.test.Test
import kotlin.test.assertEquals

class QueryParametersTest : MockWebServerTest() {
    private val queryList =
        QueryParameters(
            listOf(
                QueryParameter("foo", "bar"),
                QueryParameter("blah", "baz"),
                QueryParameter("foo", "bar2"),
            ),
        )
    private val okHttpAPIClient: OkHttpAPIClient =
        OkHttpAPIClient(
            APIClientConfiguration(
                defaultQueryParameters =
                    queryParameters {
                        "param1" to "xyz"
                        "param2" to "pqr"
                    },
            ),
        )

    @Test
    fun testDSL() {
        val actual =
            queryParameters {
                queryParameter("foo", "bar")
                "blah" to "baz"
            }
        val actual2 =
            queryParameters {
                queryParameter("foo", "bar")
                "blah" to "baz"
            }
        val expected =
            QueryParameters(
                listOf(
                    QueryParameter("foo", "bar"),
                    QueryParameter("blah", "baz"),
                ),
            )
        val queryParameterBuilder = QueryParametersBuilder()
        assertEquals(expected, actual)
        assertEquals(actual.hashCode(), actual2.hashCode())
        assertEquals(queryParameterBuilder, queryParameterBuilder)
    }

    @Test
    fun defaultQueryParamTest() {
        server.enqueue(MockResponse())
        val url = server.url("/default-query").toString()
        okHttpAPIClient.sendRequest(
            request {
                method = Method.GET
                path = url
            },
        )
        val recordedPath = server.takeRequest().path!!
        val uri = getQueryList(recordedPath)
        assertEquals(uri[0], "param1=xyz")
        assertEquals(uri[1], "param2=pqr")
    }

    @Test
    fun requestQueryParamTest() {
        server.enqueue(MockResponse())
        val url = server.url("/default-query").toString()
        val okHttpAPIClient = OkHttpAPIClient(APIClientConfiguration())
        okHttpAPIClient.sendRequest(
            request {
                method = Method.GET
                path = url
                queryParameters {
                    "param1" to "xyz"
                    "param2" to "pqr"
                }
            },
        )
        val recordedPath = server.takeRequest().path!!
        val uri = getQueryList(recordedPath)
        assertEquals(uri[0], "param1=xyz")
        assertEquals(uri[1], "param2=pqr")
    }

    @Test
    fun requestQueryParamWithOmitDefaultParamTest() {
        server.enqueue(MockResponse())
        val url = server.url("/omit-default-query").toString()
        okHttpAPIClient.sendRequest(
            request {
                method = Method.GET
                path = url
                omitDefaultQueryParameters = true
                queryParameters {
                    "param1" to "xyz"
                    "param2" to "pqr"
                }
            },
        )
        val recordedPath = server.takeRequest().path!!
        val uri = getQueryList(recordedPath)
        assertEquals(uri[0], "param1=xyz")
        assertEquals(uri[1], "param2=pqr")
    }

    // Just to remove the warning of byteArrayBody in response
    @Test
    fun requestToDemonstrateByteArrayBodyUsage() {
        server.enqueue(MockResponse().setBody("success!"))
        val url = server.url("/simple-test").toString()
        val response =
            okHttpAPIClient.sendRequest(
                request {
                    method = Method.GET
                    path = url
                },
            )
        assertEquals(response.stringBody, "success!")
    }

    @Test
    fun testGetFirst() {
        val result = queryList.getFirst("foo")
        assertEquals(QueryParameter("foo", "bar"), result)
    }

    @Test
    fun testGetFirstWithNoMatch() {
        val result = queryList.getFirst("no-match")
        assertEquals(null, result)
    }

    @Test
    fun testGetFirstValue() {
        val result = queryList.getFirstValue("foo")
        assertEquals("bar", result)
    }

    @Test
    fun testGetFirstValueWithNoMatch() {
        val result = queryList.getFirstValue("no-match")
        assertEquals(null, result)
    }

    @Test
    fun testGet() {
        val result = queryList["foo"]
        assertEquals(2, result.size)
        assertEquals(QueryParameter("foo", "bar"), result[0])
        assertEquals(QueryParameter("foo", "bar2"), result[1])
    }

    @Test
    fun getWithNoMatch() {
        val result = queryList["no-match"]
        assertEquals(0, result.size)
    }

    @Test
    fun testGetValues() {
        val result = queryList.getValues("foo")
        assertEquals(2, result.size)
        assertEquals("bar", result[0])
        assertEquals("bar2", result[1])
    }

    @Test
    fun testGetValuesWithNoMatch() {
        val result = queryList.getValues("no-match")
        assertEquals(0, result.size)
    }

    @Test
    fun testGetAll() {
        val result = queryList.getAll()
        assertEquals(3, result.size)
        assertEquals(QueryParameter("foo", "bar"), result[0])
        assertEquals(QueryParameter("blah", "baz"), result[1])
        assertEquals(QueryParameter("foo", "bar2"), result[2])
    }

    @Test
    fun testGetAllValues() {
        val result = queryList.getAllValues()
        assertEquals(3, result.size)
        assertEquals("bar", result[0])
        assertEquals("baz", result[1])
        assertEquals("bar2", result[2])
    }

    @Test
    fun testGetAllNames() {
        val result = queryList.getAllNames()
        assertEquals(3, result.size)
        assertEquals("foo", result[0])
        assertEquals("blah", result[1])
        assertEquals("foo", result[2])
    }
}

internal fun getQueryList(query: String): List<String> {
    val queryList = mutableListOf<String>()
    val params = query.split("?")[1].split("&")
    for (param in params) {
        val name = param.split("=")[0]
        val value = param.split("=")[1]
        queryList.add("$name=$value")
    }
    return queryList
}
