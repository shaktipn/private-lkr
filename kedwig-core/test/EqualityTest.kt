package com.suryadigital.leo.kedwig

import okhttp3.HttpUrl.Companion.toHttpUrl
import java.io.ByteArrayInputStream
import java.io.File
import java.io.FileInputStream
import java.io.IOException
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertFalse
import kotlin.test.assertNotEquals
import kotlin.test.assertTrue

class EqualityTest {
    private val stringStreamOne: ByteArrayInputStream =
        "This is the string on which I will open the input stream".byteInputStream(Charsets.UTF_8)
    private val stringStreamTwo: ByteArrayInputStream =
        "This is the string on which I will open the input stream".byteInputStream(Charsets.UTF_8)
    private val stringStreamThree: ByteArrayInputStream =
        "This is the string on which I will open the input stream, but not equal to first or second".byteInputStream(
            Charsets.UTF_8,
        )
    private val googleURL = "https://www.google.com".toHttpUrl()
    private val defaultStreamingResponse =
        StreamingResponse(
            300,
            headers {
                "x" to "y"
                "y" to "z"
            },
            stringStreamOne,
            googleURL,
        )

    @Test
    fun assertEqualTextStream() {
        val firstEqualSecond = stringStreamOne.isEqualTo(stringStreamTwo)
        stringStreamOne.read()
        assertTrue(firstEqualSecond)
    }

    @Test
    fun assertNotEqualTextStream() {
        val firstEqualThird = stringStreamOne.isEqualTo(stringStreamThree)
        assertTrue(!firstEqualThird)
    }

    @Test
    fun assertEqualTextStreamsWithMarkSupport() {
        val firstInputStream = stringStreamOne.buffered()
        val thirdInputStream = stringStreamThree.buffered()
        val firstEqualThird = firstInputStream.isEqualTo(thirdInputStream, 100)
        // As the stream is marked, it won't be closed and reset to position 0, and we can perform read operation.
        assertTrue(thirdInputStream.read() != -1)
        assertTrue(!firstEqualThird)
    }

    @Test
    fun responseEqualityTest() {
        val response1 = defaultStreamingResponse
        val response2 =
            StreamingResponse(
                300,
                headers {
                    "x" to "y"
                    "y" to "z"
                },
                stringStreamTwo,
                googleURL,
            )
        assertTrue(response1.isEqualTo(response2, true))
    }

    @Test
    fun responseEqualityTestWithDifferentBody() {
        val response1 = defaultStreamingResponse
        val response2 =
            StreamingResponse(
                300,
                headers {
                    "x" to "y"
                    "y" to "z"
                },
                stringStreamThree,
                googleURL,
            )
        assertTrue(response1.isEqualTo(response2))
    }

    @Test
    fun responseEqualityTestWithSameRequest() {
        val response1 = defaultStreamingResponse
        assertTrue(response1.isEqualTo(response1))
        assertEquals("This is the string on which I will open the input stream", response1.stringBody)
        assertEquals(Cookies(), response1.cookies)
    }

    @Test
    fun responseEqualityTestFailWithDifferentStatusCode() {
        val response1 = defaultStreamingResponse
        val response2 =
            StreamingResponse(
                200,
                headers {
                    "x" to "y"
                    "y" to "z"
                },
                stringStreamOne,
                googleURL,
            )
        assertFalse(response1.isEqualTo(response2))
    }

    @Test
    fun responseEqualityTestFailWithDifferentHeaders() {
        val response1 = defaultStreamingResponse
        val response2 =
            StreamingResponse(
                300,
                headers {
                    "x" to "y"
                },
                stringStreamOne,
                googleURL,
            )
        assertFalse(response1.isEqualTo(response2))
    }

    @Test
    fun responseEqualityTestWithLoadAllFalse() {
        val response1 =
            StreamingResponse(
                200,
                headers {
                    "x" to "y"
                    "y" to "z"
                },
                stringStreamOne,
                googleURL,
            )
        val response2 =
            StreamingResponse(
                200,
                headers {
                    "x" to "y"
                    "y" to "z"
                },
                stringStreamOne,
                googleURL,
            )
        assertTrue(response1.isEqualTo(response2, compareBody = true, loadAll = false))
    }

    @Test
    fun responseCookieEqualityTest() {
        val response =
            StreamingResponse(
                200,
                headers {
                    "x" to "y"
                    "y" to "z"
                    "Set-Cookie" to "cookie1=value1"
                    "Set-Cookie" to "cookie2=value2"
                },
                stringStreamOne,
                googleURL,
            )
        assertTrue(response.cookies.getAll().isNotEmpty())
    }

