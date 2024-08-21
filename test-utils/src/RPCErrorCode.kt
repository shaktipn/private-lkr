package com.suryadigital.leo.testUtils

import com.suryadigital.leo.rpc.LeoRPCError
import com.suryadigital.leo.rpc.LeoRPCResponse
import com.suryadigital.leo.rpc.LeoRPCResult
import kotlin.test.assertEquals
import kotlin.test.fail

/**
 * Helper function that can be called on a [LeoRPCResult] to assert that the RPC failed with an [expected] error code.
 *
 * @param expected error code due to which the RPC is supposed to fail.
 */
fun <R : LeoRPCResponse, E : LeoRPCError> LeoRPCResult<R, E>.assertErrorCode(expected: String) {
    when (this) {
        is LeoRPCResult.LeoError -> assertEquals(expected, this.error.code)
        is LeoRPCResult.LeoResponse -> fail(message = "Request returned response")
    }
}
