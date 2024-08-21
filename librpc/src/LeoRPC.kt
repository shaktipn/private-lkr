package com.suryadigital.leo.rpc

import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put

/**
 * Represents a class that can be serialized to JSON.
 */
interface JsonSerializable {
    /**
     * [JsonObject] representation of this object.
     *
     * @return [JsonObject] representation of this object.
     */
    fun toJson(): JsonObject
}

/**
 * Represents a LeoRPC Request.
 */
interface LeoRPCRequest : JsonSerializable

/**
 * Represents a LeoResponse returned from an LeoRPC.
 */
interface LeoRPCResponse : JsonSerializable

/**
 * Represents an error returned from an LeoRPC.
 *
 * All errors are required to have a [code].
 */
interface LeoRPCError : JsonSerializable {
    /**
     * LeoError code to identify the type of error.
     */
    val code: String

    /**
     * [JsonObject] representation of this error.
     *
     * @return [JsonObject] representation of this error.
     */
    override fun toJson(): JsonObject =
        buildJsonObject {
            put("code", code)
        }
}

/**
 * Represents the result of a LeoRPC.
 *
 * @param R [LeoRPCResponse] type.
 * @param E [LeoRPCError] type.
 */
sealed class LeoRPCResult<R : LeoRPCResponse, E : LeoRPCError> {
    /**
     * Represent the response of a LeoRPC.
     *
     * @param response object for the current LeoRPC.
     */
    data class LeoResponse<R : LeoRPCResponse, E : LeoRPCError>(val response: R) : LeoRPCResult<R, E>()

    /**
     * Represent the error thrown by the LeoRPC.
     *
     * @param error object for the current LeoRPC.
     */
    data class LeoError<R : LeoRPCResponse, E : LeoRPCError>(val error: E) : LeoRPCResult<R, E>()

    companion object {
        /**
         * Convenience function to create a [LeoResponse].
         *
         * @param R [LeoRPCResponse] type.
         * @param E [LeoRPCError] type.
         * @param response [LeoResponse] to return.
         * @return [LeoRPCResult] containing [response].
         */
        fun <R : LeoRPCResponse, E : LeoRPCError> response(response: R): LeoResponse<R, E> {
            return LeoResponse(response)
        }

        /**
         * Convenience function to create a [LeoError].
         *
         * @param R [LeoRPCResponse] type.
         * @param E [LeoRPCError] type.
         * @param error [LeoError] to return.
         * @return [LeoRPCResult] containing [error].
         */
        fun <R : LeoRPCResponse, E : LeoRPCError> error(error: E): LeoError<R, E> {
            return LeoError(error)
        }
    }
}

/**
 * Definition of a LeoRPC.
 *
 * @param Req [LeoRPCRequest] type.
 * @param Res [LeoRPCResponse] type.
 * @param Err [LeoRPCError] type.
 */
@Suppress("unused") // This is public API
interface LeoRPC<Req : LeoRPCRequest, Res : LeoRPCResponse, Err : LeoRPCError> {
    /**
     * Executes the LeoRPC.
     *
     * @param request request to send (i.e. input to LeoRPC).
     * @return result of executing LeoRPC (i.e. output of LeoRPC).
     */
    suspend fun execute(request: Req): LeoRPCResult<Res, Err>
}
