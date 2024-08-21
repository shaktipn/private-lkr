package com.suryadigital.leo.kedwig

import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import kotlin.test.AfterTest
import kotlin.test.BeforeTest

abstract class MockWebServerTest {
    val server: MockWebServer = MockWebServer()

    @BeforeTest
    fun startServer() {
        server.start()
    }

    @AfterTest
    fun tearDown() {
        server.shutdown()
    }

    fun createRequest(method: Method): Request {
        server.enqueue(MockResponse().setBody("success"))
        val url = server.url("/${method.name}").toString()
        return request {
            this.method = method
            path = url
        }
    }

    fun createRequestWithBody(method: Method): Request {
        val expectedUser =
            """
            {
                "name": "prakash", 
                "email": "sharmac@c.gmail"
            }
            """.trimIndent()
        server.enqueue(MockResponse().setBody(expectedUser).addHeader("Content-Type:application/json"))
        val url = server.url("/${method.name}").toString()
        return request {
            this.method = method
            path = url
            body(expectedUser)
        }
    }
}
