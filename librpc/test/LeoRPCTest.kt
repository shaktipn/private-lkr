package com.suryadigital.leo.rpc

import kotlinx.coroutines.runBlocking
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.buildJsonObject
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

// Suppress KotlinConstantConditions is added due to false positive for statement `result is LeoRPCResult.LeoResponse`
@Suppress("KotlinConstantConditions")
class LeoRPCTest {
    @Test
    fun testLeoRPCResultResponse() {
        val result: LeoRPCResult<Response, Error> = LeoRPCResult.response(Response(1L))
        assertTrue(result is LeoRPCResult.LeoResponse)
        assertEquals(1, result.response.id)
    }

    @Test
    fun testLeoRPCResultError() {
        val result: LeoRPCResult<Response, Error> = LeoRPCResult.error(Error.Case1())
        assertTrue(result is LeoRPCResult.LeoError)
        assertEquals("{\"code\":\"Case1\"}", result.error.toJson().toString())
        assertEquals("Case1", result.error.code)
    }

    @Test
    fun leoPGRPCHookTest() {
        class PGRPCHook : LeoPGRPCHook() {
            var errorCode = ""
            var currentRequest = Request(id = 1)

            override suspend fun onError(code: String) {
                errorCode = code
            }

            override suspend fun onSuccess(request: LeoRPCRequest) {
                currentRequest = request as Request
            }
        }
        val pgrpcHook = PGRPCHook()
        runBlocking {
            assertEquals("", pgrpcHook.errorCode)
            pgrpcHook.onError("error")
            assertEquals("error", pgrpcHook.errorCode)
            assertEquals(1, pgrpcHook.currentRequest.id)
            pgrpcHook.onSuccess(Request(id = 2))
            assertEquals(2, pgrpcHook.currentRequest.id)
        }
    }
}

private data class Response(
    val id: Long,
) : LeoRPCResponse {
    override fun toJson(): JsonObject =
        buildJsonObject {
            "id" to id
        }
}

private data class Request(
    val id: Long,
) : LeoRPCRequest {
    override fun toJson(): JsonObject =
        buildJsonObject {
            "id" to id
        }
}

sealed class Error : LeoRPCError {
    data class Case1(override val code: String = "Case1") : Error()
}
