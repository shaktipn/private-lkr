package com.suryadigital.leo.testUtils

import com.suryadigital.leo.rpc.LeoRPCError
import com.suryadigital.leo.rpc.LeoRPCResponse
import com.suryadigital.leo.rpc.LeoRPCResult
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.buildJsonObject
import java.lang.AssertionError
import kotlin.test.Test
import kotlin.test.assertFailsWith

class RPCErrorCodeTest {
    @Test
    fun testLeoRPCResultAssertErrorCodePositive() {
        val result: LeoRPCResult<Response, Error> = LeoRPCResult.error(Error.Case1())
        result.assertErrorCode("Case1")
    }

    @Test
    fun testLeoRPCResultAssertErrorCodeNegative() {
        val result: LeoRPCResult<Response, Error> =
            LeoRPCResult.response(
                Response(1L),
            )
        assertFailsWith<AssertionError> {
            result.assertErrorCode("Case1")
        }
    }

    @Test
    fun testLeoRPCResultAssertErrorCodeIncorrectErrorCode() {
        val result: LeoRPCResult<Response, Error> = LeoRPCResult.error(Error.Case1())
        assertFailsWith<AssertionError> {
            result.assertErrorCode("Case2")
        }
    }
}

private data class Response(val id: Long) : LeoRPCResponse {
    override fun toJson(): JsonObject =
        buildJsonObject {
            "id" to id
        }
}

sealed class Error : LeoRPCError {
    data class Case1(override val code: String = "Case1") : Error() {
        override fun toJson(): JsonObject =
            buildJsonObject {
                "code" to code
            }
    }
}