    @Test
    fun equalFileInputStreamTest() {
        val file =
            File(
                this.javaClass.getResource("/test-input-file.txt")?.file
                    ?: throw IllegalStateException("File not found in testresources folder. Please make sure that the file exists."),
            )
        val firstInputStream = FileInputStream(file)
        val secondInputStream = FileInputStream(file)
        assertTrue(firstInputStream.isEqualTo(secondInputStream))
        // As file input stream does not support marking, it will be closed and throw exception if try to read
        val exception = assertFailsWith<IOException> { assertTrue(firstInputStream.read() != -1) }
        assertEquals("Stream Closed", exception.message)
    }

    @Test
    fun testRequestEqualityFailOnMethod() {
        val req1 =
            request {
                method = Method.POST
                path = "url"
                headers {
                    "Content-Type" to "application/json"
                }
                body("")
            }
        val req2 =
            request {
                method = Method.PUT
                path = "url"
                headers {
                    "Content-Type" to "application/json"
                }
                body("")
            }
        assert(!req1.isEqualTo(req2))
    }

    @Test
    fun testRequestEqualityFailOnPath() {
        val req1 =
            request {
                method = Method.POST
                path = "url"
                headers {
                    "Content-Type" to "application/json"
                }
                body("")
            }
        val req2 =
            request {
                method = Method.POST
                path = "differentUrl"
                headers {
                    "Content-Type" to "application/json"
                }
                body("")
            }
        assert(!req1.isEqualTo(req2))
    }

    @Test
    fun testRequestEqualityFailOnHeaders() {
        val req1 =
            request {
                method = Method.POST
                path = "url"
                headers {
                    "Content-Type" to "application/json"
                }
                body("")
            }
        val req2 =
            request {
                method = Method.POST
                path = "url"
                headers {
                    "Content-Type" to "application/text"
                }
                body("")
            }
        assert(!req1.isEqualTo(req2))
    }

    @Test
    fun testRequestEqualityFailOnQueryParameter() {
        val req1 =
            request {
                method = Method.POST
                path = "url"
                headers {
                    "Content-Type" to "application/json"
                }
                body("")
                queryParameters { "id" to "1" }
            }
        val req2 =
            request {
                method = Method.POST
                path = "url"
                headers {
                    "Content-Type" to "application/json"
                }
                body("")
            }
        assert(!req1.isEqualTo(req2))
    }

    @Test
    fun testRequestEqualityFailOnSocketTimeout() {
        val req1 =
            request {
                method = Method.POST
                path = "url"
                headers {
                    "Content-Type" to "application/json"
                }
                socketTimeoutMS = 1000
                body("")
            }
        val req2 =
            request {
                method = Method.POST
                path = "url"
                headers {
                    "Content-Type" to "application/json"
                }
                body("")
            }
        assert(!req1.isEqualTo(req2))
    }

    @Test
    fun testRequestEqualityFailOnConnectionTimeout() {
        val req1 =
            request {
                method = Method.POST
                path = "url"
                headers {
                    "Content-Type" to "application/json"
                }
                connectionTimeoutMS = 1000
                body("")
            }
        val req2 =
            request {
                method = Method.POST
                path = "url"
                headers {
                    "Content-Type" to "application/json"
                }
                body("")
            }
        assert(!req1.isEqualTo(req2))
    }

    @Test
    fun testRequestEqualityFailOnOmitDefaultHeaders() {
        val req1 =
            request {
                method = Method.POST
                path = "url"
                headers {
                    "Content-Type" to "application/json"
                }
                omitDefaultHeaders = true
                body("")
            }
        val req2 =
            request {
                method = Method.POST
                path = "url"
                headers {
                    "Content-Type" to "application/json"
                }
                body("")
            }
        assert(!req1.isEqualTo(req2))
    }

    @Test
    fun testRequestEqualityFailOnOmitDefaultQueryParameters() {
        val req1 =
            request {
                method = Method.POST
                path = "url"
                headers {
                    "Content-Type" to "application/json"
                }
                omitDefaultQueryParameters = true
                body("")
            }
        val req2 =
            request {
                method = Method.POST
                path = "url"
                headers {
                    "Content-Type" to "application/json"
                }
                body("")
            }
        assert(!req1.isEqualTo(req2))
    }

    @Test
    fun testRequestEqualityFailOnBodyNull() {
        val req1 =
            request {
                method = Method.DELETE
                path = "url"
                headers {
                    "Content-Type" to "application/json"
                }
            }
        val req2 =
            request {
                method = Method.DELETE
                path = "url"
                headers {
                    "Content-Type" to "application/json"
                }
                body("stuff")
            }
        assert(!req1.isEqualTo(req2, compareBody = true))
    }

    @Test
    fun testRequestEqualityFailOnBodyNullReverse() {
        val req1 =
            request {
                method = Method.DELETE
                path = "url"
                headers {
                    "Content-Type" to "application/json"
                }
                body("stuff")
            }
        val req2 =
            request {
                method = Method.DELETE
                path = "url"
                headers {
                    "Content-Type" to "application/json"
                }
            }
        assert(!req1.isEqualTo(req2, compareBody = true))
    }

    @Test
    fun testRequestEqualityFailOnBodyNotEqual() {
        val req1 =
            request {
                method = Method.POST
                path = "url"
                headers {
                    "Content-Type" to "application/json"
                }
                body("stuff")
            }
        val req2 =
            request {
                method = Method.POST
                path = "url"
                headers {
                    "Content-Type" to "application/json"
                }
                body("different stuff")
            }
        assert(!req1.isEqualTo(req2, compareBody = true))
    }

    @Test
    fun testRequestEqualityFailOnBodyStreamNull() {
        val req1 =
            request {
                method = Method.DELETE
                path = "url"
                headers {
                    "Content-Type" to "application/json"
                }
            }
        val req2 =
            request {
                method = Method.DELETE
                path = "url"
                headers {
                    "Content-Type" to "application/json"
                }
                bodyStream("stuff".byteInputStream())
            }
        assert(!req1.isEqualTo(req2, compareBody = true))
    }

    @Test
    fun testRequestEqualityFailOnBodyStreamNullReverse() {
        val req1 =
            request {
                method = Method.DELETE
                path = "url"
                headers {
                    "Content-Type" to "application/json"
                }
                bodyStream("stuff".byteInputStream())
            }
        val req2 =
            request {
                method = Method.DELETE
                path = "url"
                headers {
                    "Content-Type" to "application/json"
                }
            }
        assert(!req1.isEqualTo(req2, compareBody = true))
    }

    @Test
    fun testRequestEqualityFailOnBodyStreamNotEqual() {
        val req1 =
            request {
                method = Method.POST
                path = "url"
                headers {
                    "Content-Type" to "application/json"
                }
                bodyStream("stuff".byteInputStream())
            }
        val req2 =
            request {
                method = Method.POST
                path = "url"
                headers {
                    "Content-Type" to "application/json"
                }
                bodyStream("different stuff".byteInputStream())
            }
        assert(!req1.isEqualTo(req2, compareBody = true))
    }

    @Test
    fun testResponseEquality() {
        val response1 =
            Response(
                200,
                url = googleURL,
                headers =
                    headers {
                        "Content-Type" to "application/json"
                    },
                body = "body".toByteArray(),
            )
        val response2 =
            Response(
                200,
                url = googleURL,
                headers =
                    headers {
                        "Content-Type" to "application/json"
                    },
                body = "body".toByteArray(),
            )
        assertEquals(Cookies(), response1.cookies)
        assertEquals(response1, response2)
        assertEquals(response1, response1)
        assertEquals(response1.hashCode(), response2.hashCode())
        assertFalse(response1.equals(null))
    }

    @Test
    fun testResponseEqualityFailOnStatusCode() {
        val response1 =
            Response(
                200,
                url = googleURL,
                headers =
                    headers {
                        "Content-Type" to "application/json"
                    },
                body = "body".toByteArray(),
            )
        val response2 =
            Response(
                300,
                url = googleURL,
                headers =
                    headers {
                        "Content-Type" to "application/json"
                    },
                body = "body".toByteArray(),
            )
        assertNotEquals(response1, response2)
    }

    @Test
    fun testResponseEqualityFailOnHeader() {
        val response1 =
            Response(
                200,
                url = googleURL,
                headers =
                    headers {
                        "Content-Type" to "application/text"
                    },
                body = "body".toByteArray(),
            )
        val response2 =
            Response(
                200,
                url = googleURL,
                headers =
                    headers {
                        "Content-Type" to "application/json"
                    },
                body = "body".toByteArray(),
            )
        assertNotEquals(response1, response2)
    }

    @Test
    fun testResponseEqualityFailOnBody() {
        val response1 =
            Response(
                200,
                url = googleURL,
                headers =
                    headers {
                        "Content-Type" to "application/json"
                    },
                body = "body".toByteArray(),
            )
        val response2 =
            Response(
                200,
                url = googleURL,
                headers =
                    headers {
                        "Content-Type" to "application/json"
                    },
                body = "different body".toByteArray(),
            )
        assertNotEquals(response1, response2)
    }
}
